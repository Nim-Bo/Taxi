package com.taxiuser.dto.response;

import com.taxiuser.model.Order;

public record OrderPaymentReport(Long orderId) implements ReportResult {
}
