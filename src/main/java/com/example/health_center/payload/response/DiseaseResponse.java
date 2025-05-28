package com.example.health_center.payload.response;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DiseaseResponse {

    private Long id;

    private String name;

    private String code;
}
