package io.github.nstdio.ds.map;

import java.util.Map;
import java.util.TreeMap;

public class JdkTreeMapTest implements MapContract {
    @Override
    public <K, V> Map<K, V> get() {
        //noinspection SortedCollectionWithNonComparableKeys
        return new TreeMap<>();
    }
}
