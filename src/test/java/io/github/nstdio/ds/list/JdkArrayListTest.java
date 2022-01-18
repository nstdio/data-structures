package io.github.nstdio.ds.list;

import java.util.ArrayList;
import java.util.List;

class JdkArrayListTest implements ListContract {
    @Override
    public List<?> get() {
        return new ArrayList<>();
    }
}