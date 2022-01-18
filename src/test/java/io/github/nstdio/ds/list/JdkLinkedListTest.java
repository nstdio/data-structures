package io.github.nstdio.ds.list;

import java.util.LinkedList;
import java.util.List;

public class JdkLinkedListTest implements ListContract {
    @Override
    public List<?> get() {
        return new LinkedList<>();
    }
}
