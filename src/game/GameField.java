package game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;

/**
 *
 * @author V
 */
public class GameField {

    private Random random = new Random();
    private long triforce = 0;
    private int rows = 16;
    private int cols = 9;
    private int[] field = new int[rows * cols];
    private int currentX;
    private int currentY;
    private int[] currentBlock;
    private int currentBlockSize;
    private boolean started = false;

    private enum Block {

        J,
        L,
        I,
        O,
        Z,
        S,
        T
    }

    public GameField() {
        resetGame();
    }

    public final void start() {
        started = true;
    }

    public final void stop() {
        started = false;
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

    private int[] block(Block type) {
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

    public void drop() {
        while (canMove(currentX, currentY + 1, currentBlock)) {
            currentY++;
        }
    }

    private int[] randomBlock() {
        return block(Block.values()[random.nextInt(Block.values().length)]);
    }

    public boolean rotateLeft() {
        if (!started) {
            return false;
        }
        int[] ret = new int[currentBlock.length];
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                ret[(currentBlockSize - j - 1) * currentBlockSize + i] = currentBlock[i * currentBlockSize + j];
            }
        }
        if (canMove(currentX, currentY, ret)) {
            currentBlock = ret;
            return true;
        }
        return false;
    }

    public boolean rotateRight() {
        if (!started) {
            return false;
        }
        int[] ret = new int[currentBlock.length];
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                ret[i * currentBlockSize + j] = currentBlock[(currentBlockSize - j - 1) * currentBlockSize + i];
            }
        }
        if (canMove(currentX, currentY, ret)) {
            currentBlock = ret;
            return true;
        }
        return false;
    }

    public boolean right() {
        if (!started) {
            return false;
        }
        if (canMove(currentX + 1, currentY, currentBlock)) {
            currentX++;
            return true;
        }
        return false;
    }

    public boolean left() {
        if (!started) {
            return false;
        }
        if (canMove(currentX - 1, currentY, currentBlock)) {
            currentX--;
            return true;
        }
        return false;
    }

    private boolean canMove(int x, int y, int[] block) {
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

    public final void resetGame() {
        Arrays.fill(field, 0);
        newBlock();
    }

    private void newBlock() {
        setCurrentBlock(randomBlock());

        currentX = cols / 2 - currentBlockSize / 2;
        currentY = -currentBlockSize;

        for (int i = 0; i <= currentBlockSize && !canMove(currentX, currentY, currentBlock); i++) {
            currentY++;
        }
        System.out.println(currentX);
        System.out.println(currentY);
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

    public void render(Graphics g) {
        g.setColor(Color.yellow);
        g.drawRect(0f, 0f, 30 * cols + 1, 30 * rows + 1);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (field[j + i * cols] > 0) {
                    g.setColor(idToColor(field[j + i * cols]));
                    g.fillRect(1f + j * 30, 1f + i * 30, 29, 29);
                }
            }
        }
        for (int i = 0; i < currentBlockSize; i++) {
            for (int j = 0; j < currentBlockSize; j++) {
                if (currentBlock[i + j * currentBlockSize] > 0) {
                    g.setColor(idToColor(currentBlock[i + j * currentBlockSize]));
                    g.fillRect(1f + (currentX + i) * 30, 1f + (currentY + j) * 30, 29, 29);
                }
            }
        }
    }

    public void update(int delta) {
        triforce += delta;
        if (triforce > 200 && started) {
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