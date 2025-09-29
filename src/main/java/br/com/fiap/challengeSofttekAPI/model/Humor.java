package br.com.fiap.challengeSofttekAPI.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.*;

import java.time.LocalDateTime;

@Document(collection = "humores")
@Getter
@Setter
@NoArgsConstructor // Necessário para a desserialização do Spring Data/MongoDB
@AllArgsConstructor // Construtor com todos os campos (útil para testes ou outros propósitos)
@EqualsAndHashCode
public class Humor {

    @Id
    private String id;

    @Field("colaborador_id")
    private String colaboradorId;

    @Field("data_registro")
    private LocalDateTime dataRegistro;

    @Field("nivel_humor")
    private int nivelHumor;

    @Field("descricao_humor")
    private NivelHumor descricaoHumor; // Certifique-se de que NivelHumor é um enum ou classe acessível

    public Humor(String colaboradorId, int nivel) {
        this.colaboradorId = colaboradorId; // Atribui o ID do colaborador
        this.nivelHumor = nivel;
        this.descricaoHumor = NivelHumor.fromNivel(nivel); // Assumindo que este método existe no NivelHumor
        this.dataRegistro = LocalDateTime.now(); // Data de registro é definida na criação
    }
}