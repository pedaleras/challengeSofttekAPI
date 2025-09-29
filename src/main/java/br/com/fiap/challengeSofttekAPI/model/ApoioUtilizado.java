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

    //VINCULAR O APOIO AO COLABORADOR ANÔNIMO
    @Field("colaborador_id") // Sugestão de nome para o campo no MongoDB
    private String colaboradorId;

    @Field("data_registro")
    private LocalDateTime dataRegistro;

    @Field("tipo_apoio") // Adicionei este campo pois no DTO de request tem "tipoApoio"
    private String tipoApoio;

    private String descricao;

    public ApoioUtilizado(String colaboradorId, String tipoApoio, String descricao){
        this.colaboradorId = colaboradorId; // Atribui o ID do colaborador
        this.dataRegistro = LocalDateTime.now();
        this.tipoApoio = tipoApoio;
        this.descricao = descricao;
    }
    public ApoioUtilizado(String colaboradorId, String descricao){
        this.colaboradorId = colaboradorId; // Atribui o ID do colaborador
        this.dataRegistro = LocalDateTime.now();
        this.descricao = descricao;
    }
}