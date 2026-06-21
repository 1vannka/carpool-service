package com.carpool.controller.dto.office;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на добавление нового офиса")
public record OfficeCreateRequest(
        @NotBlank(message = "Название офиса обязательно")
        @Size(max = 255, message = "Название слишком длинное")
        @Schema(description = "Название", example = "Колл-центр")
        String name,

        @NotBlank(message = "Город обязателен")
        @Size(max = 100, message = "Название города слишком длинное")
        @Schema(description = "Город", example = "Москва")
        String city,

        @NotBlank(message = "Адрес обязателен")
        @Size(max = 255, message = "Адрес слишком длинный")
        @Schema(description = "Адрес офиса", example = "Тверская ул. 1")
        String address,

        @NotNull(message = "Координаты обязательны")
        @Size(min = 2, max = 2, message = "Локация должна содержать координаты")
        @Schema(description = "Координаты офиса")
        double[] location
) {}