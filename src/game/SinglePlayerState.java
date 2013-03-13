package game;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SinglePlayerState extends BasicGameState {

    Random random = new Random();
    private long triforce = 0;
    public int rows = 16;
    public int cols = 9;
    public int[] field = new int[rows * cols];
    public int currentX;
    public int currentY;
    private int[] currentBlock;
    private int currentBlockSize;
    private int stateID;

    public SinglePlayerState(int id) {
        stateID = id;
    }

    @Override
    public int getID() {
        return stateID;
    }

    public enum Block {

        J,
        L,
        I,
        O,
        Z,
        S,
        T
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        container.setVSync(true);
        container.setAlwaysRender(true);
        resetGame();
    }

    private void setCurrentBlock(int[] newBlock) {
        currentBlock = newBlock;
        currentBlockSize = (int) Math.sqrt(currentBlock.length);
    }

    public void soutBlock(int[] block) {
        for (int i = 0; i < currentBlock.length; i = i + currentBlockSize) {
            System.out.println(block[i] + "" + block[i + 1] + "" + block[i + 2] + "" + block[i + 3]);
        }
    }

    public int[] block(Block type) {
        switch (type) {
            case J:
                return new int[]{0, 1, 0, 0, 1, 0, 1, 1, 0};
            case L:
                return new int[]{0, 2, 0, 0, 2, 0, 0, 2, 2};
            case I:
                return new int[]{0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0};
            case O:
                return new int[]{4, 4, 4, 4};
            case Z:
                return new int[]{0, 0, 5, 0, 5, 5, 0, 5, 0};
            case S:
                return new int[]{6, 0, 0, 6, 6, 0, 0, 6, 0};
            case T:
                return new int[]{0, 7, 0, 7, 7, 7, 0, 0, 0};
        }
        return new int[16];
    }

    @Override
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_X:
                if (canMove(currentX, currentY, rotateLeft(currentBlock))) {
                    currentBlock = rotateLeft(currentBlock);
                }
                break;
            case Input.KEY_UP:
            case Input.KEY_C:
                if (canMove(currentX, currentY, rotateRight(currentBlock))) {
                    currentBlock = rotateRight(currentBlock);
                }
                break;
            case Input.KEY_RIGHT:
                if (canMove(currentX + 1, currentY, currentBlock)) {
                    currentX++;
                }
                break;
            case Input.KEY_LEFT:
                if (canMove(currentX - 1, currentY, currentBlock)) {
                    currentX--;
                }
                break;
            case Input.KEY_SPACE:
            case Input.KEY_DOWN:
            case Input.KEY_LSHIFT:
                drop();
                break;
        }
    }

    public void drop() {
        while (canMove(currentX, currentY + 1, currentBlock)) {
            currentY++;
        }
    }

    public int[] randomBlock() {
        return block(Block.values()[random.nextInt(Block.values().length)]);
    }

    public int[] rotateLeft(int[] block) {
        int[] ret = new int[block.length];
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                ret[(currentBlockSize - j - 1) * currentBlockSize + i] = block[i * currentBlockSize + j];
            }
        }
        return ret;
    }

    public int[] rotateRight(int[] block) {
        int[] ret = new int[block.length];
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                ret[i * currentBlockSize + j] = block[(currentBlockSize - j - 1) * currentBlockSize + i];
            }
        }
        return ret;
    }

    public boolean canMove(int x, int y, int[] block) {
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                if (block[i + j * currentBlockSize] > 0
                        && ((x + i) < 0
                        || (x + i) >= cols
                        || (y + j) >= rows
                        || (y + j) < 0
                        || field[x + y * cols + i + j * cols] + block[i + j * currentBlockSize] > block[i + j * currentBlockSize])) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeLine(int y) {
        System.arraycopy(field, 0, field, cols, (y - 1) * cols);
        Arrays.fill(field, 0, cols - 1, 0);
    }

    public void setBlock(int x, int y, int[] block) {
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                field[x + y * cols + i + j * cols] = block[i + j * currentBlockSize];
            }
        }
    }

    public void gameOver() {
        resetGame();
    }

    public void resetGame() {
        Arrays.fill(field, 0);
        newBlock();
    }

    public void newBlock() {
        setCurrentBlock(randomBlock());
        currentX = cols / 2 - currentBlockSize / 2;
        currentY = -currentBlockSize;
        for (int i = 0; i <= currentBlockSize && !canMove(currentX, currentY, currentBlock); i++) {
            currentY++;
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setColor(Color.yellow);
        g.drawRect(49f, 49f, 30 * cols + 1, 30 * rows + 1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (field[j + i * cols] > 0) {
                    g.setColor(idToColor(field[j + i * cols]));
                    g.fillRect(50f + j * 30, 50f + i * 30, 29, 29);
                }
            }
        }
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                if (currentBlock[i + j * currentBlockSize] > 0) {
                    g.setColor(idToColor(currentBlock[i + j * currentBlockSize]));
                    //g.drawRect(49f + (currentX + i) * 30, 49f + (currentY + j) * 30, 31, 31);
                    g.fillRect(50f + (currentX + i) * 30, 50f + (currentY + j) * 30, 29, 29);
                }
            }
        }
    }

    public Color idToColor(int id) {
        switch (id) {
            case 0:
                return Color.black;
            case 1:
                return Color.green;
            case 2:
                return Color.cyan;
            case 3:
                return Color.magenta;
            case 4:
                return Color.white;
            case 5:
                return Color.red;
            case 6:
                return Color.gray;
            case 7:
                return Color.orange;
        }
        return Color.transparent;
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        triforce += delta;
        if (triforce > 200) {
            if (canMove(currentX, currentY + 1, currentBlock)) {
                currentY++;
            } else {
                for (int i = 0; i < currentBlockSize; i++) {
                    for (int j = 0; j < currentBlockSize; j++) {
                        if (currentBlock[i + j * currentBlockSize] > 0) {
                            if (field[currentX + i + (currentY + j) * cols] > 0) {
                                gameOver();
                                return;
                            }
                            field[currentX + i + (currentY + j) * cols] = currentBlock[i + j * currentBlockSize];
                        }
                    }
                }

                boolean rowCheck;
                for (int i = 0; i < rows; i++) {
                    rowCheck = true;
                    for (int j = 0; j < cols; j++) {
                        if (field[j + cols * i] <= 0 || !rowCheck) {
                            rowCheck = false;
                        }
                    }
                    if (rowCheck) {
                        removeLine(i + 1);
                    }
                }

                newBlock();
            }
            triforce -= 200;
        }
    }
}