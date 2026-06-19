package com.carpool.domain.service.impl;

import com.carpool.domain.exception.OfficeNotFoundException;
import com.carpool.domain.model.office.Office;
import com.carpool.domain.repository.OfficeRepositoryPort;
import com.carpool.domain.service.OfficeService;
import java.util.List;

public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepositoryPort officeRepositoryPort;

    public OfficeServiceImpl(OfficeRepositoryPort officeRepositoryPort) {
        this.officeRepositoryPort = officeRepositoryPort;
    }

    @Override
    public Office createOffice(Office office) {
        return officeRepositoryPort.save(office);
    }

    @Override
    public Office updateOffice(Long id, Office updatedData) {
        Office existingOffice = officeRepositoryPort.findById(id)
                .orElseThrow(() -> new OfficeNotFoundException("Офис с ID " + id + " не найден"));

        existingOffice.setName(updatedData.getName());
        existingOffice.setCity(updatedData.getCity());
        existingOffice.setAddress(updatedData.getAddress());
        existingOffice.setLocation(updatedData.getLocation());

        return officeRepositoryPort.save(existingOffice);
    }

    @Override
    public List<Office> getAllOffices() {
        return officeRepositoryPort.findAll();
    }

    @Override
    public void deleteOffice(Long id) {
        officeRepositoryPort.deleteById(id);
    }
}