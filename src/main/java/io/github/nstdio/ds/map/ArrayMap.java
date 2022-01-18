package io.github.nstdio.ds.map;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ArrayMap<K, V> implements Map<K, V> {
    private Entry<K, V>[] entries = new Entry[16];
    private int size;

    private static <K, V, R> int indexOf(Object value, Entry<K, V>[] arr, int size, Function<Entry<K, V>, R> fn) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(fn.apply(arr[i]), value)) {
                return i;
            }
        }

        return -1;
    }

    private <R> int indexOf(Object o, Function<Entry<K, V>, R> fn) {
        return indexOf(o, entries, size, fn);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return indexOf(key, Entry::getKey) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        return indexOf(value, Entry::getValue) != -1;
    }

    @Override
    public V get(Object key) {
        var i = indexOf(key, Entry::getKey);
        if (i == -1) {
            return null;
        }

        return entries[i].getValue();
    }

    @Override
    public V put(K key, V value) {
        var es = entries;
        var n = size;

        int i = indexOf(key, Entry::getKey);
        boolean found = i != -1;
        i = found ? i : n;
        V old = found ? es[i].getValue() : null;

        if (!found && es.length == n) {
            int newLen = n * 2;
            es = entries = Arrays.copyOf(es, newLen);
        }

        es[i] = new SimpleEntry<>(key, value);

        if (!found) {
            size = n + 1;
        }
        return old;
    }

    @Override
    public V remove(Object key) {
        final int i = indexOf(key, Entry::getKey);
        if (i == -1) {
            return null;
        }
        return removeAt(i);
    }

    private V removeAt(int i) {
        var es = entries;
        var n = size;

        V old = es[i].getValue();

        int shiftLen = Math.max(n - 1, 1);
        System.arraycopy(es, i + 1, es, i, shiftLen);
        size = n - 1;

        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        Arrays.fill(entries, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        var ret = new HashSet<K>(size);
        for (int i = 0; i < size; i++) {
            ret.add(entries[i].getKey());
        }

        return ret;
    }

    @Override
    public Collection<V> values() {
        @SuppressWarnings("unchecked")
        V[] ret = (V[]) new Object[size];
        for (int i = 0; i < size; i++) {
            ret[i] = entries[i].getValue();
        }

        return Arrays.asList(ret);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        var ret = new HashSet<Entry<K, V>>(size);
        for (int i = 0; i < size; i++) {
            ret.add(entries[i]);
        }

        return ret;
    }
}
