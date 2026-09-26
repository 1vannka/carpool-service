package com.carpool.integration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Epic("Интеграционное окружение")
@Feature("Запуск Spring-контекста")
@DisplayName("Проверка поднятия тестового окружения")
public class ContextLoadsTest extends AbstractIntegrationTest {

    @Test
    @Story("Успешный запуск приложения")
    @DisplayName("Spring-контекст успешно поднимается с PostgreSQL/PostGIS и Redis")
    void contextLoads() {

    }
}