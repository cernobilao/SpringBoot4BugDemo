package cz.cernobilao.exception.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Generated(event = {EventType.UPDATE})
    private LocalDate manufactureDate;

    @JdbcTypeCode(SqlTypes.JSON)
    private String specs;

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
