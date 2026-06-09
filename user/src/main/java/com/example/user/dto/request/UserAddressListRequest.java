package com.example.user.dto.request;

import com.example.common.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAddressListRequest extends PageRequest {
    @Schema(description = "搜索关键词", example = "\"\"")
    private String keyword;
}
