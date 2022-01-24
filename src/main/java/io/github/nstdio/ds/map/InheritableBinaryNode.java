package io.github.nstdio.ds.map;

abstract class InheritableBinaryNode<K, V, S extends InheritableBinaryNode<K, V, S>> extends BinaryNode<K, V> {
    InheritableBinaryNode(K key, V value) {
        super(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    S left() {
        return (S) super.left();
    }

    @Override
    @SuppressWarnings("unchecked")
    S right() {
        return (S) super.right();
    }

    @Override
    @SuppressWarnings("unchecked")
    S parent() {
        return (S) super.parent();
    }

    @Override
    @SuppressWarnings("unchecked")
    S parent(BinaryNode<K, V> parent) {
        return (S) super.parent(parent);
    }

    @Override
    @SuppressWarnings("unchecked")
    S left(BinaryNode<K, V> left) {
        return (S) super.left(left);
    }

    @Override
    @SuppressWarnings("unchecked")
    S right(BinaryNode<K, V> right) {
        return (S) super.right(right);
    }

    @Override
    @SuppressWarnings("unchecked")
    S direction(int cmp) {
        return (S) super.direction(cmp);
    }
}
