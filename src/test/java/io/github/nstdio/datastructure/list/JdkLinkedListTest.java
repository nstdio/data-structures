package io.github.nstdio.datastructure.list;

import io.github.nstdio.datastructure.ListContract;

import java.util.LinkedList;
import java.util.List;

public class JdkLinkedListTest implements ListContract {
    @Override
    public List<?> get() {
        return new LinkedList<>();
    }
}
