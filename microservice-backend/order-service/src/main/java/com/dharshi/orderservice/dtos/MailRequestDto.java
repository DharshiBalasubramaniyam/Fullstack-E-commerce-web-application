package com.dharshi.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class MailRequestDto {

    private String to;
    private String subject;
    private String body;
}
