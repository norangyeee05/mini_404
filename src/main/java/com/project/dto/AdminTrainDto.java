package com.project.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.project.domain.Train;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AdminTrainDto {
    private AdminTrainDto() { }

    public record Request(@NotNull Long lineId, @NotBlank String trainNo, @NotBlank String section,
            @NotBlank String driver, @NotNull com.project.domain.TrainDirection direction,
            @NotBlank String status, Boolean active, LocalDate registeredAt, LocalDate inspectionDate) { }

    public record Row(Long trainId, String lineName, String status, String section, String trainNo,
            String driver, String direction, boolean active, LocalDateTime updatedAt,
            LocalDate registeredAt, LocalDate inspectionDate) {
        public static Row from(Train train) {
            String status = switch (train.getStatus()) {
                case NORMAL -> "RUNNING";
                case MAINTENANCE -> "MAINTENANCE";
                case FAULT -> "BROKEN";
            };
            String lineName = train.getLine() == null ? "7호선" : train.getLine().getName();
            LocalDateTime updated = train.getUpdatedAt() != null ? train.getUpdatedAt()
                    : train.getRegisteredAt() == null ? null : train.getRegisteredAt().atStartOfDay();
            return new Row(train.getId(), lineName, status, train.getRoute(), train.getTrainNo(), train.getDriver(),
                    train.getDirection().name(), train.isActive(), updated, train.getRegisteredAt(), train.getInspectionDate());
        }
    }

    public record ListResponse(long totalCount, int page, int size, List<Row> items) { }
}
