package io.github.nstdio.ds.map;

import java.util.AbstractMap;
import java.util.Set;

import static io.github.nstdio.ds.map.Nodes.*;

public final class AVLTreeMap<K, V> extends AbstractMap<K, V> {
    private AVLNode<K, V> root;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public V get(Object key) {
        var n = Nodes.find(root, key);

        return n != null ? n.getValue() : null;
    }

    @Override
    public boolean containsKey(Object key) {
        var n = Nodes.find(root, key);
        return n != null;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new AVLNode<>(key, value);

            size++;
            return null;
        }

        return put(root, key, value);
    }

    private V put(AVLNode<K, V> p, K key, V value) {
        Comparable<? super K> k = asComparable(key);

        while (p != null) {
            int cmp = k.compareTo(p.getKey());
            if (cmp == 0) {
                return p.setValue(value);
            }

            AVLNode<K, V> n = p.direction(cmp);
            if (n == null) {
                n = new AVLNode<>(key, value);
                if (cmp < 0) p.left(n);
                else p.right(n);

                tryBalance(p);
                size++;
                break;
            }
            p = n;
        }

        return value;
    }

    @Override
    public V remove(Object key) {
        AVLNode<K, V> n = find(root, key);
        if (n == null) {
            return null;
        }
        V old = n.getValue();

        delete(n);

        return old;
    }

    private void delete(AVLNode<K, V> n) {
        AVLNode<K, V> toBalance;
        if (n.right() == null || n.left() == null) {
            var p = n.parent();
            var replacement = n.right() != null ? n.right() : n.left();

            if (p == null) {
                root = replacement;
            } else {
                if (n == p.left()) {
                    p.left(replacement);
                } else {
                    p.right(replacement);
                }
            }

            toBalance = replacement != null ? replacement : p;
        } else {
            var min = (AVLNode<K, V>) min(n.right());
            var minP = min.parent();
            swapParents(n, min);

            min.left(n.left());
            if (min != n.right()) {
                min.right(n.right());
            }
            minP.left(null);
            toBalance = min;
        }

        if (toBalance != null) {
            tryBalance(toBalance);
        }
        dispose(n);
        size--;
    }

    private void dispose(AVLNode<K, V> n) {
        n.left(null);
        n.right(null);
        n.parent(null);
        n.setValue(null);
        n.setKey(null);
    }

    private void tryBalance(AVLNode<K, V> n) {
        var p = n;
        while (p != null) {
            if (!p.isBalanced()) {
                balance(p);
            }
            p = p.parent();
        }
    }

    private void balance(AVLNode<K, V> p) {
        if (p.balance() < 0) {
            if (p.right().balance() < 0) {
                rotateRR(p);
            } else {
                rotateRL(p);
            }
        } else {
            if (p.left().balance() < 0) {
                rotateLR(p);
            } else {
                rotateLL(p);
            }
        }
    }

    private void rotateRR(AVLNode<K, V> p) {
        var pr = p.right();

        swapParents(p, pr);

        p.right(pr.left());
        pr.left(p);
    }

    private void rotateRL(AVLNode<K, V> p) {
        var pr = p.right();
        var prl = pr.left();
        swapParents(p, prl);

        p.right(prl.left());
        prl.left(p);
        pr.left(prl.right());
        prl.right(pr);
    }

    private void rotateLL(AVLNode<K, V> p) {
        var pl = p.left();
        swapParents(p, pl);

        p.left(pl.right());
        pl.right(p);
    }

    private void rotateLR(AVLNode<K, V> p) {
        var pl = p.left();
        var plr = pl.right();
        swapParents(p, plr);

        p.left(plr.right());
        plr.right(p);
        pl.right(plr.left());
        plr.left(pl);
    }

    private void swapParents(AVLNode<K, V> n1, AVLNode<K, V> n2) {
        var n1p = n1.parent();
        if (n1.parent() == null) {
            root = n2;
            n2.parent(null);
        } else if (n1p.right() == n1) {
            n1p.right(n2);
        } else {
            n1p.left(n2);
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Nodes.inorderEntrySet(root);
    }

    /**
     * Internal method used only for testing propose.
     */
    AVLNode<K, V> root() {
        return root;
    }

    AVLNode<K, V> node(K k) {
        return find(root, k);
    }

    static class AVLNode<K, V> extends InheritableBinaryNode<K, V, AVLNode<K, V>> {

        AVLNode(K key, V value) {
            super(key, value);
        }

        static <K, V> int height(BinaryNode<K, V> n) {
            if (n == null)
                return 0;

            int l, r = l = 0;

            if (n.left() != null)
                l += height(n.left());

            if (n.right() != null)
                r += height(n.right());

            return Math.max(l, r) + 1;
        }

        boolean isBalanced() {
            return Math.abs(balance()) < 2;
        }

        int balance() {
            if (isLeaf())
                return 0;

            int diff = height(left()) - height(right());

            return diff;
        }
    }
}
