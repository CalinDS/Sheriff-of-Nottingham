package com.tema1.players;

import java.util.Comparator;

public final class PlayerComparator implements Comparator<Player> {

    @Override
    public int compare(final Player arg0, final Player arg1) {
        // TODO Auto-generated method stub
        return arg1.money - arg0.money;
    }

}
