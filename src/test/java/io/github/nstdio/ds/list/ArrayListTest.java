package io.github.nstdio.ds.list;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayListTest implements ListContract {
    @Override
    public List<?> get() {
        return getImpl();
    }

    private ArrayList<?> getImpl() {
        return new ArrayList<>();
    }

    private ArrayList<Integer> getImplChecked() {
        return new ArrayList<>();
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 7, 8, 9})
    void shouldNotCreateMemoryLeakWhenRemovingByIndex(int size) {
        //given
        var list = getImplChecked();
        Lists.uniformFill(list, size);

        //when + then
        for (int i = 0; i < size; i++) {
            assertEquals(i, list.remove(0));
        }

        assertThat(list.size()).isZero();
        assertThat(list.data())
                .allMatch(Objects::isNull);
    }
}