package io.github.nstdio.ds.map;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.Set;

public class PlainTreeMap<K, V> extends AbstractMap<K, V> {
    private Node<K, V> root;
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
        return n != null ? n.value : null;
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
        Node<K, V> n = find(key);
        if (n == null) {
            return null;
        }
        size--;
        V old = n.value;

        if (n == root || n.parent == root) {
            root = rm(root, key);
        } else {
            rm(n.parent, key);
        }

        return old;
    }

    private Node<K, V> rm(Node<K, V> n, Object k) {
        if (n == null) {
            return null;
        }

        int cmp = asComparable(k).compareTo(n.key);

        if (cmp < 0) {
            n.left = rm(n.left, k);
        } else if (cmp > 0) {
            n.right = rm(n.right, k);
        } else {
            if (n.left == null) {
                n = n.right;
            } else if (n.right == null) {
                n = n.left;
            } else {
                Node<K, V> minN = min(n.right);
                n.key = minN.key;
                n.value = minN.value;

                n.right = rm(n.right, n.key);
            }
        }

        return n;
    }

    private Node<K, V> min(Node<K, V> n) {
        while (n.left != null) {
            n = n.left;
        }
        return n;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return null;
        }

        return put(root, key, value);
    }

    private V put(Node<K, V> p, K key, V value) {
        Comparable<? super K> k = asComparable(key);
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp == 0) {
                return p.setValue(value);
            }

            Node<K, V> n = cmp < 0 ? p.left : p.right;
            if (n == null) {
                n = new Node<>(key, value);
                if (cmp < 0) {
                    p.left = n;
                } else {
                    p.right = n;
                }
                n.parent = p;
                size++;
                break;
            }
            p = n;
        }

        return value;
    }

    private Node<K, V> find(Object key) {
        Comparable<? super K> k = asComparable(key);

        Node<K, V> n = root;
        while (n != null) {
            var cmp = k.compareTo(n.key);
            if (cmp == 0) {
                return n;
            }

            n = cmp < 0 ? n.left : n.right;
        }

        return null;
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
        if (root == null) {
            return Set.of();
        }
        Set<Entry<K, V>> ret = new LinkedHashSet<>(size);
        var s = new ArrayDeque<Node<K, V>>();
        var n = root;

        while (!s.isEmpty() || n != null) {
            if (n != null) {
                s.push(n);
                n = n.left;
            } else {
                n = s.pop();
                ret.add(new SimpleEntry<>(n.getKey(), n.getValue()));
                n = n.right;
            }
        }

        return ret;
    }

    static final class Node<K, V> implements Entry<K, V> {
        private K key;
        private V value;
        private Node<K, V> left, right, parent;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
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
    }
}
