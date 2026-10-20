package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public final class AdminRailwayStationDto {
    private AdminRailwayStationDto() { }

    public record Form(
            @NotBlank @Size(max = 20) String stationNo,
            @NotBlank @Size(max = 100) String stationName,
            @Size(max = 100) String nameEn,
            @PositiveOrZero int stationOrder,
            @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") double longitude,
            @Size(max = 300) String address,
            @Size(max = 500) String facilities,
            @Size(max = 100) String transferLine,
            @Size(max = 500) String transferInfo,
            @Size(max = 50) String staffName,
            @Size(max = 30) String phone,
            @Size(max = 500) String usageInfo,
            LocalDate registeredAt,
            LocalDate inspectionDate,
            String facilityStatus,
            LocalDate lastInspectionDate,
            LocalDate nextInspectionDueDate,
            Boolean active) { }

    public record ListItem(Long stationId, String stationNo, String stationName, String nameEn,
                           int stationOrder, double latitude, double longitude, String address,
                           String facilities, String transferLine, String transferInfo, boolean active,
                           String facilityStatus, String staffName, String phone, String usageInfo,
                           LocalDate registeredAt, LocalDate inspectionDate, LocalDate lastInspectionDate,
                           LocalDate nextInspectionDueDate) { }

    public record ListResponse(long totalCount, int page, int size, List<ListItem> items) { }

    public record ActiveUpdate(@NotNull Boolean active) { }
}
