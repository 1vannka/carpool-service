package com.carpool.controller.office;

import com.carpool.controller.dto.office.OfficeCreateRequest;
import com.carpool.controller.dto.office.OfficeResponse;
import com.carpool.controller.dto.office.OfficeUpdateRequest;
import com.carpool.domain.model.office.Office;
import com.carpool.domain.service.OfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/offices")
@Tag(name = "Offices", description = "Справочник офисов")
public class OfficeController {

    private final OfficeService officeService;
    private final OfficeWebMapper officeWebMapper;

    public OfficeController(OfficeService officeService, OfficeWebMapper officeWebMapper) {
        this.officeService = officeService;
        this.officeWebMapper = officeWebMapper;
    }

    @GetMapping
    @Operation(summary = "Получить список офисов", description = "С возможностью фильтрации по названию города")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список получен"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<OfficeResponse>> getOffices(
            @RequestParam(required = false) String city) {

        List<OfficeResponse> offices = officeService.getOffices(city).stream()
                .map(officeWebMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(offices);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить офис", description = "Только для роли ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Офис создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не админ)")
    })
    public ResponseEntity<OfficeResponse> createOffice(@Valid @RequestBody OfficeCreateRequest request) {
        Office officeToCreate = officeWebMapper.toDomain(request);
        Office createdOffice = officeService.createOffice(officeToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(officeWebMapper.toDto(createdOffice));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить офис", description = "Только для роли ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Офис обновлен"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не админ)"),
            @ApiResponse(responseCode = "404", description = "Офис не найден")
    })
    public ResponseEntity<OfficeResponse> updateOffice(
            @PathVariable Long id,
            @Valid @RequestBody OfficeUpdateRequest request) {
        Office officeUpdateData = officeWebMapper.toDomain(request);
        Office updatedOffice = officeService.updateOffice(id, officeUpdateData);
        return ResponseEntity.ok(officeWebMapper.toDto(updatedOffice));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить офис", description = "Только для роли ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Офис удален"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (не админ)"),
            @ApiResponse(responseCode = "404", description = "Офис не найден (если выброшено исключение в сервисе)")
    })
    public ResponseEntity<Void> deleteOffice(@PathVariable Long id) {
        officeService.deleteOffice(id);
        return ResponseEntity.noContent().build();
    }
}