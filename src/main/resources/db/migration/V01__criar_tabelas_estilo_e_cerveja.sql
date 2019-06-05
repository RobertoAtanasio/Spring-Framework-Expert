CREATE TABLE estilo (
  codigo bigint(20) NOT NULL AUTO_INCREMENT,
  nome varchar(50) NOT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB;

CREATE TABLE cerveja (
    codigo BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL,
    nome VARCHAR(80) NOT NULL,
    descricao TEXT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    teor_alcoolico DECIMAL(10, 2) NOT NULL,
    comissao DECIMAL(10, 2) NOT NULL,
    sabor VARCHAR(50) NOT NULL,
    origem VARCHAR(50) NOT NULL,
    codigo_estilo BIGINT(20) NOT NULL,
    FOREIGN KEY (codigo_estilo) REFERENCES estilo(codigo)
) ENGINE=InnoDB;

INSERT INTO estilo VALUES (0, 'Amber Lager');
INSERT INTO estilo VALUES (0, 'Dark Lager');
INSERT INTO estilo VALUES (0, 'Pale Lager');
INSERT INTO estilo VALUES (0, 'Pilsner');