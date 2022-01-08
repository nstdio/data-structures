package io.github.nstdio.datastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface ListContract {
    static Stream<Arguments> containsAllPositiveData() {
        return Stream.of(
                arguments(new Integer[]{1, 2}, List.of(1)),
                arguments(new Integer[]{1, 2}, List.of(1, 2)),
                arguments(new Integer[]{1}, List.of(1)),
                arguments(new Integer[]{1, 1, 1}, List.of(1)),
                arguments(new Integer[]{1, 2, 3}, List.of(1, 2, 3)),
                arguments(new Integer[]{1, 2, 3}, Set.of(1, 2, 3)),
                arguments(new Integer[]{}, Set.of()),
                arguments(new Integer[]{1}, Set.of())
        );
    }

    static Stream<Arguments> containsAllNegativeData() {
        return Stream.of(
                arguments(new Integer[]{1, 2}, List.of(1, 2, 3)),
                arguments(new Integer[]{1}, List.of(1, 2, 3)),
                arguments(new Integer[]{1}, Set.of(1, 2, 3))
        );
    }

    List<?> get();

    @SuppressWarnings("unchecked")
    default <T> List<T> getChecked(Class<? extends T> cls) {
        return (List<T>) get();
    }

    @Test
    default void shouldBeCreatedWithoutElements() {
        //given
        var list = get();

        //then
        assertThat(list.size()).isEqualTo(0);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    default void shouldIncreaseSize() {
        //given
        var list = getChecked(String.class);

        //when
        list.add("a");
        list.add("a");
        list.add("a");
        list.add("a");

        //then
        assertThat(list.size()).isEqualTo(4);
    }

    @Test
    default void shouldReturnTrueWhenAdded() {
        //given
        var list = getChecked(String.class);

        //when + then
        for (int i = 0; i < 128; i++) {
            assertTrue(list.add("a"));
        }
    }

    @Test
    default void shouldAddAndGetInSameOrder() {
        //given
        var list = getChecked(Integer.class);
        var size = 4096;

        //when
        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        //then
        for (int i = 0; i < size; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    default void shouldThrowIndexOutOfBound() {
        //given
        var list = getChecked(Integer.class);

        //then
        IntStream.range(0, 128)
                .forEach(value -> {
                    assertThatExceptionOfType(IndexOutOfBoundsException.class)
                            .isThrownBy(() -> list.get(value));
                });
    }

    @Test
    default void shouldFindByIndex() {
        //given
        var list = getChecked(Integer.class);
        for (int i = 0; i < 521; i++) {
            list.add(i);
        }

        //then
        for (int i = 0; i < 521; i++) {
            assertEquals(i, list.indexOf(i));
        }
    }

    @Test
    default void shouldFindByIndexNull() {
        //given
        var list = getChecked(Integer.class);
        list.add(1);
        list.add(2);
        list.add(null);
        list.add(4);

        //then
        assertEquals(2, list.indexOf(null));
    }

    @Test
    default void shouldReturnFirstOccurrence() {
        //given
        var list = getChecked(Integer.class);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(4);
        list.add(4);

        //then
        assertEquals(1, list.indexOf(2));
        assertEquals(3, list.indexOf(4));
    }

    @ParameterizedTest
    @MethodSource("containsAllPositiveData")
    default void shouldContainsAll(Integer[] data, Collection<Integer> collection) {
        //given
        var list = getChecked(Integer.class);
        //noinspection ManualArrayToCollectionCopy
        for (Integer item : data) {
            list.add(item);
        }

        //then
        assertTrue(list.containsAll(collection));
    }

    @ParameterizedTest
    @MethodSource("containsAllNegativeData")
    default void shouldContainsAllNegative(Integer[] data, Collection<Integer> collection) {
        //given
        var list = getChecked(Integer.class);
        //noinspection ManualArrayToCollectionCopy
        for (Integer item : data) {
            //noinspection UseBulkOperation
            list.add(item);
        }

        //then
        assertFalse(list.containsAll(collection));
    }

    @Test
    default void shouldThrowNPEWhenAddAllNull() {
        //given
        var list = getChecked(Integer.class);

        //when + then
        assertThatNullPointerException()
                .isThrownBy(() -> list.addAll(null));
    }

    @Test
    default void shouldAddAllOnEmptyList() {
        //given
        var list = getChecked(Integer.class);
        var data = List.of(1, 2, 3, 4);

        //when
        var modified = list.addAll(data);

        //then
        assertTrue(modified);
        assertEquals(4, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
        assertEquals(4, list.get(3));
    }

    @RepeatedTest(512)
    default void shouldAddAllOnNotEmptyList() {
        //given
        var list = getChecked(Integer.class);
        var end = 4098 * 2;
        var r1 = RandomUtils.nextInt(1, end);
        var r2 = RandomUtils.nextInt(r1 + 1, end);

        for (int i = 0; i <= r1; i++) list.add(i);

        var data = new ArrayList<Integer>();
        for (int i = r1 + 1; i < r2; i++) data.add(i);

        //when
        var modified = list.addAll(data);

        //then
        for (int i = 0; i < r2; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @RepeatedTest(16)
    default void shouldRemoveFromStartWithIndex() {
        //given
        var size = RandomUtils.nextInt(1, 64);
        var list = get(size);

        //when + then
        for (int i = 0; i < size; i++) {
            assertEquals(i, list.remove(0));
        }
        assertTrue(list.isEmpty());
    }

    @RepeatedTest(16)
    default void shouldRemoveFromEndIndex() {
        //given
        var size = RandomUtils.nextInt(1, 64);
        var list = get(size);

        //when + then
        for (int i = 0; i < size; i++) {
            var index = list.size() - 1;
            assertEquals(index, list.remove(index));
        }
        assertTrue(list.isEmpty());
    }

    @RepeatedTest(16)
    default void shouldClear() {
        //given
        var size = RandomUtils.nextInt(1, 64);
        var list = get(size);

        //when
        list.clear();

        //then
        //noinspection ConstantConditions
        assertEquals(0, list.size());
    }

    @Test
    default void shouldRemoveObjectFromStart() {
        //given
        var list = get(3);

        //when
        list.remove((Object) 0);

        //then
        assertEquals(2, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    default void shouldRemoveObjectFromEnd() {
        //given
        var list = get(3);

        //when
        list.remove((Object) 2);

        //then
        assertEquals(2, list.size());
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
    }

    @Test
    default void shouldRemoveObjectFromMiddle() {
        //given
        var list = get(3);

        //when
        list.remove((Object) 1);

        //then
        assertEquals(2, list.size());
        assertEquals(0, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    default void shouldReturnFalseIfRemovingNotExistingElement() {
        //given
        var list = get(3);

        //when
        var removed = list.remove((Object) 4);

        //then
        assertFalse(removed);
        assertEquals(3, list.size());
    }

    @Test
    default void shouldCreateProperArray() {
        //given
        var list = get(3);

        //when
        var objects = list.toArray();

        //then
        assertThat(objects).containsExactly(0, 1, 2);
    }

    @Test
    default void shouldCreateProperNewArray() {
        //given
        var list = get(3);

        //when
        @SuppressWarnings("MismatchedReadAndWriteOfArray") var objects = list.toArray();
        objects[0] = "5";

        //then
        assertEquals(0, list.get(0));
    }

    default List<Integer> get(int size) {
        var list = getChecked(Integer.class);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        return list;
    }
}
