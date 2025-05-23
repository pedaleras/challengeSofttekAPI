package br.com.fiap.challengeSofttekAPI.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apoio_utilizado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApoioUtilizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "tipo_apoio", nullable = false)
    private String tipoApoio;

    @Column(name = "descricao")
    private String descricao;


    public ApoioUtilizado(String tipoApoio,String descricao){
        this.dataRegistro = LocalDateTime.now();
        this.tipoApoio = tipoApoio;
        this.descricao = descricao;
    }
}

