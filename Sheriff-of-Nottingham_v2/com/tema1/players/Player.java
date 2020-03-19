package com.tema1.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import com.tema1.common.Constants;
import com.tema1.goods.Goods;
import com.tema1.goods.GoodsComparator;
import com.tema1.goods.GoodsComparatorById;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.LegalGoods;
import com.tema1.goods.IllegalGoods;


/* clasa de jucatori, cea mai importanta a programului */
public abstract class Player {
    /* am considerat ca e suficient sa am atributele protected,
       avand in vedere ca tip-urile de playeri sunt asa legate de
       parinte */
    protected int id;
    protected String playStyle;
    protected int money;
    protected ArrayList<Goods> receivedCards = new ArrayList<Goods>();
    protected ArrayList<Goods> sack = new ArrayList<Goods>();
    protected ArrayList<Goods> stand = new ArrayList<Goods>();
    protected int bribe = 0;
    protected int goodFreq = 0;
    protected int card = Constants.INITIAL_MONEY;

    public Player(final int id, final String playStyle) {
        this.id = id;
        this.playStyle = playStyle;
        this.money = Constants.INITIAL_MONEY;
    }

    /* pune cartile in mana */
    public void addToHand(final Goods good) {
        receivedCards.add(good);
    }

    /* declara cartea ca un basic */
    public Goods declareCard() {
        GoodsComparator goodsComparator = new GoodsComparator();
        Collections.sort(receivedCards, goodsComparator);
        ArrayList<Goods> auxList = new ArrayList<Goods>();
        /*daca nu are legale, spune ca are mere */
        if (receivedCards.get(0).getId() > Constants.LAST_LEGAL_ID) {
            return GoodsFactory.getInstance().getGoodsById(0);
        } else {
            GoodsComparator goodsComp = new GoodsComparator();
            Collections.sort(receivedCards, goodsComp);
            /*elimin cartile ilegale */
            int legalOnly = 0;
            for (int i = 0; i < receivedCards.size(); i++) {
                if (receivedCards.get(i).getId() > Constants.FROM_NOW_ILLEGAL) {
                    legalOnly = i;
                    break;
                }
            }
            if (legalOnly != 0) {
                for (int i = legalOnly; i < Constants.CARDS_PER_HAND; i++) {
                    receivedCards.remove(legalOnly);
                }
            }
            /* aflu cele mai frecvente carti */
            int maxCount = 1;
            int currCount = 1;
            GoodsComparatorById goodsCompId = new GoodsComparatorById();
            Collections.sort(receivedCards, goodsCompId);
            for (int i = 1; i < receivedCards.size(); i++) {
                if (receivedCards.get(i).getId() == receivedCards.get(i - 1).getId()) {
                    currCount++;
                    /* trebuie tratat special cazul cand ajung la final de vector */
                    if (i == receivedCards.size() - 1) {
                        if (currCount == maxCount) {
                            maxCount = currCount;
                            auxList.add(receivedCards.get(i - 1));
                        } else if (currCount > maxCount) {
                            maxCount = currCount;
                            auxList.clear();
                            auxList.add(receivedCards.get(i - 1));
                        }
                    }
                } else {
                    if (currCount == maxCount) {
                        maxCount = currCount;
                        auxList.add(receivedCards.get(i - 1));
                    } else if (currCount > maxCount) {
                        maxCount = currCount;
                        auxList.clear();
                        auxList.add(receivedCards.get(i - 1));
                    }
                    currCount = 1;
                }
            }
            /*tot pentru ultimul element */
            if (maxCount == 1) {
                auxList.add(receivedCards.get(receivedCards.size() - 1));
            }
            /* declar cartea conform cerintei */
            Collections.sort(auxList, goodsComp);
            return auxList.get(auxList.size() - 1);
        }
    }

    /* umple sacul pe strategia de baza */
    public void fillSack() {
        GoodsComparator goodsComparator = new GoodsComparator();
        Collections.sort(receivedCards, goodsComparator);
        if (receivedCards.get(0).getId() > Constants.LAST_LEGAL_ID) {
            sack.add(receivedCards.get(receivedCards.size() - 1));
        } else {
            card = declareCard().getId();
            Goods chosenGood = declareCard();
            for (Goods good : receivedCards) {
                if (good.getId() == chosenGood.getId()) {
                    sack.add(chosenGood);
                }
            }
        }
    }

    /* muta din sac pe taraba */
    public void fillStand() {
        for (Goods good : sack) {
            stand.add(good);
        }
        sack.clear();
        receivedCards.clear();
    }

