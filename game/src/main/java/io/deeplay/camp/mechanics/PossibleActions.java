package io.deeplay.camp.mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PossibleActions <K,V>{
    private final HashMap<K, ArrayList<V>> map = new HashMap<>();

    public void put(K key,V value){
        map.computeIfAbsent(key, k-> new ArrayList<>()).add(value);
    }

    public List<V> get(K key){
        return map.getOrDefault(key,new ArrayList<>());
    }
}
