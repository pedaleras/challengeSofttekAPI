package br.com.fiap.challengeSofttekAPI.model;

public enum NivelHumor {
    MUITO_MAU(1, "Muito mau"),
    MAU(2, "Mau"),
    NORMAL(3, "Normal"),
    BEM(4, "Bem"),
    MUITO_BEM(5, "Muito bem");

    private final int nivel;
    private final String descricao;

    NivelHumor(int nivel, String descricao) {
        this.nivel = nivel;
        this.descricao = descricao;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescricao() {
        return descricao;
    }

    public static NivelHumor fromNivel(int nivel) {
        for (NivelHumor h : values()) {
            if (h.nivel == nivel) return h;
        }
        throw new IllegalArgumentException("Nível de humor inválido: " + nivel);
    }
}

