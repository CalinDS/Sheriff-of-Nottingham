package com.tema1.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsComparator;

public class GreedyPlayer extends Player {

    public GreedyPlayer(final int id, final String playStyle) {
        super(id, playStyle);
    }

    /* @Overload */
    public void fillSack(final int round) {
        /* fillSack-ul parinte va modifica receivedCards asa ca
           il voi salva in alt array */
        ArrayList<Goods> copyHand = new ArrayList<Goods>();
        GoodsComparator goodsComparator = new GoodsComparator();
        Collections.sort(receivedCards, goodsComparator);
        copyHand.addAll(receivedCards);
        super.fillSack();
        /* tratez cazul cu runda para */
        if (round % 2 == 0) {
            if (sack.size() < Constants.FULL_SACK) {
                /* daca greedy primeste doar ilegale, isi va pune una in sac,
                   deci acum o va lua pe a doua cea mai valoroasa */
                if (copyHand.get(0).getId() > Constants.LAST_LEGAL_ID
                        && copyHand.get(copyHand.size() - 1).getId()
                        > Constants.LAST_LEGAL_ID) {
                    sack.add(copyHand.get(copyHand.size() - 2));
                    return;
                }
                /* altfel o ia pe cea mai valoroasa, daca exista */
                if (copyHand.get(copyHand.size() - 1).getId() > Constants.LAST_LEGAL_ID) {
                    sack.add(copyHand.get(copyHand.size() - 1));
                    return;
                }
            }
        }
    }

    /* inspect-ul e la fel, doar ca el mai primeste si mita */
    public void inspect(final Player player, final int noPlayers,
            final List<Integer> deck) {
        if (player.bribe != 0) {
            money = money + player.bribe;
            player.money = player.money - player.bribe;
            return;
        }
        if (money >= Constants.AFFORD_INSPECTION) {
            super.inspect(player, noPlayers, deck);
        }
    }
}
