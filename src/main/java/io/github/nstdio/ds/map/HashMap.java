package io.github.nstdio.ds.map;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;

public final class HashMap<K, V> extends AbstractMap<K, V> {
    private final float loadFactor;
    private List<Entry<K, V>>[] table = new List[8];
    private int rehashThreshold;
    private int size;

    public HashMap(float loadFactor) {
        this.loadFactor = loadFactor;
        this.rehashThreshold = (int) (table.length * loadFactor);
    }

    HashMap() {
        this(0.75f);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V get(Object key) {
        Entry<K, V> e;
        return (e = entryForKey(bucketForKey(key), key)) == null ? null : e.getValue();
    }

    @Override
    public V remove(Object key) {
        var bucket = bucketForKey(key);
        int eix;
        if ((eix = entryIndex(bucket, key)) == -1) {
            return null;
        }

        var e = bucket.remove(eix);
        size--;
        return e.getValue();
    }

    @Override
    public boolean containsKey(Object key) {
        return entryIndex(bucketForKey(key), key) != -1;
    }

    private List<Entry<K, V>> bucketForKey(Object key) {
        return table[indexFor(key)];
    }

    @Override
    public boolean containsValue(Object value) {
        for (List<Entry<K, V>> bucket : table) {
            if (bucket == null || bucket.isEmpty()) continue;

            for (Entry<K, V> e : bucket) {
                if (Objects.equals(e.getValue(), value)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public V put(K key, V value) {
        int i = indexFor(key);
        var bucket = table[i];
        if (bucket == null) {
            table[i] = bucket = newBucket();
        }

        V old = null;
        Entry<K, V> e;
        if ((e = entryForKey(bucket, key)) != null) {
            old = e.setValue(value);
        } else {
            bucket.add(new SimpleEntry<>(key, value));
            size++;
        }


        if (size > rehashThreshold) {
            rehash();
        }

        return old;
    }

    private List<Entry<K, V>> newBucket() {
        return new LinkedList<>();
    }

    private void rehash() {
        var oldTab = table;
        int tabLen = table.length * 2;
        @SuppressWarnings("unchecked")
        List<Entry<K, V>>[] tab = new List[tabLen];

        for (int j = 0, l = oldTab.length; j < l; j++) {
            var bucket = oldTab[j];
            if (bucket == null || bucket.isEmpty()) continue;

            for (Entry<K, V> e : bucket) {
                var i = indexFor(e.getKey(), tabLen);
                if (tab[i] == null) {
                    tab[i] = newBucket();
                }

                tab[i].add(e);
            }
        }

        rehashThreshold = (int) (tabLen * loadFactor);
        table = tab;
    }

    int indexFor(Object k) {
        return indexFor(k, table.length);
    }

    int indexFor(Object k, int size) {
        return k == null ? 0 : k.hashCode() % size;
    }

    Entry<K, V> entryForKey(List<Entry<K, V>> bucket, Object key) {
        int i;
        return (i = entryIndex(bucket, key)) == -1 ? null : bucket.get(i);
    }

    int entryIndex(List<Entry<K, V>> bucket, Object key) {
        if (bucket == null) {
            return -1;
        }

        if (bucket instanceof RandomAccess) {
            for (int i = 0; i < bucket.size(); i++) {
                var e = bucket.get(i);
                if (Objects.equals(e.getKey(), key)) {
                    return i;
                }
            }
        } else {
            int i = 0;
            for (Entry<K, V> e : bucket) {
                if (Objects.equals(e.getKey(), key)) {
                    return i;
                }
                i++;
            }
        }

        return -1;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        var ret = new HashSet<Entry<K, V>>(size);
        for (List<Entry<K, V>> bucket : table) {
            if (bucket == null || bucket.isEmpty()) continue;
            ret.addAll(bucket);
        }

        return ret;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }
}
