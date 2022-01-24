package io.github.nstdio.ds.map;

import java.util.Map;

import static io.github.nstdio.ds.map.Nodes.asComparable;

class BinaryNode<K, V> implements Map.Entry<K, V> {
    private static final boolean DEBUG = false;

    private K key;
    private V value;
    private BinaryNode<K, V> left, right, parent;

    BinaryNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    K setKey(K k) {
        var old = key;
        key = k;
        return old;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V v) {
        var old = value;
        value = v;
        return old;
    }

    BinaryNode<K, V> left() {
        return left;
    }

    BinaryNode<K, V> right() {
        return right;
    }

    BinaryNode<K, V> parent() {
        return parent;
    }

    BinaryNode<K, V> parent(BinaryNode<K, V> parent) {
        this.parent = parent;
        return this;
    }

    BinaryNode<K, V> left(BinaryNode<K, V> left) {
        var oldLeft = this.left;
        this.left = left;
        afterSet(left, oldLeft);
        return this;
    }

    BinaryNode<K, V> right(BinaryNode<K, V> right) {
        var oldRight = this.left;
        this.right = right;
        afterSet(right, oldRight);
        return this;
    }

    private void afterSet(BinaryNode<K, V> n, BinaryNode<K, V> oldN) {
        if (n != null) {
            if (DEBUG) {
                var cmp = asComparable(key).compareTo(n.key);
                if (n == right) {
                    assert cmp < 0;
                } else {
                    assert cmp > 0;
                }
            }
            n.parent(this);
        }
    }

    BinaryNode<K, V> direction(int cmp) {
        if (cmp < 0) return left;
        else if (cmp > 0) return right;

        return this;
    }

    boolean isLeaf() {
        return left == null && right == null;
    }
}
