package game;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author V
 */
public class Game extends StateBasedGame {
    
    AppGameContainer app;
    public static final int MAINMENUSTATE = 0;
    public static final int SINGLEPLAYERSTATE = 1;
    
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new SinglePlayerState(SINGLEPLAYERSTATE));
    }
    
    public static void main(String[] args) {
        new Game();
    }
    
    public Game() {
        super("4 hours of my life");
        try {
            app = new AppGameContainer(this);
            app.setDisplayMode(800, 600, false);
            app.start();
        } catch (SlickException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        enterState(SINGLEPLAYERSTATE);
    }
}