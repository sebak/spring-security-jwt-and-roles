package com.pw.springsecurity.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterUserDto {
    @Email(message = "must be an valid email")
    private @NotNull String email;

    private @NotNull String password;

    private @NotNull String fullName;
}
