package com.inventarios.inventarios.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

//Esta calse es muy similar a la de ItemEntity solo que se encarga de 
//gaurdar en hisotrioc, la orden y el numero de la operacion
//para futuras consultar y cuadrar las cantidades

@Entity
@Data
@Table(name = "historicos",
       indexes = {
         @Index(name = "idx_hist_corr", columnList = "correlation_id"),
         @Index(name = "idx_hist_line", columnList = "line_id")
       })
public class HistoricDiscount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // <- PK única por fila

  @Column(name = "correlation_id", length = 64, nullable = false)
  private String correlationId;

  @Column(name = "line_id", length = 32, nullable = false)
  private String lineId;

  @Column(length = 200, nullable = false)
  private String name;

  @Column(length = 64, nullable = false)
  private String sku;

  @Column(nullable = false)
  private Integer qty;

  @Column(length = 64, nullable = false)
  private String vendor;

  @Column(name = "create_at", nullable = false)
  private LocalDateTime createAt;

  @Column(name ="completed", nullable=true)
  private String completed;

}