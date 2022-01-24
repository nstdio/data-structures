package io.github.nstdio.ds.map;

import io.github.nstdio.ds.map.AVLTreeMap.AVLNode;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AVLTreeMapTest implements MapContract {
    static <K, V> void assertBalanced(AVLNode<K, V> n) {
        if (n == null)
            return;

        assertThat(n).satisfiesAnyOf(
                Assertions::assertNull,
                left -> assertThat(left.balance()).isBetween(-1, 1)
        );

        assertBalanced(n.left());
        assertBalanced(n.right());
    }

    static <K, V> void assertBalanced(AVLTreeMap<K, V> n) {
        assertBalanced(n.root());
    }

    @Override
    public <K, V> Map<K, V> get() {
        return new AVLTreeMap<>();
    }

    @Test
    void shouldPutSimple() {
        //given
        var map = new AVLTreeMap<Integer, Integer>();
        var size = 7;

        //when
        for (int i = 1; i <= size; i++) {
            map.put(i, 1);
        }

        //when
        for (int i = 1; i <= size; i++) {
            assertThat(map.get(i)).isEqualTo(1);
        }
        assertBalanced(map);
    }

    @Test
    void shouldStayBalancedWithRandomValues() {
        //given
        var map = new AVLTreeMap<Integer, Integer>();
        var size = 4096 * 8;

        //when
        while (map.size() != size) {
            map.put(RandomUtils.nextInt(), 0);
        }

        //then
        assertBalanced(map);
    }

    @Test
    void shouldUniformPut() {
        //given
        var map = new AVLTreeMap<Integer, Integer>();
        var size = 1024;

        //when
        for (int i = 1; i <= size; i++) {
            map.put(i, i);
        }

        //then
        assertBalanced(map);
    }

    private AVLTreeMap<Integer, Integer> mapWithSize(int size) {
        AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();

        for (int i = 1; i <= size; i++) {
            map.put(i, 1);
        }

        return map;
    }

    private void assertDisposed(AVLNode<Integer, Integer> node) {
        assertThat(node).hasAllNullFieldsOrProperties();
    }

    @Nested
    class SimpleRemove {
        @Test
        void shouldRemoveFromRight() {
            //given
            AVLTreeMap<Integer, Integer> map = mapWithSize(7);

            //when
            map.remove(6);

            //then
            assertThat(map.root().getKey()).isEqualTo(4);
            assertThat(map.root().right().getKey()).isEqualTo(7);

            assertBalanced(map);
        }

        @Test
        void shouldRemoveRoot() {
            //given
            AVLTreeMap<Integer, Integer> map = mapWithSize(7);

            //when
            map.remove(4);

            //then
            var root = map.root();
            var r = root.right();
            var l = root.left();
            assertThat(root)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::parent)
                    .containsExactly(5, 0, null);
            assertThat(r)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::isLeaf)
                    .containsExactly(6, -1, false);
            assertThat(l)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::isLeaf)
                    .containsExactly(2, 0, false);
            assertBalanced(root);
        }

        @Test
        void shouldBalanceOnRemove() {
            //given
            AVLTreeMap<Integer, Integer> map = mapWithSize(9);
            var node = map.node(5);

            //when
            map.remove(5);

            assertThat(map.keySet())
                    .containsExactly(1, 2, 3, 4, 6, 7, 8, 9);
            assertBalanced(map);
            assertDisposed(node);
        }

        @Test
        void shouldRemoveWhenDontHaveRightChild() {
            //given
            AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
            map.put(2, 1);
            map.put(1, 1);
            map.put(4, 1);
            map.put(3, 1);
            var node = map.node(4);

            //when
            map.remove(4);

            //then
            assertThat(map.keySet())
                    .containsExactly(1, 2, 3);
            assertBalanced(map);
            assertDisposed(node);
        }

        @Test
        void shouldRemove2() {
            //given
            AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
            map.put(1, 1);
            map.put(2, 1);
            var node = map.node(2);

            //when
            map.remove(2);

            //then
            assertThat(map.keySet()).containsExactly(1);
            assertDisposed(node);
            assertBalanced(map);
        }

        @Test
        void shouldRemove3() {
            //given
            AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
            map.put(1, 1);
            map.put(2, 1);
            var node = map.node(1);

            //when
            map.remove(1);

            //then
            assertThat(map.keySet()).containsExactly(2);
            assertDisposed(node);
            assertBalanced(map);
        }

        @Test
        void shouldRemove4() {
            //given
            AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
            map.put(2, 1);
            map.put(1, 1);
            var node = map.node(2);

            //when
            map.remove(2);

            //then
            assertThat(map.keySet()).containsExactly(1);
            assertDisposed(node);
            assertBalanced(map);
        }
    }

    @Nested
    class SimpleRotations {
        @Test
        void shouldBeBalancesRR() {
            var map = new AVLTreeMap<Integer, Integer>();

            //when
            map.put(1, 1);
            map.put(2, 1);
            map.put(3, 1);

            //then
            assertBalancedWith231(map);
        }

        private void assertBalancedWith231(AVLTreeMap<Integer, Integer> map) {
            var root = map.root();
            var r = root.right();
            var l = root.left();
            assertThat(root)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::parent)
                    .containsExactly(2, 0, null);
            assertThat(r)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::isLeaf)
                    .containsExactly(3, 0, true);
            assertThat(l)
                    .extracting(AVLNode::getKey, AVLNode::balance, AVLNode::isLeaf)
                    .containsExactly(1, 0, true);
        }

        @Test
        void shouldPerformLL() {
            var map = new AVLTreeMap<Integer, Integer>();

            //when
            map.put(3, 1);
            map.put(2, 1);
            map.put(1, 1);

            //then
            assertBalancedWith231(map);
        }

        @Test
        void shouldPerformRL() {
            var map = new AVLTreeMap<Integer, Integer>();

            //when
            map.put(1, 1);
            map.put(3, 1);
            map.put(2, 1);

            //then
            assertBalancedWith231(map);
        }

        @Test
        void shouldPerformLR() {
            var map = new AVLTreeMap<Integer, Integer>();

            //when
            map.put(2, 1);
            map.put(1, 1);
            map.put(3, 1);

            //then
            assertBalancedWith231(map);
        }
    }
}