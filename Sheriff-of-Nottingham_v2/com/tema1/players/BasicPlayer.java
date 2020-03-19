package com.tema1.players;

import java.util.List;
import com.tema1.common.Constants;

/* aprope identic cu Player */
public class BasicPlayer extends Player {

    public BasicPlayer(final int id, final String playStyle) {
        super(id, playStyle);
    }

    /* @Override  pentru ca trebuie sa iau in considerare cazul in care
      ar ramane pe 0 cu banii*/
    public void inspect(final Player player, final int noPlayers,
            final List<Integer> deck) {
        if (money < Constants.AFFORD_INSPECTION) {
            return;
        }
        super.inspect(player, noPlayers, deck);
    }
}
