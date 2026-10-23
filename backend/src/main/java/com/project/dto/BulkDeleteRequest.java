package com.project.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BulkDeleteRequest(@NotEmpty List<@NotNull Long> ids) {
}
