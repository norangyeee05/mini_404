package com.project.service;

import com.project.common.BusinessException;
import com.project.common.SearchUtil;
import com.project.domain.RailwayLine;
import com.project.domain.RailwayStation;
import com.project.domain.Station;
import com.project.dto.AdminRailwayStationDto.Form;
import com.project.dto.AdminRailwayStationDto.ListItem;
import com.project.dto.AdminRailwayStationDto.ListResponse;
import com.project.repository.RailwayLineRepository;
import com.project.repository.RailwayStationRepository;
import com.project.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import com.project.domain.OperationStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRailwayStationService {
    private final RailwayLineRepository lineRepository;
    private final RailwayStationRepository stationRepository;
    private final StationRepository legacyStationRepository;

    public ListResponse list(int page, int size, String searchType, String keyword) {
        RailwayLine line = line();
        int safeSize = Math.min(Math.max(size, 1), 100);
        String safeKeyword = SearchUtil.isBlank(keyword) ? "" : keyword.trim();
        String pattern = safeKeyword.isEmpty() ? "" : SearchUtil.likePattern(safeKeyword);
        Page<RailwayStation> result = stationRepository.findAdminPage(line.getId(),
                searchType == null ? "" : searchType, safeKeyword, pattern,
                PageRequest.of(Math.max(page, 1) - 1, safeSize));
        return new ListResponse(result.getTotalElements(), result.getNumber() + 1, result.getSize(),
                result.getContent().stream().map(this::item).toList());
    }

    public ListItem get(Long id) { return item(find(id)); }

    @Transactional
    public ListItem create(Form form) {
        RailwayLine line = line();
        ensureStationNoAvailable(line.getId(), form.stationNo().trim(), null);
        int stationOrder = form.stationOrder() > 0 ? form.stationOrder()
                : stationRepository.findByLineIdOrderBySequenceNoAsc(line.getId()).stream()
                        .mapToInt(RailwayStation::getSequenceNo).max().orElse(0) + 1;
        RailwayStation station = new RailwayStation(line, form.stationNo().trim(), form.stationName().trim(),
                blankToNull(form.nameEn()), stationOrder, form.latitude(), form.longitude(),
                blankToNull(form.address()), blankToNull(form.facilities()), blankToNull(form.transferLine()), blankToNull(form.transferInfo()));
        station.updateAdminDetails(blankToNull(form.staffName()), blankToNull(form.phone()),
                blankToNull(form.usageInfo()), form.registeredAt() == null ? LocalDate.now() : form.registeredAt(),
                form.inspectionDate(), form.facilityStatus(), form.lastInspectionDate(),
                form.nextInspectionDueDate() == null
                        ? (form.registeredAt() == null ? LocalDate.now() : form.registeredAt()).plusYears(2)
                        : form.nextInspectionDueDate());
        if (Boolean.FALSE.equals(form.active())) station.setActive(false);
        RailwayStation saved = stationRepository.save(station);
        syncLegacyStaff(saved, form.staffName(), null);
        return item(saved);
    }

    @Transactional
    public ListItem update(Long id, Form form) {
        RailwayStation station = find(id);
        ensureStationNoAvailable(station.getLine().getId(), form.stationNo().trim(), id);
        String previousStationNo = station.getStationNo();
        station.update(form.stationNo().trim(), form.stationName().trim(), blankToNull(form.nameEn()),
                form.stationOrder(), form.latitude(), form.longitude(), blankToNull(form.address()),
                blankToNull(form.facilities()), blankToNull(form.transferLine()), blankToNull(form.transferInfo()));
        station.updateAdminDetails(blankToNull(form.staffName()), blankToNull(form.phone()),
                blankToNull(form.usageInfo()), form.registeredAt() == null ? station.getRegisteredAt() : form.registeredAt(),
                form.inspectionDate(), form.facilityStatus() == null ? station.getFacilityStatus() : form.facilityStatus(),
                form.lastInspectionDate() == null ? station.getLastInspectionDate() : form.lastInspectionDate(),
                form.nextInspectionDueDate() == null ? station.getNextInspectionDueDate() : form.nextInspectionDueDate());
        syncLegacyStaff(station, form.staffName(), previousStationNo);
        if (form.active() != null) station.setActive(form.active());
        return item(station);
    }

    private void syncLegacyStaff(RailwayStation railwayStation, String staffName, String previousStationNo) {
        String stationNo = railwayStation.getStationNo();
        String staff = blankToNull(staffName);
        Station existing = legacyStationRepository.findByStationNo(previousStationNo == null ? stationNo : previousStationNo).orElse(null);
        if (existing == null) {
            legacyStationRepository.save(Station.builder().name(railwayStation.getName()).stationNo(stationNo)
                    .staff(staff == null ? "미지정" : staff).status(OperationStatus.NORMAL).build());
        } else {
            existing.update(railwayStation.getName(), stationNo, staff == null ? existing.getStaff() : staff, existing.getStatus());
        }
    }

    @Transactional
    public ListItem setActive(Long id, boolean active) {
        RailwayStation station = find(id);
        station.setActive(active);
        return item(station);
    }

    private RailwayLine line() {
        return lineRepository.findByName("7호선")
                .orElseThrow(() -> BusinessException.notFound("7호선 정보를 찾을 수 없습니다."));
    }

    private RailwayStation find(Long id) {
        RailwayStation station = stationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("역 정보를 찾을 수 없습니다."));
        if (!station.getLine().getName().equals("7호선")) {
            throw BusinessException.notFound("7호선 역 정보를 찾을 수 없습니다.");
        }
        return station;
    }

    private ListItem item(RailwayStation station) {
        Station stationData = legacyStationRepository.findByStationNo(station.getStationNo()).orElse(null);
        String facilityStatus = station.getFacilityStatus();
        if (facilityStatus == null || facilityStatus.isBlank()) {
            facilityStatus = stationData == null ? "INSPECTED" : switch (stationData.getStatus()) {
                case NORMAL -> "INSPECTED";
                case MAINTENANCE -> "IN_INSPECTION";
                case FAULT -> "NEEDS_INSPECTION";
            };
        }
        LocalDate nextDue = station.getNextInspectionDueDate();
        if (nextDue == null && station.getRegisteredAt() != null) nextDue = station.getRegisteredAt().plusYears(2);
        java.time.LocalDateTime updatedAt = station.getUpdatedAt();
        if (updatedAt == null) {
            LocalDate registeredDate = station.getRegisteredAt() == null ? LocalDate.of(2025, 10, 6) : station.getRegisteredAt();
            updatedAt = registeredDate.atStartOfDay();
        }
        if (nextDue != null && nextDue.isBefore(LocalDate.now()) && !"IN_INSPECTION".equals(facilityStatus)) {
            facilityStatus = "NEEDS_INSPECTION";
        }
        return new ListItem(station.getId(), station.getStationNo(), station.getName(), station.getNameEn(),
                station.getSequenceNo(), station.getLatitude(), station.getLongitude(), station.getAddress(),
                station.getFacilities(), station.getTransferLine(), station.getTransferInfo(), station.isActive(),
                facilityStatus,
                station.getStaffName() != null ? station.getStaffName() : stationData == null ? null : stationData.getStaff(),
                station.getContact(), station.getUsageInfo(),
                station.getRegisteredAt() == null ? LocalDate.of(2025, 10, 6) : station.getRegisteredAt(),
                station.getInspectionDate(), station.getLastInspectionDate() == null ? station.getInspectionDate() : station.getLastInspectionDate(),
                nextDue, updatedAt);
    }

    private void ensureStationNoAvailable(Long lineId, String stationNo, Long exceptId) {
        boolean exists = exceptId == null
                ? stationRepository.existsByLine_IdAndStationNo(lineId, stationNo)
                : stationRepository.existsByLine_IdAndStationNoAndIdNot(lineId, stationNo, exceptId);
        if (exists) throw BusinessException.conflict("\uC774\uBBF8 \uC0AC\uC6A9 \uC911\uC778 \uC5ED \uBC88\uD638\uC785\uB2C8\uB2E4.");
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
