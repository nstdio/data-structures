package io.github.nstdio.datastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
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

    /**
     * The actual list implementation that should be tested.
     *
     * @return The list implementation.
     */
    List<?> get();

    /**
     * Whether list under the test permits {@code null} elements or not.
     */
    default boolean permitsNull() {
        return true;
    }

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
    default void shouldThrowWhenIndexNotFound() {
        //given
        var list = get();

        //then + when
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> list.get(0));
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
        assumePermitNull();

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
    default void shouldRemoveObjectFromEndAndAdd() {
        //given
        var list = get(3);

        //when
        list.remove((Object) 2);
        list.add(5);

        //then
        assertEquals(3, list.size());
        assertEquals(0, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(5, list.get(2));
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

    @Test
    default void shouldCreateProperArrayTyped() {
        //given
        var list = get(3);

        //when
        var objects = list.toArray(new Integer[0]);

        //then
        assertThat(objects).containsExactly(0, 1, 2);
    }

    @Test
    default void shouldCreateProperArrayTypedWithSize() {
        //given
        var list = get(3);

        //when
        Integer[] a = new Integer[3];
        var objects = list.toArray(a);

        //then
        assertThat(objects)
                .isSameAs(a)
                .containsExactly(0, 1, 2);
    }

    @Test
    default void shouldCreateIterator() {
        //given
        var list = get(3);
        var it = list.iterator();

        //when + then
        assertThat(it).toIterable().containsExactly(0, 1, 2);
        assertThat(it.hasNext()).isFalse();
    }

    @Test
    default void shouldThrowWhenHasNoElements() {
        //given
        var list = getChecked(String.class);
        var it = list.iterator();

        //when
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(it::next);
    }

    @Test
    default void shouldAddAllAtIndex() {
        //given
        var a = get(4);

        //when
        a.addAll(2, List.of(1, 1, 1, 1, 1));

        //then
        assertThat(a)
                .containsExactly(0, 1, 1, 1, 1, 1, 1, 2, 3);
    }

    @Test
    default void shouldAddAtIndexAtMiddle() {
        //given
        var a = get(4);

        //when
        a.add(2, 5);

        //then
        assertThat(a)
                .containsExactly(0, 1, 5, 2, 3);
    }

    @Test
    default void shouldAddAtIndexAtEnd() {
        //given
        var list = get(4);

        //when
        list.add(3, 5);
        list.add(6);

        //then
        assertThat(list)
                .containsExactly(0, 1, 2, 5, 3, 6);
    }

    @Test
    default void shouldAddAtIndex() {
        //given
        var list = get(1);
        var size = RandomUtils.nextInt(1, 255);

        //when
        for (int i = 0; i < size; i++) {
            list.add(0, i);
        }

        //then
        assertThat(list)
                .hasSize(size + 1);
    }

    @Test
    default void shouldRemoveAll() {
        //given
        var list = get(12);
        var remove = Set.of(4, 5, 8, 9, 15);

        //when
        var changed = list.removeAll(remove);

        //then
        assertTrue(changed);
        assertThat(list)
                .containsExactly(0, 1, 2, 3, 6, 7, 10, 11);
    }

    @Test
    @DisplayName("removeAll should return false when not changed")
    default void shouldRemoveAll2() {
        //given
        var list = get(5);
        var remove = Set.of(10, 11);

        //when
        var changed = list.removeAll(remove);

        //then
        assertFalse(changed);
        assertThat(list)
                .containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    @DisplayName("removeAll should return false when collection is empty")
    default void shouldRemoveAll3() {
        //given
        var list = get(5);
        var remove = Set.<Integer>of();

        //when
        var changed = list.removeAll(remove);

        //then
        assertFalse(changed);
        assertThat(list)
                .containsExactly(0, 1, 2, 3, 4);
    }

    @Test
    @DisplayName("lastIndexOf should return proper index")
    default void lastIndexOf() {
        //given
        var list = getChecked(Integer.class);
        list.add(1);
        list.add(2);
        list.add(2);
        list.add(3);

        //when
        var lastIndexOf = list.lastIndexOf(2);

        //then
        assertEquals(2, lastIndexOf);
    }

    @Test
    @DisplayName("lastIndexOf should return proper index")
    default void lastIndexOfNull() {
        assumePermitNull();

        //given
        var list = getChecked(Integer.class);
        list.add(null);
        list.add(null);
        list.add(null);

        //when
        var lastIndexOf = list.lastIndexOf(null);

        //then
        assertEquals(2, lastIndexOf);
    }

    @Test
    @DisplayName("Should retail all provided values")
    default void retainAll() {
        //given
        var list = get(5);
        var retain = Set.of(0, 3);

        //when
        list.retainAll(retain);

        //then
        assertThat(list).containsExactly(0, 3);
    }

    @Test
    default void shouldSetAtIndexAtStart() {
        //given
        var list = get(2);

        //when
        var old = list.set(0, 2);

        //then
        assertThat(list).containsExactly(2, 1);
        assertThat(old).isEqualTo(0);
    }

    @Test
    default void shouldSetAtIndexAtEnd() {
        //given
        var list = get(3);

        //when
        var old = list.set(2, 10);

        //then
        assertThat(list).containsExactly(0, 1, 10);
        assertThat(old).isEqualTo(2);
    }

    @Test
    default void iteratorWithForLoop() {
        //given
        final int size = 10;
        var it = get(size).iterator();

        //when + then
        for (int i = 0; i < size; i++) {
            assertTrue(it.hasNext());
            assertEquals(i, it.next());
        }
    }

    @Test
    default void iteratorWithWhileLoop() {
        //given
        final int size = 10;
        var it = get(size).iterator();

        //when + then
        int i = 0;
        while (it.hasNext()) {
            assertEquals(i++, it.next());
        }
        assertEquals(size, i);
    }

    private void assumePermitNull() {
        assumeTrue(permitsNull(), "This list does not support null elements.");
    }

    default List<Integer> get(int size) {
        var list = getChecked(Integer.class);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }

        return list;
    }
}
