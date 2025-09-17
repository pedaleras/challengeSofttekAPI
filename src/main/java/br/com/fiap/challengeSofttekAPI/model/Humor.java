package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "humores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Humor {

    @Id
    private String id;

    @Field("data_registro")
    private LocalDateTime dataRegistro;

    @Field("nivel_humor")
    private int nivelHumor;

    @Field("descricao_humor")
    private NivelHumor descricaoHumor;

    public Humor(int nivel) {
        this.nivelHumor = nivel;
        this.descricaoHumor = NivelHumor.fromNivel(nivel); // Assumindo que este método existe no NivelHumor
        this.dataRegistro = LocalDateTime.now();
    }
}