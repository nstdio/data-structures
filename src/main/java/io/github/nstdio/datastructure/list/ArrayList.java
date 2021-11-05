package io.github.nstdio.datastructure.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public final class ArrayList<E> implements List<E> {
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
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        growIfNecessary(1);

        data[size++] = e;

        return true;
    }

    private void growIfNecessary(int n) {
        if (data == null) {
            data = new Object[8];
            return;
        }

        if (data.length == size) {
            int cap = size * 2;
            var newData = new Object[cap];
            System.arraycopy(data, 0, newData, 0, size);
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
            System.arraycopy(d, idx + 1, d, idx, len);
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

        for (E o : c) {
            add(o);
        }

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkIndex(index);
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        var d = data;
        int n = size;
        for (int i = 0; i < n; i++) {
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
        Object old = data[index];
        data[index] = element;

        return (E) old;
    }

    @Override
    public void add(int index, E element) {
        checkIndex(index);
        growIfNecessary(1);
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        return removeAt(index);
    }

    @Override
    public int indexOf(Object o) {
        if (isEmpty()) {
            return -1;
        }

        var d = data;
        var sz = this.size;
        if (o == null) {
            for (int i = 0; i < sz; i++) {
                if (d[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < sz; i++) {
                if (Objects.equals(d[i], o)) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
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
}
