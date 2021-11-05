package io.github.nstdio.datastructure.list;

import java.util.List;

class Lists {
    static void uniformFill(List<Integer> list, int len) {
        for (int i = 0; i < len; i++) {
            list.add(i);
        }
    }
}
