package com.carpool.controller.dto.office;

public record OfficeCreateRequest(
        String name,
        String city,
        String address,
        double[] location
) {
}