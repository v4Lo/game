package game;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 *
 * @author V
 */
public class GameField {

    private Random random = new Random();
    public int rows = 16;
    public int cols = 9;
    public int[] field = new int[rows * cols];
    public int currentX;
    public int currentY;
    public int[] currentBlock;
    private int currentBlockSize;
    private boolean started = false;
    private LinkedList<Block> blockQueue;
    private int speed = 200;
    private int triforce;

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

    public void setCurrentBlock(int[] newBlock) {
        currentBlock = newBlock;
        currentBlockSize = getBlockSize(currentBlock);
    }

    private int getBlockSize(int[] b) {
        return (int) Math.sqrt(b.length);
    }

    private int[] block(Block type) {
        switch (type) {
            case J:
                return new int[]{0, 1, 0, 0, 1, 0, 1, 1, 0};
            case L:
                return new int[]{2, 0, 0, 2, 0, 0, 2, 2, 0};
            case I:
                return new int[]{0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0};
            case O:
                return new int[]{4, 4, 4, 4};
            case Z:
                return new int[]{0, 5, 0, 5, 5, 0, 5, 0, 0};
            case S:
                return new int[]{6, 0, 0, 6, 6, 0, 0, 6, 0};
            case T:
                return new int[]{0, 7, 0, 7, 7, 7, 0, 0, 0};
        }
        return new int[16];
    }

    public void drop() {
        if (!started) {
            return;
        }
        while (canMove(currentX, currentY + 1, currentBlock)) {
            currentY++;
        }
        triforce = speed;
    }

    private Block randomBlock() {
        return Block.values()[random.nextInt(Block.values().length)];
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
        //stop();
    }

    public final void resetGame() {
        blockQueue = new LinkedList<>();
        Arrays.fill(field, 0);
        newBlock();
    }

    private void newBlock() {
        while (blockQueue.size() < 5) {
            blockQueue.offer(randomBlock());
        }
        setCurrentBlock(block(blockQueue.poll()));

        currentX = cols / 2 - currentBlockSize / 2;
        currentY = -currentBlockSize;

        for (int i = 0; i <= currentBlockSize && !canMove(currentX, currentY, currentBlock); i++) {
            currentY++;
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

    public void render(Graphics g) {
        drawField(g);
        drawNextBlocks(g);
    }

    protected void drawField(Graphics g) {
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
        drawBlockAt(currentX, currentY, currentBlock, g);
    }

        protected void drawNextBlocks(Graphics g) {
        int offset = 0;
        for (Iterator<Block> it = blockQueue.iterator(); it.hasNext();) {
            int[] temp = block(it.next());
            drawBlockAt(cols + 1, offset, temp, g);
            offset += getBlockSize(temp) + 1;
        }
    }
    
    public void drawBlockAt(int x, int y, int[] block, Graphics g) {
        int blockSize = getBlockSize(block);
        for (int i = 0; i < blockSize; i++) {
            for (int j = 0; j < blockSize; j++) {
                if (block[i + j * blockSize] > 0) {
                    g.setColor(idToColor(block[i + j * blockSize]));
                    g.fillRect(1f + (x + i) * 30, 1f + (y + j) * 30, 29, 29);
                }
            }
        }
    }

    public void update(int delta) {
        triforce += delta;
        if (speed > triforce || !started) {
            return;
        }

        triforce -= speed;
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
    }
}