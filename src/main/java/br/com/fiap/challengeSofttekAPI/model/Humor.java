package br.com.fiap.challengeSofttekAPI.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "humor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Humor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "nivel_humor", nullable = false)
    private int nivelHumor;

    @Column(name = "descricao_humor")
    @Enumerated(EnumType.STRING)
    private NivelHumor descricaoHumor;

    public Humor(int nivel) {
        this.nivelHumor = nivel;
        this.descricaoHumor = NivelHumor.fromNivel(nivel);
        this.dataRegistro = LocalDateTime.now();
    }
}
