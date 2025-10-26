package com.inventarios.inventarios.models;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "items", indexes = {
    @Index(name = "idx_item_sku", columnList = "sku", unique = true)
})
public class ItemEntity {
    
    @Id
    @Column(length = 32, nullable = false)
    private String lineId;

    @Column(length = 64, nullable = false, unique = true)
    private String sku;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer qty; 

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 5, scale = 4, nullable = false)
    private BigDecimal taxRate;

    @Column(length = 64, nullable = false)
    private String warehouse; //origen

    @Column(length = 64, nullable = false)
    private String vendor; //proveedor
    @Lob
    @Column(name = "attributes_json")
    private String attributesJson;  //colores,garantias, consideracioines etc

    private Boolean requiresSerial;
  
    @Override
    public String toString() {
        return "ItemEntity{" +
            "lineId='" + lineId + '\'' +
            ", sku='" + sku + '\'' +
            ", name='" + name + '\'' +
            ", qty=" + String.valueOf(qty) +
            ", unitPrice=" + (unitPrice == null ? "null" : unitPrice.toPlainString()) +
            ", taxRate=" + (taxRate == null ? "null" : taxRate.toPlainString()) +
            ", warehouse='" + warehouse + '\'' +
            ", vendor='" + vendor + '\'' +
            ", requiresSerial=" + String.valueOf(requiresSerial) +
            ", attributesJson=" + (attributesJson == null ? "null" : "'" + attributesJson + "'") +
            '}';
    }
   
}
