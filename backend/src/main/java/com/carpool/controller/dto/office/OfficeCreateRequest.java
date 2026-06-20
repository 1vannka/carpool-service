package com.carpool.controller.dto.office;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OfficeCreateRequest(
        @NotBlank(message = "Название офиса обязательно")
        @Size(max = 255, message = "Название слишком длинное")
        String name,

        @NotBlank(message = "Город обязателен")
        @Size(max = 100, message = "Название города слишком длинное")
        String city,

        @NotBlank(message = "Адрес обязателен")
        @Size(max = 255, message = "Адрес слишком длинный")
        String address,

        @NotNull(message = "Координаты обязательны")
        @Size(min = 2, max = 2, message = "Локация должна содержать координаты")
        double[] location
) {}