package io.github.nstdio.ds;

import java.lang.reflect.Array;
import java.util.*;

final class PriorityQueue<E extends Comparable<E>> extends AbstractQueue<E> {
    private final Comparator<E> comparator;
    private E[] q = newArray(10);
    private int size;

    PriorityQueue() {
        this(Comparator.naturalOrder());
    }

    PriorityQueue(Comparator<E> comparator) {
        this.comparator = Objects.requireNonNull(comparator);
    }

    private E[] newArray(int size) {
        @SuppressWarnings("unchecked")
        var a = (E[]) Array.newInstance(Comparable.class, size);

        return a;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int cursor = 1;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext())
                    throw new NoSuchElementException();

                return q[cursor++];
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean offer(E k) {
        if (size == q.length - 1) {
            q = Arrays.copyOf(q, size * 2);
        }

        q[++size] = k;
        swim(size);
        return true;
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        E max = q[1];
        swap(1, size--);
        sink(1);
        q[size + 1] = null;
        return max;
    }

    @Override
    public E peek() {
        return isEmpty() ? null : q[1];
    }

    private boolean cmp(int i, int j) {
        return comparator.compare(q[i], q[j]) < 0;
    }

    private void swap(int i, int j) {
        var q = this.q;
        E t = q[i];
        q[i] = q[j];
        q[j] = t;
    }

    private void swim(int k) {
        while (k > 1 && cmp(k / 2, k)) {
            swap(k / 2, k);
            k = k / 2;
        }
    }

    private void sink(int k) {
        int n = size;
        while (2 * k <= n) {
            int j = 2 * k;
            if (j < n && cmp(j, j + 1)) {
                j++;
            }
            if (!cmp(k, j)) {
                break;
            }
            swap(k, j);
            k = j;
        }
    }
}
