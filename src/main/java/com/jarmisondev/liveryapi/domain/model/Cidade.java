package com.jarmisondev.liveryapi.domain.model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cidade {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "cidade_id", nullable = false)
    private Estado estado;
}
