package com.example.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页请求参数")
public class PageRequest {

    @Schema(description = "当前页码，从1开始", example = "1")
    private Integer page = 1;

    @Schema(description = "每页记录数", example = "10")
    private Integer size = 10;
}
