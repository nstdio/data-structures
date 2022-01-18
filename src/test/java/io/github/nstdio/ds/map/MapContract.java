package io.github.nstdio.ds.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

public interface MapContract {

    /**
     * @param <K>
     * @param <V>
     *
     * @return
     */
    <K, V> Map<K, V> get();

    @Test
    default void shouldCreateEmptyMap() {
        //given
        var map = get();

        //when + then
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    default void shouldBeAbleToPutAndGet() {
        //given
        Map<String, Integer> map = get();

        //when
        map.put("a", 1);

        //then
        assertEquals(1, map.get("a"));
    }

    @Test
    default void shouldReturnOldWhenPuttingNew() {
        //given
        Map<String, Integer> map = get();

        //when + then
        assertNull(map.put("a", 1));
        assertEquals(1, map.put("a", 2));
        assertEquals(2, map.put("a", 3));
    }

    @Test
    default void shouldBeAbleToRemoveValue() {
        //given
        Map<String, Integer> map = get();

        //when
        map.put("a", 1);

        //then
        assertEquals(1, map.remove("a"));
        assertFalse(map.containsKey("a"));
        assertNull(map.get("a"));
    }

    @RepeatedTest(32)
    default void shouldPutManyEntries() {
        //given
        var r1 = RandomUtils.nextInt(1, 8196);
        Map<Integer, Integer> map = get();

        //when
        for (int i = 0; i < r1; i++) {
            map.put(i, i * 2);
        }

        //then
        for (int i = 0; i < r1; i++) {
            assertEquals(i * 2, map.get(i));
        }
    }

    @Test
    default void shouldReturnProperKeySet() {
        //given
        Map<String, Integer> map = get();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        //when

        //then
        assertThat(map.keySet())
                .hasSize(3)
                .contains("a", "b", "c");
    }
}
