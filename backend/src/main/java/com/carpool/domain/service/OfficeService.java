package com.carpool.domain.service;

import com.carpool.domain.model.office.Office;
import java.util.List;

public interface OfficeService {
    Office createOffice(Office office);
    Office updateOffice(Long id, Office updatedData);
    List<Office> getOffices(String city);
    void deleteOffice(Long id);
}