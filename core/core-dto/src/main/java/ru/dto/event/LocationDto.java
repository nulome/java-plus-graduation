package ru.dto.event;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LocationDto {
    private Long id;
    private BigDecimal lat;
    private BigDecimal lon;
}
