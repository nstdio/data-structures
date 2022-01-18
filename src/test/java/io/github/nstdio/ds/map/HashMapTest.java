package io.github.nstdio.ds.map;

import java.util.Map;

class HashMapTest implements MapContract {

    @Override
    public <K, V> Map<K, V> get() {
        return new HashMap<>();
    }
}