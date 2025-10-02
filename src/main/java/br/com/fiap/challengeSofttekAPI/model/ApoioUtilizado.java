package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "apoios_utilizados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApoioUtilizado {

    @Id
    private String id;

    @Field("data_registro")
    private LocalDateTime dataRegistro;

    @Field("tipo_apoio") // Adicionei este campo pois no DTO de request tem "tipoApoio"
    private String tipoApoio;

    private String descricao;

    public ApoioUtilizado(String tipoApoio, String descricao) {
        this.dataRegistro = LocalDateTime.now();
        this.tipoApoio = tipoApoio;
        this.descricao = descricao;
    }
}