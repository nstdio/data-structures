package io.github.nstdio.datastructure.list;

import io.github.nstdio.datastructure.ListContract;

import java.util.List;

class SinglyLinkedListTest implements ListContract {

    @Override
    public List<?> get() {
        return new SinglyLinkedList<>();
    }
}