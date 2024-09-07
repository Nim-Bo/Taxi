package com.taxiuser.dto.response;

import com.taxiuser.enums.ReportType;

public record ReportDTO(
        ReportType type,
        ReportResult reportResult
) {
}
