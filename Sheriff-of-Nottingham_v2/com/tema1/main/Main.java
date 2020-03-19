package com.tema1.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tema1.common.Constants;
import com.tema1.goods.GoodsFactory;
import com.tema1.players.BasicPlayer;
import com.tema1.players.BribedPlayer;
import com.tema1.players.GreedyPlayer;
import com.tema1.players.Player;
import com.tema1.players.PlayerComparator;


public final class Main {
    private Main() {
        // just to trick checkstyle
    }

    public static void main(final String[] args) throws IOException {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();

        //TODO implement homework logic

        /* redenumesc datele de intrare ca sa lucrez mai ok cu ele */
        int noRounds = gameInput.getRounds();
        int noPlayers = gameInput.getPlayerNames().size();
        List<String> playersType = (List<String>) gameInput.getPlayerNames();
        List<Integer> cardsId = (List<Integer>) gameInput.getAssetIds();

        /* initializez jucatorii */
        ArrayList<Player> players = new ArrayList<Player>(noPlayers);
        for (int i = 0; i < noPlayers; i++) {
            if (playersType.get(i).contentEquals("basic")) {
                players.add(new BasicPlayer(i, playersType.get(i)));
            } else if (playersType.get(i).contentEquals("greedy")) {
                players.add(new GreedyPlayer(i, playersType.get(i)));
            } else {
                players.add(new BribedPlayer(i, playersType.get(i)));
            }
        }

        for (int l = 0; l < noRounds; l++) {
            /* pe rand fiecare jucator va fi serif */
            for (int i = 0; i < noPlayers; i++) {
                for (int j = 0; j < noPlayers; j++) {
                    /* iar ceilalti comercianti */
                    if (j != i) {
                        /* fiecare isi primeste cartile, care dispar din pachet */
                        for (int k = 0; k < Constants.CARDS_PER_HAND; k++) {
                            players.get(j).addToHand(GoodsFactory.
                                    getInstance().
                                    getGoodsById(cardsId.get(0)));
                            cardsId.remove(0);
                        }
                        /* fiecare jucator declara carte, umple sac, e inspectat
                          si apoi isi umple taraba */
                        /* fac cazul de overload */
                        if (players.get(j).getPlayStyle().equals("greedy")) {
                            ((GreedyPlayer) players.get(j)).fillSack(l + 1);
                        } else {
                            players.get(j).fillSack();
                        }
                        players.get(i).inspect(players.get(j), noPlayers, cardsId);
                        players.get(j).fillStand();
                    }
                }
                /* tratez special inspectia la bribed */
                if (players.get(i).getPlayStyle().equals("bribed")) {
                    ((BribedPlayer) players.get(i)).getPaid();
                }
            }
        }
        /* adaug bonusurile ilegale, inainte sa le calculez pe cele regale,
         * pentru ca ele adauga alte legale pe taraba */
        for (Player player : players) {
            /* bonusurile ilegale influenteaza bonusurile regale deci
               se calculeaza primele */
            player.addIllegalBonuses();
            /* pot face profitul acum, cu toate bunurile pe taraba */
            player.calculateStandProfit();
        }

        /* calculez bonusurile de king si queen pentru fiecare tip
         * de bun legal */
        for (int i = 0; i < Constants.NO_OF_LEGAL_GOODS; i++) {
            Player.addRegalBonuses(players, i);
        }

        /* sortez jucatorii in clasament si il afisez */
        PlayerComparator playerComp = new PlayerComparator();
        Collections.sort(players, playerComp);

        for (Player player : players) {
            System.out.println(player);
        }
        System.out.println();
    }
}
