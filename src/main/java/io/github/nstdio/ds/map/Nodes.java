package io.github.nstdio.ds.map;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class Nodes {
    private Nodes() {
    }

    static <K, V, N extends BinaryNode<K, V>> N find(N root, Object key) {
        Comparable<? super K> k = asComparable(key);

        N n = root;
        while (n != null) {
            var cmp = k.compareTo(n.getKey());
            if (cmp == 0) {
                return n;
            }

            @SuppressWarnings("unchecked")
            var dir = (N) n.direction(cmp);
            n = dir;
        }

        return null;
    }

    static <K, V> Set<Map.Entry<K, V>> inorderEntrySet(BinaryNode<K, V> root) {
        if (root == null) {
            return Set.of();
        }

        Set<Map.Entry<K, V>> ret = new LinkedHashSet<>();
        var s = new ArrayDeque<BinaryNode<K, V>>();
        var n = root;

        while (!s.isEmpty() || n != null) {
            if (n != null) {
                s.push(n);
                n = n.left();
            } else {
                n = s.pop();
                ret.add(new AbstractMap.SimpleEntry<>(n.getKey(), n.getValue()));
                n = n.right();
            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    static <K> Comparable<? super K> asComparable(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        return (Comparable<? super K>) key;
    }

    static <K, V> BinaryNode<K, V> min(BinaryNode<K, V> n) {
        while (n.left() != null) {
            n = n.left();
        }
        return n;
    }
}
