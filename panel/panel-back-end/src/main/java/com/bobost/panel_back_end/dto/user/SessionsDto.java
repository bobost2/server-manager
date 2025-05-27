package com.bobost.panel_back_end.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor @NoArgsConstructor
@Data
public class SessionsDto {
    private String sessionId;
    private boolean currentSession;
    private Date lastRequest;
}
