package com.carpool.controller.dto.office;

public record OfficeResponse(
        Long id,
        String name,
        String city,
        String address,
        double[] location
) {
}