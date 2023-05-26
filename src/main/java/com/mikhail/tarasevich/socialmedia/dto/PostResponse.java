package com.mikhail.tarasevich.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponse {

    private int id;

    private int userId;

    private String header;

    private String content;

    private LocalDateTime createdAt;

    private List<Integer> images;

}
