package io.github.nstdio.ds;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PriorityQueueTest {

    @Test
    void simple() {
        //given
        var pq = new PriorityQueue<Integer>();
        var size = 256;

        //when
        offerUniform(pq, size);

        //then
        for (int i = size; i >= 0; i--)
            assertEquals(i, pq.poll());
    }

    @Test
    void shouldStaySortedUnderRandom() {
        //given
        var pq = new PriorityQueue<Integer>();
        var size = 256;

        //when
        while (pq.size() != size) {
            pq.offer(RandomUtils.nextInt());
        }

        //then
        assertThat(new ArrayList<>(pq)).isSorted();
    }

    @Test
    void simpleWithComparator() {
        //given
        var pq = new PriorityQueue<Integer>(Comparator.reverseOrder());
        var size = 256;

        //when
        for (int i = size; i >= 0; i--)
            pq.offer(i);

        //then
        for (int i = 0; i <= size; i++)
            assertEquals(i, pq.poll());
    }

    private void offerUniform(Queue<Integer> pq, int size) {
        for (int i = 0; i <= size; i++)
            pq.offer(i);
    }
}