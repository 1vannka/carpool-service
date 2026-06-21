package com.carpool.controller.common;

public interface RequestMapper<REQ, D> {
    D toDomain(REQ request);
}