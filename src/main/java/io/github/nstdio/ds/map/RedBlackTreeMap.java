package io.github.nstdio.ds.map;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.Set;

import static io.github.nstdio.ds.map.Nodes.*;

public final class RedBlackTreeMap<K, V> extends AbstractMap<K, V> {
    static final boolean RED = true;
    static final boolean BLACK = false;

    private Node<K, V> root;
    private int size;

    static boolean isRed(Node<?, ?> x) {
        return x != null && x.color() == RED;
    }

    @Nullable
    static <K, V> Node<K, V> parentOf(Node<K, V> x) {
        return x != null ? x.parent() : null;
    }

    @Nullable
    static <K, V> Node<K, V> left(Node<K, V> x) {
        return x != null ? x.left() : null;
    }

    @Nullable
    static <K, V> Node<K, V> right(Node<K, V> x) {
        return x != null ? x.right() : null;
    }

    static void setColor(Node<?, ?> x, boolean color) {
        if (x != null)
            x.color(color);
    }

    static boolean color(Node<?, ?> x) {
        return x == null ? BLACK : x.color();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return inorderEntrySet(root);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new Node<>(key, value, BLACK);

            size++;
            return null;
        }

        return put(root, key, value);
    }

    @Override
    public V remove(Object key) {
        var n = find(root, key);
        if (n == null) {
            return null;
        }
        V old = n.getValue();
        delete(n);
        size--;

        return old;
    }

    private void delete(Node<K, V> n) {
        Node<K, V> toFix;
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

            toFix = replacement != null ? replacement : p;
        } else {
            var min = (Node<K, V>) min(n.right());
            var minP = min.parent();
            swapParents(n, min);

            min.left(n.left());
            if (min != n.right()) {
                min.right(n.right());
            }
            minP.left(null);
            toFix = min;
        }

        fixAfterRemove(toFix);
    }

    private void fixAfterRemove(Node<K, V> x) {
        while (x != null && x != root && !isRed(x)) {
            if (x == left(parentOf(x))) {
                var sib = right(parentOf(x));

                if (isRed(sib)) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = right(parentOf(x));
                }

                if (!isRed(left(sib)) && !isRed(right(sib))) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (!isRed(right(sib))) {
                        setColor(left(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = right(parentOf(x));
                    }
                    setColor(sib, color(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(right(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else { // symmetric
                var sib = left(parentOf(x));

                if (isRed(sib)) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = left(parentOf(x));
                }

                if (!isRed(right(sib)) && !isRed(left(sib))) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (!isRed(left(sib))) {
                        setColor(right(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = left(parentOf(x));
                    }
                    setColor(sib, color(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(left(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }

        setColor(x, BLACK);
    }


    @Override
    public boolean containsKey(Object key) {
        var n = Nodes.find(root, key);
        return n != null;
    }

    private V put(Node<K, V> p, K key, V value) {
        Comparable<? super K> k = asComparable(key);

        while (p != null) {
            int cmp = k.compareTo(p.getKey());
            if (cmp == 0) {
                return p.setValue(value);
            }

            Node<K, V> n = p.direction(cmp);
            if (n == null) {
                n = new Node<>(key, value, RED);
                if (cmp < 0) p.left(n);
                else p.right(n);

                fixAfterPut(n);

                size++;
                break;
            }
            p = n;
        }

        return value;
    }

    private void fixAfterPut(Node<K, V> n) {
        while (n != null && isRed(parentOf(n))) {
            var isParentLeft = parentOf(n) == left(parentOf(parentOf(n)));
            Node<K, V> uncle = isParentLeft ? right(parentOf(parentOf(n))) : left(parentOf(parentOf(n)));

            if (isRed(uncle)) {
                setColor(parentOf(n), BLACK);
                setColor(uncle, BLACK);
                setColor(parentOf(parentOf(n)), RED);
                n = parentOf(parentOf(n));
            } else {
                if (isParentLeft) {
                    if (n == right(parentOf(n))) {
                        n = parentOf(n);
                        rotateLeft(n);
                    }

                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    rotateRight(parentOf(parentOf(n)));
                } else {
                    if (n == left(parentOf(n))) {
                        n = parentOf(n);
                        rotateRight(n);
                    }

                    setColor(parentOf(n), BLACK);
                    setColor(parentOf(parentOf(n)), RED);
                    rotateLeft(parentOf(parentOf(n)));
                }
            }
        }

        root.color(BLACK);
    }

    private void rotateRight(Node<K, V> n) {
        if (n != null) {
            var nl = n.left();
            swapParents(n, nl);

            n.left(nl.right());
            nl.right(n);
        }
    }

    private void rotateLeft(@Nullable Node<K, V> n) {
        if (n != null) {
            var nr = n.right();
            swapParents(n, nr);

            n.right(nr.left());
            nr.left(n);
        }
    }

    private void swapParents(Node<K, V> n1, Node<K, V> n2) {
        var n1p = n1.parent();
        if (n1.parent() == null) { // n1 is root
            root = n2;
            n2.parent(null); // delete parent
        } else if (n1p.right() == n1) { // n1 right child?
            n1p.right(n2);
        } else {
            n1p.left(n2);
        }
    }


    boolean isRedBlackTree() {
        var rootIsBlack = !isRed(root);
        var isBalanced = isBalanced();

        return rootIsBlack && isBalanced;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        int black = 0;     // number of black links on path from root to min
        var x = root;
        while (x != null) {
            if (!isRed(x)) black++;
            x = x.left();
        }
        return isBalanced(root, black);
    }

    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(Node<K, V> x, int black) {
        if (x == null) return black == 0;
        if (!isRed(x)) black--;
        return isBalanced(x.left(), black) && isBalanced(x.right(), black);
    }

    Node<K, V> node(K k) {
        return Nodes.find(root, k);
    }

    Node<K, V> root() {
        return root;
    }

    static class Node<K, V> extends InheritableBinaryNode<K, V, Node<K, V>> {
        private boolean color;

        Node(K key, V value, boolean color) {
            super(key, value);
            this.color = color;
        }

        boolean color() {
            return color;
        }

        Node<K, V> color(boolean color) {
            this.color = color;
            return this;
        }
    }
}
