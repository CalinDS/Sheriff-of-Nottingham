package com.tema1.goods;

import java.util.Comparator;

public final class ComplexGoodsComparator implements Comparator<Goods> {

    @Override
    public int compare(final Goods arg0, final Goods arg1) {
        // TODO Auto-generated method stub
        if (arg0.getProfit() == arg1.getProfit()) {
            return arg0.getId() - arg1.getId();
        }
        return arg0.getProfit() - arg1.getProfit();
    }

}
