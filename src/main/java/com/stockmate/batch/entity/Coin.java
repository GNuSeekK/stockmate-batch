package com.stockmate.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
public class Coin {

    @Id
    @Column(name = "symbol")
    private String symbol;

    @Column(name = "name")
    private String name;

}
