package io.github.nstdio.ds.map;

import java.util.HashMap;
import java.util.Map;

public class JdkHashMapTest implements MapContract {
    @Override
    public <K, V> Map<K, V> get() {
        return new HashMap<>();
    }
}
