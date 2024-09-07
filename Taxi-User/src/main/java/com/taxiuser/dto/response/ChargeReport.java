package com.taxiuser.dto.response;

import com.taxiuser.model.User;

public record ChargeReport(Integer amount, Long userId) implements ReportResult {
}
