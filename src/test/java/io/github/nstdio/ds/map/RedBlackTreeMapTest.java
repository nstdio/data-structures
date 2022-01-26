package io.github.nstdio.ds.map;

import io.github.nstdio.ds.map.RedBlackTreeMap.Node;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.github.nstdio.ds.map.RedBlackTreeMap.BLACK;
import static io.github.nstdio.ds.map.RedBlackTreeMap.RED;
import static org.assertj.core.api.Assertions.assertThat;

class RedBlackTreeMapTest implements MapContract {

    @Override
    public <K, V> Map<K, V> get() {
        return new RedBlackTreeMap<>();
    }

    void assertProperRoot(Node<?, ?> n) {
        assertThat(n.color()).isEqualTo(BLACK);
        assertThat(n.parent()).isNull();
    }

    @Test
    void shouldStayBalancedWithRandomValues() {
        //given
        var map = new RedBlackTreeMap<Integer, Integer>();
        var size = 4096 * 8;

        //when
        while (map.size() != size) {
            map.put(RandomUtils.nextInt(), 0);
        }

        //then
        assertThat(map.isRedBlackTree()).isTrue();
    }

    @Nested
    class SimpleDelete {
        @Test
        void one() {
            //given
            var map = new RedBlackTreeMap<Integer, Integer>();

            map.put(1, 1);
            map.put(2, 1);
            map.put(3, 1);
            map.put(4, 1);

            //when
            map.remove(3);

            //then
            assertThat(map.keySet())
                    .contains(1, 2, 4)
                    .doesNotContain(3);
            assertThat(map.isRedBlackTree()).isTrue();
        }
    }

    @Nested
    class SimplePut {
        @Test
        void first() {
            //given
            var map = new RedBlackTreeMap<Integer, Integer>();
            Node<Integer, Integer> root, left;

            //when
            map.put(2, 1);
            map.put(1, 1);
            root = map.node(2);
            left = map.node(1);

            //then
            assertProperRoot(root);
            assertThat(left.color()).isEqualTo(RED);
        }

        @Test
        void second() {
            //given
            var map = new RedBlackTreeMap<Integer, Integer>();

            //when
            map.put(1, 1);
            map.put(2, 1);
            map.put(3, 1);

            //then
            assertProperRoot(map.root());
            assertThat(map.root().color()).isEqualTo(BLACK);
            assertThat(map.root().left().color()).isEqualTo(RED);
            assertThat(map.root().right().color()).isEqualTo(RED);
        }
    }
}