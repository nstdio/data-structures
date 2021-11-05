package io.github.nstdio.datastructure.list;

import io.github.nstdio.datastructure.ListContract;

import java.util.ArrayList;
import java.util.List;

class JdkArrayListTest implements ListContract {
    @Override
    public List<?> get() {
        return new ArrayList<>();
    }
}