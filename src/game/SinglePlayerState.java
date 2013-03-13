package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author V
 */
public class SinglePlayerState extends BasicGameState {
    
    private int stateID;
    private GameField theGame;
    
    public SinglePlayerState(int id) {
        stateID = id;
        theGame = new GameField();
    }
    
    @Override
    public int getID() {
        return stateID;
    }
    
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        container.setVSync(true);
        container.setAlwaysRender(true);
    }
    
    @Override
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_X:
                theGame.rotateLeft();
                break;
            case Input.KEY_UP:
            case Input.KEY_C:
                theGame.rotateRight();
                break;
            case Input.KEY_RIGHT:
                theGame.right();
                break;
            case Input.KEY_LEFT:
                theGame.left();
                break;
            case Input.KEY_SPACE:
            case Input.KEY_DOWN:
            case Input.KEY_LSHIFT:
                theGame.drop();
                break;
        }
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.translate(50f, 50f);
        theGame.render(g);
    }
    
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        theGame.update(delta);
    }
}