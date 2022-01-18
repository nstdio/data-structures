package io.github.nstdio.ds.map;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ArrayMap<K, V> implements Map<K, V> {
    private Object[] keys = new Object[16];
    private Object[] values = new Object[16];
    private int size;

    private static int indexOf(Object value, Object[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (Objects.equals(arr[i], value)) {
                return i;
            }
        }

        return -1;
    }

    private static boolean contains(Object value, Object[] arr) {
        return indexOf(value, arr) != -1;
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
        return contains(key, keys);
    }

    @Override
    public boolean containsValue(Object value) {
        return contains(value, values);
    }

    @Override
    public V get(Object key) {
        var i = indexOf(key, keys);
        if (i == -1) {
            return null;
        }

        @SuppressWarnings("unchecked")
        var value = (V) values[i];

        return value;
    }

    @Override
    public V put(K key, V value) {
        var ks = keys;
        var vs = values;
        var n = size;

        int i = indexOf(key, ks);
        boolean found = i != -1;
        i = found ? i : n;
        @SuppressWarnings("unchecked")
        V old = found ? (V) vs[i] : null;

        if (!found && ks.length == n) {
            int newLen = n * 2;
            ks = keys = Arrays.copyOf(ks, newLen);
            vs = values = Arrays.copyOf(vs, newLen);
        }

        ks[i] = key;
        vs[i] = value;

        if (!found) {
            size = n + 1;
        }
        return old;
    }

    @Override
    public V remove(Object key) {
        final int i = indexOf(key, keys);
        if (i == -1) {
            return null;
        }
        return removeAt(i);
    }

    private V removeAt(int i) {
        Object[] ks = keys, vs = values;
        var n = size;

        @SuppressWarnings("unchecked")
        V old = (V) vs[i];

        int shiftLen = Math.max(n - 1, 1);
        System.arraycopy(ks, i + 1, ks, i, shiftLen);
        System.arraycopy(vs, i + 1, vs, i, shiftLen);
        size = n - 1;

        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        @SuppressWarnings("unchecked")
        K[] ks = (K[]) keys;
        return Set.of(ks);
    }

    @Override
    public Collection<V> values() {
        @SuppressWarnings("unchecked")
        V[] vs = (V[]) values;
        return List.of(vs);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        var es = new Entry[size];
        Object[] ks = keys, vs = values;
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            var e = new SimpleEntry<>((K) ks[i], (V) vs[i]);
            es[i] = e;
        }

        @SuppressWarnings("unchecked")
        var ret = (Entry<K, V>[]) es;
        return Set.of(ret);
    }
}
