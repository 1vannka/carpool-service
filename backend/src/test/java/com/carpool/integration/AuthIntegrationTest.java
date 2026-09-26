package com.carpool.integration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

@Epic("Аутентификация пользователей")
@Feature("Регистрация, JWT и управление сессией")
@DisplayName("Интеграционные тесты аутентификации")
class AuthIntegrationTest extends AbstractIntegrationTest {

    private static final String EMAIL = "integration.user@company.com";
    private static final String PASSWORD = "password123";

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void cleanup() {
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
        @Story("Регистрация и вход")
        @DisplayName("Успешная регистрация пользователя и последующая аутентификация")
    void shouldRegisterAndLoginUser() {
        String[] tokens = registerTestUser(EMAIL);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(EMAIL, PASSWORD))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", matchesPattern("^[^.]+\\.[^.]+\\.[^.]+$"));
    }

    @Test
        @Story("Обновление JWT")
        @DisplayName("Успешное обновление access-токена через refresh-токен")
    void shouldRefreshTokensSuccessfully() {
        String[] tokens = registerTestUser(EMAIL);
        String refreshToken = tokens[1];

        Response refreshResponse = given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "refreshToken": "%s"
                        }
                        """.formatted(refreshToken))
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String refreshedAccessToken = refreshResponse.path("accessToken");
        String refreshedRefreshToken = refreshResponse.path("refreshToken");
        assertThat(refreshedAccessToken, not(emptyOrNullString()));
        assertThat(refreshedRefreshToken, equalTo(refreshToken));
    }

    @Test
        @Story("Выход из системы")
        @DisplayName("Инвалидация refresh-токена после выхода пользователя")
    void shouldInvalidateTokenOnLogout() {
        String[] tokens = registerTestUser(EMAIL);
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        given()
                .port(port)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "refreshToken": "%s"
                        }
                        """.formatted(refreshToken))
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(204);

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "refreshToken": "%s"
                        }
                        """.formatted(refreshToken))
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(400);
    }

    private String[] registerTestUser(String email) {
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
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()))
                .extract()
                .response();

        return new String[]{
                response.path("accessToken"),
                response.path("refreshToken")
        };
    }
}