package com.tema1.players;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import com.tema1.common.Constants;
import com.tema1.goods.ComplexGoodsComparator;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsComparator;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.ReversedGoodsComparator;

public class BribedPlayer extends Player {

    /* imi trebuie pentru un inspect corect */
    private int credit = 0;

    public BribedPlayer(final int id, final String playStyle) {
        super(id, playStyle);
    }

    /* @Override */
    public Goods declareCard() {
        GoodsComparator goodsComparator = new GoodsComparator();
        Collections.sort(receivedCards, goodsComparator);
        /* daca are doar legale sau nu are bani de mita joaca normal */
        if (receivedCards.get(receivedCards.size() - 1).getId()
                <= Constants.LAST_LEGAL_ID || money <= Constants.SMALL_BRIBE) {
            return super.declareCard();
        } else {
            return GoodsFactory.getInstance().getGoodsById(0);
        }
    }

    /* metoda ajutatoare pentru fillSack, ca sa aflu cand e nevoie
       sa ma opresc cand pun ilegale */
    public int calculatePenalty() {
        int penalty = 0;
        for (Goods good : sack) {
            penalty += good.getPenalty();
        }
        return penalty;
    }

    /* @Override */
    public void fillSack() {
        int penalty = 0;
        ComplexGoodsComparator goodsComparator = new ComplexGoodsComparator();
        Collections.sort(receivedCards, goodsComparator);
        int countIllegals = 0;
        /* doar legale sau fara bani fix de o mita inseamna joc normal */
        if (receivedCards.get(receivedCards.size() - 1).getId()
                <= Constants.LAST_LEGAL_ID
                || money == Constants.SMALL_BRIBE) {
            Goods chosenGood = declareCard();
            for (Goods good : receivedCards) {
                if (good.getId() == chosenGood.getId()) {
                    sack.add(chosenGood);
                }
            }
            bribe = 0;
        } else {
            /* daca nu are bani de mita joaca tot normal */
            if (money < Constants.SMALL_BRIBE) {
                super.fillSack();
                bribe = 0;
                return;
            }
            if (money > Constants.SMALL_BRIBE) {
                /* baga cele mai mari carti pana cand se umple sacul
                   sau penalty-ul l-ar aduce pe 0 */
                for (int i = receivedCards.size() - 1; i >= 2; i--) {
                    sack.add(receivedCards.get(i));
                    penalty = calculatePenalty();
                    if (penalty >= money - Constants.ILLEGAL_PENALTY
                            && receivedCards.get(i - 1).getId()
                            > Constants.FROM_NOW_ILLEGAL) {
                        if (penalty <=  money - 2 * Constants.LEGAL_PENALTY) {
                            ArrayList<Goods> copyCards = new ArrayList<>();
                            copyCards.addAll(receivedCards);
                            ReversedGoodsComparator revComp
                            = new ReversedGoodsComparator();
                            Collections.sort(copyCards, revComp);
                            for (Goods good : copyCards) {
                                if (good.getId()
                                        < Constants.FROM_NOW_ILLEGAL) {
                                    sack.add(good);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    if (penalty >= money - Constants.LEGAL_PENALTY
                            && receivedCards.get(i - 1).getId()
                            < Constants.FROM_NOW_ILLEGAL) {
                        break;
                    }
                }
            }
            for (Goods good : sack) {
                if (good.getId() > Constants.FROM_NOW_ILLEGAL) {
                    countIllegals++;
                }
            }
            /* adauga mita in functie de ce carti are */
            if (countIllegals >= 1 && countIllegals <= 2) {
                bribe = Constants.SMALL_BRIBE;
                /* daca nu a mai abut bani sa bage ilegale dar isi permite riscul
                  sa bage o legala, o adauga */
                penalty = calculatePenalty();
                if (sack.size() == 1 && money - penalty > Constants.LEGAL_PENALTY) {
                    ArrayList<Goods> copyCards = new ArrayList<>();
                    copyCards.addAll(receivedCards);
                    ReversedGoodsComparator revComp = new ReversedGoodsComparator();
                    Collections.sort(copyCards, revComp);
                    for (Goods good : copyCards) {
                        if (good.getId() < Constants.FROM_NOW_ILLEGAL) {
                            sack.add(good);
                            break;
                        }
                    }
                }
            }
            if (countIllegals > 2) {
                bribe = Constants.BIG_BRIBE;
            }
        }
    }

    /* @Override */
    public void inspect(final Player player, final int noPlayers,
            final List<Integer> deck) {
        /* a trebuit sa duplic cod pentru situatiile cand
           jucatorul era la margini si inspecta stanga-drapta */
        if (getId() == 0) {
            if (player.getId() == 1
                    || player.getId() == (noPlayers - 1)) {
                /* daca risca sa ramana fara bani in urma inspectiei
                   trece peste */
                if (money < Constants.AFFORD_INSPECTION) {
                    return;
                }
                super.inspect(player, noPlayers, deck);
                return;
            }
        } else if (getId() == (noPlayers - 1)) {
            if (player.getId() == 0
                    || player.getId() == (noPlayers - 2)) {
                if (money < Constants.AFFORD_INSPECTION) {
                    return;
                }
                super.inspect(player, noPlayers, deck);
                return;
            }
        } else {
            if (player.getId() == (getId() - 1)
                    || player.getId() == (getId() + 1)) {
                if (money < Constants.AFFORD_INSPECTION) {
                    return;
                }
                super.inspect(player, noPlayers, deck);
                return;
            }
        }
        /* ca sa ma asigur ca mai intai verifica si apoi poate
           ia mita, a trebuit sa adaug un credit in care sa
           stochez eventuala mita. Daca nu faceam asa, pentru ca
           in main am un for de la 0 la noPlayers, exista riscul
           ca un bribedPlayer cu putini bani, care nu si-ar fi permis
           sa inspecteze, sa o faca, pentru ca mai intai lua mita. */
        if (player.bribe != 0) {
            credit += player.bribe;
            player.money = player.money - player.bribe;
        }
    }

    /* pun mita primita in contul sumei totale obtinute */
    public void getPaid() {
        money += credit;
        credit = 0;
    }
}
