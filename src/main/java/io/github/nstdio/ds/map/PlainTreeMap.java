package io.github.nstdio.ds.map;

import static io.github.nstdio.ds.map.Nodes.asComparable;
import static io.github.nstdio.ds.map.Nodes.min;

import java.util.AbstractMap;
import java.util.Set;

public class PlainTreeMap<K, V> extends AbstractMap<K, V> {
    private BinaryNode<K, V> root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V get(Object key) {
        var n = find(key);
        return n != null ? n.getValue() : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return find(key) != null;
    }

    @Override
    public V remove(Object key) {
        return removeNode(key);
    }

    private V removeNode(Object key) {
        BinaryNode<K, V> n = find(key);
        if (n == null) {
            return null;
        }
        size--;
        V old = n.getValue();

        if (n == root || n.parent() == root) {
            root = rm(root, key);
        } else {
            rm(n.parent(), key);
        }

        return old;
    }

    private BinaryNode<K, V> rm(BinaryNode<K, V> n, Object k) {
        if (n == null) {
            return null;
        }

        int cmp = asComparable(k).compareTo(n.getKey());

        if (cmp < 0) {
            n.left(rm(n.left(), k));
        } else if (cmp > 0) {
            n.right(rm(n.right(), k));
        } else {
            if (n.left() == null) {
                n = n.right();
            } else if (n.right() == null) {
                n = n.left();
            } else {
                BinaryNode<K, V> m = min(n.right());

                if (m.parent() == n) {
                    m.parent().parent(m.right());
                } else {
                    m.parent().left(m.right());
                }

                n.setKey(m.getKey());
                n.setValue(m.getValue());
            }
        }

        return n;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new BinaryNode<>(key, value);
            size++;
            return null;
        }

        return put(root, key, value);
    }

    private V put(BinaryNode<K, V> p, K key, V value) {
        Comparable<? super K> k = asComparable(key);
        while (p != null) {
            int cmp = k.compareTo(p.getKey());
            if (cmp == 0) {
                return p.setValue(value);
            }

            BinaryNode<K, V> n = p.direction(cmp);
            if (n == null) {
                n = new BinaryNode<>(key, value);
                if (cmp < 0) {
                    p.left(n);
                } else {
                    p.right(n);
                }
                n.parent(p);
                size++;
                break;
            }
            p = n;
        }

        return value;
    }

    private BinaryNode<K, V> find(Object key) {
        return Nodes.find(root, key);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Nodes.inorderEntrySet(root);
    }
}
