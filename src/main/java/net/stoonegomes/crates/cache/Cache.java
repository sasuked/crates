package net.stoonegomes.crates.cache;

import com.google.common.collect.Maps;
import net.stoonegomes.crates.StrixCrates;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public abstract class Cache<K, V> {

    public StrixCrates strixCrates = StrixCrates.getInstance();
    private Map<K, V> elements = Maps.newConcurrentMap();

    public void putElement(K key, V value) {
        elements.put(key, value);
    }

    public void removeElement(K key) {
        elements.remove(key);
    }

    public V getElement(K key) {
        return elements.get(key);
    }

    public boolean containsElement(K key) {
        return elements.containsKey(key);
    }

    public int size() {
        return elements.size();
    }

    public V getElement(Predicate<V> predicate) {
        for (V value : elements.values()) {
            if (predicate.test(value)) return value;
        }

        return null;
    }

    public Collection<K> getKeys() {
        return elements.keySet();
    }

    public Collection<V> getValues() {
        return elements.values();
    }

    public Map<K, V> getElements() {
        return elements;
    }

}
