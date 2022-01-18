package io.github.nstdio.ds.list;

import java.util.List;

class SinglyLinkedListTest implements ListContract {

    @Override
    public List<?> get() {
        return new SinglyLinkedList<>();
    }
}