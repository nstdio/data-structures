package io.github.nstdio.ds.map;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The {@code Map} implementation backed by growable array. Note that internal array maintained by this map NOT and hash
 * table, it's sorted array of {@code Entry}. So all key related operations should be O(logN), because be are performing
 * search on a sorted array. However {@link #put(Object, Object)} might require an additional array shift, so it's
 * become O(N).
 */
public final class SortedArrayMap<K, V> extends AbstractMap<K, V> {
    private Entry<K, V>[] entries;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public V get(Object key) {
        if (isEmpty())
            return null;

        int i = indexFor((K) key);
        return i < 0 ? null : entries[i].getValue();
    }

    @Override
    public V put(K key, V value) {
        Entry<K, V>[] es = entries();
        int n = size;
        int i = indexFor(key);

        if (i < 0) {
            if (es.length == n) {
                es = entries = Arrays.copyOf(es, n * 2);
            }

            int ins = -(i + 1);
            if (ins != n) {
                System.arraycopy(es, ins, es, ins + 1, Math.max(n - ins, 1));
            }

            es[ins] = new SimpleEntry<>(key, value);
            size++;
            return null;
        }

        return es[i].setValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        if (isEmpty())
            return false;

        return indexFor((K) key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        if (isEmpty())
            return false;

        Entry<K, V>[] es = entries;
        for (int i = 0, n = size; i < n; i++) {
            if (Objects.equals(es[i].getValue(), value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V remove(Object key) {
        int n = size;
        if (n == 0)
            return null;

        int i = indexFor((K) key);
        if (i < 0) {
            return null;
        }

        Entry<K, V>[] es = entries;
        V val = es[i].getValue();
        if (i != n) {
            System.arraycopy(es, i + 1, es, i, Math.max(n - i, 1));
        } else {
            es[i] = null;
        }

        size = n - 1;
        return val;
    }

    Entry<K, V>[] entries() {
        if (entries == null)
            entries = new Entry[16];
        return entries;
    }

    private int indexFor(K key) {
        Entry<K, V>[] es = entries();
        int lo = 0;
        int hi = size - 1;

        while (lo <= hi) {
            int mid = (hi + lo) / 2;
            K v = es[mid].getKey();

            int cmp = asComparable(v).compareTo(key);

            if (cmp < 0) {
                lo = mid + 1;
            } else if (cmp > 0) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }

        return -(lo + 1);
    }

    @SuppressWarnings("unchecked")
    private Comparable<? super K> asComparable(Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        return (Comparable<? super K>) key;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        int n = size;
        if (n == 0) {
            return Set.of();
        }

        var ret = new LinkedHashSet<Entry<K, V>>(n);
        var es = entries;
        for (int i = 0; i < n; i++) {
            ret.add(es[i]);
        }

        return ret;
    }

    @Override
    public void clear() {
        var es = entries;
        for (int i = 0, n = size; i < n; i++) {
            es[i] = null;
        }
        size = 0;
    }
}
