package com.mikhail.tarasevich.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {

    private String token;

}
