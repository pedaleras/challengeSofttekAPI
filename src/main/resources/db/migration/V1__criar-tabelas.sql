-- Tabela: avaliacao_riscos
CREATE TABLE IF NOT EXISTS avaliacao_riscos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_avaliacao DATETIME NOT NULL,
    media_percentual DOUBLE NOT NULL,
    categoria_final VARCHAR(100) NOT NULL
);

-- Tabela: humor
CREATE TABLE IF NOT EXISTS humor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_registro DATETIME NOT NULL,
    nivel_humor INT NOT NULL CHECK (nivel_humor BETWEEN 1 AND 5),
    descricao_humor VARCHAR(255)
);

-- Tabela: apoio_utilizado
CREATE TABLE IF NOT EXISTS apoio_utilizado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_registro DATETIME NOT NULL,
    tipo_apoio VARCHAR(100) NOT NULL,
    descricao TEXT
);
