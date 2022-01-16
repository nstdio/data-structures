package io.github.nstdio.datastructure.list;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class SinglyLinkedList<E> implements List<E> {
    private int size;
    private Node<E> head;
    private Node<E> tail;

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
        for (Node<E> n = head; n != null; n = n.next) {
            if (Objects.equals(n.value, o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new It(head);
    }

    @Override
    public Object[] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int sz = size;
        T[] ret = a.length < sz ? (T[]) Array.newInstance(a.getClass().getComponentType(), sz) : a;
        int i = 0;
        for (Node<E> n = head; n != null; n = n.next, i++) {
            ret[i] = (T) n.value;
        }
        return ret;
    }

    @Override
    public boolean add(E e) {
        var n = new Node<>(e);
        if (tail != null) {
            tail.next = n;
            tail = tail.next;
        } else if (head == null) {
            head = n;
        } else {
            head.next = n;
            tail = head.next;
        }

        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (isEmpty()) {
            return false;
        }

        var cur = new Node<>(null, head);
        while (cur != null) {
            var next = cur.next;
            if (next != null && Objects.equals(next.value, o)) {

                cur.next = cur.next.next;
                if (next == head) {
                    head = cur.next;
                } else if (next == tail) {
                    tail = cur;
                }

                size--;
                return true;
            }
            cur = cur.next;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }

        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }

        for (E e : c) {
            add(index++, e);
        }

        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean changed = false;
        for (Object e : c) {
            changed |= remove(e);
        }

        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        var toRemove = new java.util.ArrayList<>();
        for (Node<E> n = head; n != null; n = n.next) {
            if (!c.contains(n.value)) {
                toRemove.add(n.value);
            }
        }

        return removeAll(toRemove);
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public E get(int index) {
        checkIndex(index);

        Node<E> n = nodeAt(index);

        return n.value;
    }

    private Node<E> nodeAt(int index) {
        var n = head;
        while (index-- > 0) {
            n = n.next;
        }
        return n;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public E set(int index, E element) {
        checkIndex(index);
        if (index == 0) {
            var old = head.value;
            head = new Node<>(element, head.next);
            return old;
        }

        var prev = nodeAt(index - 1);
        var cur = prev.next;
        prev.next = new Node<>(element, cur == tail ? null : cur);

        if (cur == tail) {
            tail = prev.next;
        }

        return cur.value;
    }

    @Override
    public void add(int index, E element) {
        checkIndex(index);

        if (index == 0) {
            head = new Node<>(element, head);
        } else if (index == size - 1) {
            var prev = nodeAt(index - 1);
            prev.next = new Node<>(element, tail);
        } else {
            var prev = nodeAt(index - 1);
            var cur = prev.next;
            prev.next = new Node<>(element, cur);
            prev.next.next = cur;
        }

        size++;
    }

    @Override
    public E remove(int index) {
        checkIndex(index);
        E old;
        if (index == 0) {
            old = head.value;
            head = head.next;
        } else {
            var prev = nodeAt(index - 1);
            var cur = prev.next;
            prev.next = cur.next;
            old = cur.value;

            if (cur == tail) {
                tail = prev;
            }
        }

        size--;
        return old;
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        for (Node<E> n = head; n != null; n = n.next, i++) {
            if (Objects.equals(n.value, o)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = 0;
        int j = -1;
        for (Node<E> n = head; n != null; n = n.next, i++) {
            if (Objects.equals(n.value, o)) {
                j = i;
            }
        }

        return j;
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

    static final class Node<E> {
        E value;
        Node<E> next;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = next;
        }

        Node(E value) {
            this(value, null);
        }
    }

    class It implements Iterator<E> {
        Node<E> cur;

        It(Node<E> head) {
            cur = head;
        }

        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public E next() {
            if (cur == null) {
                throw new NoSuchElementException();
            }

            var c = this.cur;
            cur = cur.next;
            return c.value;
        }
    }
}