    /* joaca rolul de serif */
    public void inspect(final Player player, final int noPlayers,
            final List<Integer> deck) {
        /* pentru ca declare-ul meu modifica receivedCards, il pastrez
           in card */
        card = player.declareCard().getId();
        int countLies = 0;
        /* aflu cate carti care nu corespund cu declaratia sunt */
        for (Goods good : player.sack) {
            if (player.declareCard().getId() != good.getId()) {
                countLies++;
            }
        }
        /* daca nu minte e clar ca trebuie sa platesc penalty pe toate
           cartile lui */
        if (countLies == 0) {
            money  = money - player.sack.size() * Constants.LEGAL_PENALTY;
            player.money = player.money + player.sack.size() * Constants.LEGAL_PENALTY;
        } else {
            ArrayList<Goods> copy = new ArrayList<Goods>();
            for (Goods good : player.sack) {
                if (sack.size() != 0) {
                    /* elimin riscul schimbarii cartii declarate */
                    card = player.sack.get(sack.size() - 1).getId();
                }
                if (player.declareCard().getId()
                        != good.getId() || good.getId() != card) {
                    money = money + good.getPenalty();
                    player.money = player.money - good.getPenalty();
                    /* pun cartea la finalul gramezii */
                    deck.add(good.getId());
                } else {
                    copy.add(good);
                }
            }
            /* sterg toate cartile care nu sunt in conformitate cu declaratia */
            player.sack.clear();
            player.sack = copy;
        }
    }

    /* daca am carti ilegale pe taraba, aplic bonusurile */
    public void addIllegalBonuses() {
        /* am nevoie de acest array pentru ca modific stand */
        ArrayList<Goods> auxTable = new ArrayList<>();
        auxTable.addAll(stand);
        for (Goods good : auxTable) {
            if (good.getId() > Constants.FROM_NOW_ILLEGAL) {
                Map<Goods, Integer> illBonus =
                        ((IllegalGoods) good).getIllegalBonus();
                for (Goods legalGood : illBonus.keySet()) {
                    for (int i = 0; i < illBonus.get(legalGood); i++) {
                        stand.add(legalGood);
                    }
                }
            }
        }
    }

    /* aflu cati bani va face taraba */
    public void calculateStandProfit() {
        for (Goods good : stand) {
            money = money + good.getProfit();
        }
    }

    /* pentru bonusurile de queen si king, care nu tin de instanta, ci mai degraba
       de clasa */
    public static void addRegalBonuses(final ArrayList<Player> players, final int itemId) {
        LegalGoods item =
                (LegalGoods) GoodsFactory.getInstance().getGoodsById(itemId);
        for (Player player : players) {
            player.goodFreq = 0;
            for (Goods good : player.stand) {
                if (good.getId() == itemId) {
                    player.goodFreq++;
                }
            }
        }
        BonusesComparator kingBonusComp = new BonusesComparator();
        Collections.sort(players, kingBonusComp);
        /* iau si cazurile cand nu am deloc acel tip de bun */
        if (players.get(0).goodFreq == 0) {
            return;
        } else {
            players.get(0).money += item.getKingBonus();
        }
        if (players.get(1).goodFreq == 0) {
            return;
        } else {
            players.get(1).money += item.getQueenBonus();
        }
    }


    /* pentru pretty print */
    public String toString() {
        return id + " " + playStyle.toUpperCase() + " " + money;
    }

    /* for checker */
    public int getId() {
        return id;
    }

    /* for checker */
    public void setId(final int id) {
        this.id = id;
    }

    /* for checker */
    public String getPlayStyle() {
        return playStyle;
    }

    /* for checker */
    public void setPlayStyle(final String playStyle) {
        this.playStyle = playStyle;
    }

    /* for checker */
    public int getMoney() {
        return money;
    }

    /* for checker */
    public void setMoney(final int money) {
        this.money = money;
    }

    /* for checker */
    public int getBribe() {
        return bribe;
    }

    /* for checker */
    public void setBribe(final int bribe) {
        this.bribe = bribe;
    }

    /* for checker */
    public int getGoodFreq() {
        return goodFreq;
    }

    /* for checker */
    public void setGoodFreq(final int goodFreq) {
        this.goodFreq = goodFreq;
    }

    /* for checker */
    public int getCard() {
        return card;
    }

    /* for checker */
    public void setCard(final int card) {
        this.card = card;
    }

    /* for checker */
    public ArrayList<Goods> getReceivedCards() {
        return receivedCards;
    }

    /* for checker */
    public void setReceivedCards(final ArrayList<Goods> receivedCards) {
        this.receivedCards = receivedCards;
    }

    /* for checker */
    public ArrayList<Goods> getSack() {
        return sack;
    }

    /* for checker */
    public void setSack(final ArrayList<Goods> sack) {
        this.sack = sack;
    }

    /* for checker */
    public ArrayList<Goods> getStand() {
        return stand;
    }

    /* for checker */
    public void setStand(final ArrayList<Goods> stand) {
        this.stand = stand;
    }

}











