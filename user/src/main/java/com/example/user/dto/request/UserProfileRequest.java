package com.example.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileRequest {
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空!")
    @Size(max = 255)
    private String email;

    private String name;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "【手机号】格式不正确")
    private String mobile;

    private String avatar;
}
