package com.astrapay.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MemoryStoreService<T> {
    private final Map<Integer, T> memory = new ConcurrentHashMap<>();


    public List<T> getAll() {
        return memory.values().stream().collect(Collectors.toList());
    }

    public void put(Integer id, T dto) {
        memory.put(id, dto);
    }

    public T get(Integer id) {
        return memory.get(id);
    }

    public void remove(Integer id) {
        memory.remove(id);
    }

    public List<T> find(Predicate<T> predicate){
        return memory.values().stream().filter(predicate).collect(Collectors.toList());
    }
    public T findFirst(Predicate<T> predicate){
        return memory.values().stream().filter(predicate).findFirst().orElse(null);
    }
}
