package com.carpool.integration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

@Epic("Управление поездками")
@Feature("Гео-поиск и бронирование")
@DisplayName("Интеграционные тесты логики поездок (PostGIS + Redis)")
class TripIntegrationTest extends AbstractIntegrationTest {

    private static final String PASSWORD = "password123";
    private static final String ROUTE_PATH = "[[30.3158, 59.9390], [30.3200, 59.9450]]";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void createTestOffice() {
        jdbcTemplate.update("""
                INSERT INTO offices (id, name, city, address, location)
                VALUES (1, 'Test Office', 'Test City', 'Test Address',
                        ST_SetSRID(ST_Point(30.3158, 59.9390), 4326))
                ON CONFLICT (id) DO NOTHING
                """);
    }

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE trips, ride_requests, users RESTART IDENTITY CASCADE");
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
        @Story("Создание поездки")
        @DisplayName("Успешное создание поездки водителем с валидным маршрутом")
    void shouldCreateTripSuccessfully() {
        String accessToken = registerTestUser("driver.create@company.com");

        given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(tripRequestBody(getValidTime()))
                .when()
                .post("/api/trips")
                .then()
                .log().all()
                .statusCode(201)
                .body("status", equalTo("CREATED"))
                .body("routePath", hasSize(2));
    }

    @Test
        @Story("Умный матчинг")
        @DisplayName("Поиск поездки через ST_DWithin (успешный сценарий)")
    void shouldFindMatchingTripsViaPostGis() {
        String passengerToken = registerTestUser("passenger.matching@company.com");
        String driverToken = registerTestUser("driver.matching@company.com");
        String validTime = getValidTime();

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "officeId": 1,
                          "targetTime": "%s",
                          "toleranceTime": 30,
                          "pickupLocation": [30.3158, 59.9390]
                        }
                        """.formatted(validTime))
                .when()
                .post("/api/ride-requests")
                .then()
                .log().all()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .contentType(ContentType.JSON)
                .body(tripRequestBody(getValidTime()))
                .when()
                .post("/api/trips")
                .then()
                .log().all()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .when()
                .get("/api/trips/matching")
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].driverFirstName", not(emptyOrNullString()));
    }

    @Test
        @Story("Умный матчинг")
        @DisplayName("Отсутствие поездок при посадке вне радиуса маршрута")
    void shouldNotMatchWhenOutsideRadius() {
        String passengerToken = registerTestUser("passenger.radius@company.com");
        String driverToken = registerTestUser("driver.radius@company.com");
        String validTime = getValidTimePlus(0);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "officeId": 1,
                          "targetTime": "%s",
                          "toleranceTime": 30,
                          "pickupLocation": [30.4000, 59.9900]
                        }
                        """.formatted(validTime))
                .when()
                .post("/api/ride-requests")
                .then()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .contentType(ContentType.JSON)
                .body(tripRequestBody(validTime))
                .when()
                .post("/api/trips")
                .then()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .when()
                .get("/api/trips/matching")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
        @Story("Умный матчинг")
        @DisplayName("Отсутствие поездок при превышении допустимого временного интервала")
    void shouldNotMatchWhenTimeOutsideTolerance() {
        String passengerToken = registerTestUser("passenger.time@company.com");
        String driverToken = registerTestUser("driver.time@company.com");
        String passengerTime = getValidTimePlus(0);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "officeId": 1,
                          "targetTime": "%s",
                          "toleranceTime": 30,
                          "pickupLocation": [30.3158, 59.9390]
                        }
                        """.formatted(passengerTime))
                .when()
                .post("/api/ride-requests")
                .then()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .contentType(ContentType.JSON)
                .body(tripRequestBody(getValidTimePlus(4)))
                .when()
                .post("/api/trips")
                .then()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .when()
                .get("/api/trips/matching")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
        @Story("Бронирование поездки")
        @DisplayName("Одобрение пассажира водителем и уменьшение числа свободных мест")
    void shouldApprovePassengerAndDecreaseSeats() {
        String driverToken = registerTestUser("driver.approval@company.com");
        String passengerToken = registerTestUser("passenger.approval@company.com");
        String validTime = getValidTimePlus(0);

        Response tripResponse = given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .contentType(ContentType.JSON)
                .body(tripRequestBody(validTime))
                .when()
                .post("/api/trips")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .response();

        String tripId = tripResponse.path("id").toString();

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "officeId": 1,
                          "targetTime": "%s",
                          "toleranceTime": 30,
                          "pickupLocation": [30.3158, 59.9390]
                        }
                        """.formatted(validTime))
                .when()
                .post("/api/ride-requests")
                .then()
                .statusCode(201);

        given()
                .port(port)
                .header("Authorization", "Bearer " + passengerToken)
                .when()
                .post("/api/trips/{tripId}/join", tripId)
                .then()
                .log().all()
                .statusCode(201);

        Response passengersResponse = given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .when()
                .get("/api/trips/{tripId}/passengers", tripId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        String passengerId = passengersResponse.path("[0].passengerId").toString();

        given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .when()
                .post("/api/trips/{tripId}/passengers/{passengerId}/approve", tripId, passengerId)
                .then()
                .log().all()
                .statusCode(200);

        given()
                .port(port)
                .header("Authorization", "Bearer " + driverToken)
                .when()
                .get("/api/trips/my-active")
                .then()
                .log().all()
                .statusCode(200)
                .body("availableSeats", equalTo(2));
    }

    private String registerTestUser(String email) {
        Response response = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "%s",
                          "firstName": "Integration",
                          "lastName": "User"
                        }
                        """.formatted(email, PASSWORD))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.path("accessToken");
    }

    private String tripRequestBody(String time) {
        return """
                {
                  "officeId": 1,
                  "departureTime": "%s",
                  "estimatedDuration": 45,
                  "totalSeats": 3,
                  "carModel": "Kia Rio",
                  "carColor": "White",
                  "carPlate": "A123AA77",
                  "routePath": %s
                }
                """.formatted(time, ROUTE_PATH);
    }

    private String getValidTime() {
        return java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                .plusHours(2)
                .truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
                .toString();
    }

    private String getValidTimePlus(long hours) {
        return java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC)
                .plusHours(2 + hours)
                .truncatedTo(java.time.temporal.ChronoUnit.SECONDS)
                .toString();
    }
}