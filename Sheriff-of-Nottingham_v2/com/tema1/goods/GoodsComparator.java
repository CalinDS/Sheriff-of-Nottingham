package com.tema1.goods;

import java.util.Comparator;

public final class GoodsComparator implements Comparator<Goods> {

    @Override
    public int compare(final Goods arg0, final Goods arg1) {
        // TODO Auto-generated method stub
        return arg0.getProfit() - arg1.getProfit();
    }

}
