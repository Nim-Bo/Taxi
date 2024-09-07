package com.taxiuser.dto.response;

import com.taxiuser.model.Order;

public record NewOrderReport(Long orderId) implements ReportResult {
}
