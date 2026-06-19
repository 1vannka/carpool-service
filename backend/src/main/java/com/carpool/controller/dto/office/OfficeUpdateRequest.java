package com.carpool.controller.dto.office;

public record OfficeUpdateRequest(
        String name,
        String city,
        String address,
        double[] location
) {
}