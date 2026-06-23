package com.carpool.controller.common;

public interface BiResponseMapper<D1, D2, RES> {
    RES toDetailedDto(D1 domain1, D2 domain2);
}