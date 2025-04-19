package com.bobost.server_instance.dto.adoptium;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {
    private BinaryInfo binary;
}
