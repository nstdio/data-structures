package io.github.nstdio.datastructure.list;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public final class ArrayList<E> implements List<E>, RandomAccess {
    private Object[] data;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) > -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new It();
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int n = size;
        @SuppressWarnings("unchecked")
        T[] ret = a.length < n ? (T[]) Array.newInstance(a.getClass().getComponentType(), n) : a;
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(data, 0, ret, 0, n);

        return ret;
    }

    @Override
    public boolean add(E e) {
        growIfNecessary(1);

        data[size++] = e;

        return true;
    }

    private void growIfNecessary(int n) {
        if (data == null) {
            data = new Object[Math.max(8, n)];
            return;
        }

        int sz = size;
        if (sz + n > data.length) {
            var d = data;
            var newCap = sz == d.length ? sz * 2 + n : sz + n;
            var newData = new Object[newCap];
            System.arraycopy(d, 0, newData, 0, sz);
            data = newData;
        }
    }

    @Override
    public boolean remove(Object o) {
        var idx = indexOf(o);
        if (idx == -1) {
            return false;
        }
        removeAt(idx);

        return true;
    }

    @SuppressWarnings("unchecked")
    private E removeAt(int idx) {
        Object[] d = data;
        E old = (E) d[idx];
        if (idx == size - 1) {
            d[idx] = null;
        } else {
            var len = d.length - 1;
            System.arraycopy(d, idx + 1, d, idx, len - idx);
            d[len] = null;
        }
        size--;

        return old;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) { // implicit NPE
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) { // implicit NPE
            return false;
        }

        int cSize = c.size();
        growIfNecessary(cSize);

        fastAddFrom(size, c, cSize);
        return true;
    }

    void fastAddFrom(int from, Collection<? extends E> c, int cSize) {
        Object[] d = data;
        for (E o : c) {
            d[from++] = o;
        }
        size += cSize;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndex(index);
        if (c.isEmpty()) {
            return false;
        }

        var cSize = c.size();
        growIfNecessary(cSize);
        System.arraycopy(data, index, data, index + cSize, index);

        fastAddFrom(index, c, cSize);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean changed = false;
        for (Object o : c) {
            changed |= remove(o);
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        var toRemove = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            var o = data[i];

            if (!c.contains(o)) {
                toRemove.add(o);
            }
        }

        return removeAll(toRemove);
    }

    @Override
    public void clear() {
        var d = data;
        for (int i = 0, n = size; i < n; i++) {
            d[i] = null;
        }

        size = 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        checkIndex(index);

        return (E) data[index];
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public E set(int index, E element) {
        checkIndex(index);
        var d = data;
        Object old = d[index];
        d[index] = element;

        return (E) old;
    }

    @Override
    public void add(int index, E element) {
        checkIndex(index);
        growIfNecessary(1);

        var d = data;
        System.arraycopy(d, index, d, index + 1, size - index);
        size++;
        d[index] = element;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        return removeAt(index);
    }

    @Override
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOfRange(o, size, 0);
    }

    private int indexOfRange(Object o, int start, int end) {
        if (isEmpty()) {
            return -1;
        }
        var d = data;

        if (start < end) {
            for (int i = start; i < end; i++) {
                if (Objects.equals(d[i], o)) {
                    return i;
                }
            }
        } else {
            for (int i = start - 1; i > end; i--) {
                if (Objects.equals(d[i], o)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }

    /**
     *
     */
    Object[] data() {
        return data;
    }

    private class It implements Iterator<E> {
        private int idx;

        @Override
        public boolean hasNext() {
            return idx != size;
        }

        @Override
        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();

            @SuppressWarnings("unchecked")
            E e = (E) data[idx++];
            return e;
        }
    }
}
