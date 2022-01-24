package io.github.nstdio.ds.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.util.Map;

class PlainTreeMapTest implements MapContract {

    @Override
    public <K, V> Map<K, V> get() {
        return new PlainTreeMap<>();
    }

    @Test
    void shouldRemove() {
        //given
        Map<Integer, Integer> map = get();

        map.put(8, 1);
        map.put(3, 2);
        map.put(10, 1);
        map.put(1, 1);
        map.put(0, 1);
        map.put(6, 1);
        map.put(4, 1);
        map.put(7, 1);
        map.put(14, 1);
        map.put(15, 1);
        map.put(13, 1);
        map.put(9, 1);

        //when
        var rm3 = map.remove(3);
        var rm1 = map.remove(4);
        var rm2 = map.remove(1);

        //then
        assertThat(rm3).isEqualTo(2);
        assertThat(rm1).isEqualTo(1);
        assertThat(rm2).isEqualTo(1);

        assertFalse(map.containsKey(3));
        assertFalse(map.containsKey(4));
        assertFalse(map.containsKey(1));
    }

    @Test
    void shouldRemoveRoot() {
        //given
        Map<Integer, Integer> map = get();

        map.put(5, 5);
        map.put(1, 1);
        map.put(6, 6);

        //when
        map.remove(5);

        //then
        assertThat(map)
                .containsExactly(entry(1, 1), entry(6, 6));
    }

    @Test
    void shouldPutSimple() {
        //given
        Map<Integer, Integer> map = get();

        //when
        map.put(1, 1);
        map.put(2, 1);
        map.put(3, 1);

        //then
        assertThat(map).containsKeys(1, 2, 3);
    }
}