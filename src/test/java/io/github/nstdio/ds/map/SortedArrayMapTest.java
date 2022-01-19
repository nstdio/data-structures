package io.github.nstdio.ds.map;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SortedArrayMapTest implements MapContract {

    @Override
    public <K, V> Map<K, V> get() {
        return new SortedArrayMap<>();
    }

    @Test
    void shouldBeSorted() {
        //given
        Map<Integer, Integer> map = get();
        int n = 64;

        //when
        for (int i = 0; i < n; i++) {
            var key = RandomUtils.nextInt(0, 4096);

            map.put(key, i);
        }
        var keys = List.copyOf(map.keySet());

        //then
        assertThat(keys).isSorted();
    }
}