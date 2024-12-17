package ru.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Embeddable
public class Location {
    private BigDecimal lat;
    private BigDecimal lon;
}
