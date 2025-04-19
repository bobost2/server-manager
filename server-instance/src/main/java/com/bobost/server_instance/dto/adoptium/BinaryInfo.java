package com.bobost.server_instance.dto.adoptium;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinaryInfo {
    @JsonProperty("image_type")
    private String imageType;

    @JsonProperty("package")
    private PackageDetail packageDetail; // Note the renaming to avoid conflict with reserved keywords
}
