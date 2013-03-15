package server;

import game.GameField;
import java.util.Arrays;
import org.newdawn.slick.Graphics;

/**
 *
 * @author V
 */
public class NetworkGameField extends GameField {

    public long lastUpdate = 0;
    public boolean markedForTimeOut = false;

    @Override
    public void update(int delta) {
        lastUpdate = System.currentTimeMillis();
    }

    @Override
    public void render(Graphics g) {
        drawField(g);
    }
}