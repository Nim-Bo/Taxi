package com.taxiuser.dto.response;

import com.taxiuser.model.User;

public record SignupCompleteReport(User user) implements ReportResult {
}
