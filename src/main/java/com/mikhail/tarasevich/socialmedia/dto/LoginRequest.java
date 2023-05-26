package com.mikhail.tarasevich.socialmedia.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "Класс для аутентификации пользователя в системе")
public class LoginRequest {

    @Email(message = "Incorrect email format")
    @ApiModelProperty(value = "Email пользователя", example = "john.smith@example.com", required = true)
    private String email;

    @NotEmpty(message = "Password should not be empty")
    @ApiModelProperty(value = "Password пользователя", example = "1111", required = true)
    private String password;

}
