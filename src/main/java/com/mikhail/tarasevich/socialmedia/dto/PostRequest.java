package com.mikhail.tarasevich.socialmedia.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(description = "Класс для регистрации пользователя в системе")
public class PostRequest {

    @ApiModelProperty(value = "ID пользователя", example = "1", notes = "ID используется в запросах на изменение данных о пользователе")
    private int id;

    @ApiModelProperty(value = "ID пользователя, указывать не обязательно (устанавливается автоматически значение аутентифицированного пользователя)")
    private int userId;

    @NotEmpty(message = "Header should not be empty")
    @ApiModelProperty(value = "Заголовок", example = "Heloooo", required = true)
    private String header;

    @NotEmpty(message = "Content should not be empty")
    @ApiModelProperty(value = "Содержание публикации", example = "What's a beautiful day!", required = true)
    private String content;

}
