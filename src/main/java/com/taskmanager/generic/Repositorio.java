package com.taskmanager.generic;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Repositorio<T, ID> {
    void adicionar(T item);
    void remover(ID id);
    void atualizar(T item);
    Optional<T> buscarPorId(ID id);
    List<T> listarTodos();
}