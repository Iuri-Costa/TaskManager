package com.taskmanager.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RepositorioGenerico<T> {

    private final List<T> itens = new ArrayList<>();

    public void adicionar(T item) {
        if (item == null) throw new IllegalArgumentException("Item não pode ser nulo.");
        itens.add(item);
    }

    public void remover(T item) {
        itens.remove(item);
    }

    public List<T> listarTodos() {
        return new ArrayList<>(itens);
    }

    public List<T> filtrar(Predicate<? super T> criterio) {
        return itens.stream()
                .filter(criterio)
                .collect(Collectors.toList());
    }


    public long contar(Predicate<? super T> criterio) {
        return itens.stream().filter(criterio).count();
    }


    public int tamanho() {
        return itens.size();
    }

    public static <T> void imprimirTodos(List<? extends T> lista) {
        lista.forEach(System.out::println);
    }

    public static <T> void copiar(List<? extends T> origem, List<? super T> destino) {
        destino.addAll(origem);
    }
}