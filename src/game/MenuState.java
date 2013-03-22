package game;

import java.util.Arrays;
import org.newdawn.slick.Color;
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
public class MenuState extends BasicGameState {

    private int stateID;
    private String[] menuPoints;
    private int selection = 0;
    private GameContainer container;
    private StateBasedGame game;

    public MenuState(int id) {
        stateID = id;
    }

    @Override
    public int getID() {
        return stateID;
    }

    private enum MainMenu {

        Local,
        Online,
        Quit
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.container = container;
        this.game = game;
        menuPoints = new String[MainMenu.values().length];
        for (int i = 0; i < MainMenu.values().length; i++) {
            menuPoints[i] = MainMenu.values()[i].toString();
        }
        System.out.println(Arrays.toString(menuPoints));
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        for (int i = 0; i < menuPoints.length; i++) {
            String string = menuPoints[i];
            if (i == selection) {
                g.setColor(Color.cyan);
            } else {
                g.setColor(Color.white);
            }
            g.drawString(string, 100, container.getHeight() / 2 + 50 * i);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    }

    @Override
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_UP:
                selection--;
                if (selection < 0) {
                    selection = menuPoints.length-1;
                }
                break;
            case Input.KEY_DOWN:
                selection++;
                if (selection >= menuPoints.length) {
                    selection = 0;
                }
                break;
            case Input.KEY_ESCAPE:
                container.exit();
                break;
            case Input.KEY_SPACE:
            case Input.KEY_ENTER:
                switch (MainMenu.values()[selection]) {
                    case Local:
                        game.enterState(Game.SINGLEPLAYERSTATE);
                        break;
                    case Online:
                        game.enterState(Game.SELFRELAYSTATE);
                        break;
                    case Quit:
                        container.exit();
                        break;
                }
                break;
        }
    }
}