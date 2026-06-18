package com.carpool.controller.common;

public interface ResponseMapper<D, RES> {
    RES toDto(D domain);
}
