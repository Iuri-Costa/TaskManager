-- Script de configuração do banco de dados TaskManager
-- Execute como root: sudo mariadb -u root < banco.sql

CREATE DATABASE IF NOT EXISTS taskmanager;

CREATE USER IF NOT EXISTS 'iuri'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON taskmanager.* TO 'iuri'@'localhost';
FLUSH PRIVILEGES;

USE taskmanager;

CREATE TABLE IF NOT EXISTS tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    prioridade VARCHAR(50) NOT NULL,
    data_criacao DATE NOT NULL,
    concluida BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO tarefas (titulo, descricao, prioridade, data_criacao, concluida) VALUES
('Tarefa de exemplo', 'Descrição da tarefa de exemplo', 'MEDIA', CURRENT_DATE, FALSE);
