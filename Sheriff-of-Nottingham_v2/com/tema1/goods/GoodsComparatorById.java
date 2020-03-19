package com.tema1.goods;

import java.util.Comparator;

public final class GoodsComparatorById implements Comparator<Goods> {

    @Override
    public int compare(final Goods arg0, final Goods arg1) {
        // TODO Auto-generated method stub
        return arg0.getId() - arg1.getId();
    }
}
