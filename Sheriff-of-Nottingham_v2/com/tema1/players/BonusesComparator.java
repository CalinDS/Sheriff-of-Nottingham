package com.tema1.players;

import java.util.Comparator;

public class BonusesComparator implements Comparator<Player> {

    @Override
    public final int compare(final Player arg0, final Player arg1) {
        // TODO Auto-generated method stub
        if (arg1.goodFreq == arg0.goodFreq) {
            return arg0.getId() - arg1.getId();
        }
        return arg1.goodFreq - arg0.goodFreq;
    }

}
