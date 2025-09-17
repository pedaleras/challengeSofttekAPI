package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id; // Importar do Spring Data
import org.springframework.data.mongodb.core.mapping.Document; // Importar do Spring Data MongoDB
import org.springframework.data.mongodb.core.mapping.Field; // Opcional: para customizar nome do campo no Mongo
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "apoios_utilizados") // Mapeia para a coleção "apoios_utilizados" no MongoDB
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ApoioUtilizado {

    @Id
    private String id;

    // @Field é opcional, mas pode ser usado para dar um nome explícito no MongoDB

    @Field("data_registro")
    private LocalDateTime dataRegistro;

    @Field("tipo_apoio")
    private String tipoApoio;

    private String descricao;

    public ApoioUtilizado(String tipoApoio, String descricao){
        this.dataRegistro = LocalDateTime.now();
        this.tipoApoio = tipoApoio;
        this.descricao = descricao;
    }
}