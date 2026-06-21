package com.carpool.controller.dto.office;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с данными запрашиваемого офиса")
public record OfficeResponse(
        @Schema(description = "ID офиса", example = "1")
        Long id,
        @Schema(description = "Название офиса", example = "Колл-центр")
        String name,
        @Schema(description = "Город", example = "Москва")
        String city,
        @Schema(description = "Адрес офиса", example = "Тверская ул. 1")
        String address,
        @Schema(description = "Координаты офиса")
        double[] location
) {
}