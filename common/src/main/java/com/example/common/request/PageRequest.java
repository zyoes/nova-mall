package com.example.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "分页请求参数")
public class PageRequest {

    @Schema(description = "当前页码，从1开始", example = "1")
    @Min(value = 1, message = "页码不能小于1")
    private Integer page = 1;

    @Schema(description = "每页记录数", example = "10")
    @Min(value = 1, message = "每页记录数不能小于1")
    @Max(value = 100, message = "每页记录数不能超过100")
    private Integer size = 10;
}
