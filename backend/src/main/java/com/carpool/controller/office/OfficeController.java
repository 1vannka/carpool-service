package com.carpool.controller.office;

import com.carpool.controller.dto.office.OfficeCreateRequest;
import com.carpool.controller.dto.office.OfficeResponse;
import com.carpool.controller.dto.office.OfficeUpdateRequest;
import com.carpool.domain.model.office.Office;
import com.carpool.domain.service.OfficeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/offices")
public class OfficeController {

    private final OfficeService officeService;
    private final OfficeWebMapper officeWebMapper;

    public OfficeController(OfficeService officeService, OfficeWebMapper officeWebMapper) {
        this.officeService = officeService;
        this.officeWebMapper = officeWebMapper;
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> getOffices(
            @RequestParam(required = false) String city) {

        List<OfficeResponse> offices = officeService.getOffices(city).stream()
                .map(officeWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(offices);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OfficeResponse> createOffice(@RequestBody OfficeCreateRequest request) {
        Office officeToCreate = officeWebMapper.toDomain(request);
        Office createdOffice = officeService.createOffice(officeToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(officeWebMapper.toDto(createdOffice));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OfficeResponse> updateOffice(
            @PathVariable Long id,
            @RequestBody OfficeUpdateRequest request) {
        Office officeUpdateData = officeWebMapper.toDomain(request);
        Office updatedOffice = officeService.updateOffice(id, officeUpdateData);
        return ResponseEntity.ok(officeWebMapper.toDto(updatedOffice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }
}