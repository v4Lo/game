package server;

import game.GameField;
import java.util.Arrays;

/**
 *
 * @author V
 */
public class GameFieldData {

    public String id;
    public int currentX;
    public int currentY;
    public int[] currentBlock;
    public int[] field;

    public GameFieldData(byte[] data) {
        int blockLength = Byte.valueOf(data[0]).intValue();
        int rows = Byte.valueOf(data[1]).intValue();
        int cols = Byte.valueOf(data[2]).intValue();
        currentX = Byte.valueOf(data[3]).intValue();
        currentY = Byte.valueOf(data[4]).intValue();
        field = new int[rows * cols];
        int i = 5;
        //System.out.println(i);
        //System.out.println("+" + field.length);
        for (; i < field.length + 5; i++) {
            field[i - 5] = Byte.valueOf(data[i]).intValue();
        }
        //System.out.println(i);
        //System.out.println("+" + blockLength);
        currentBlock = new int[blockLength];
        for (; i < blockLength + field.length + 5; i++) {
            currentBlock[i - field.length - 5] = Byte.valueOf(data[i]).intValue();
        }
        //System.out.println(i);
        byte[] idBytes = Arrays.copyOfRange(data, i, data.length);
        //System.out.println("+" + (data.length-i));
        id = new String(idBytes).trim();
        System.out.println("rows: " + rows + " cols: " + cols );
    }

    public static byte[] encode(GameField f, String id) {
        byte blockLength = Integer.valueOf(f.currentBlock.length).byteValue();
        byte rows = Integer.valueOf(f.rows).byteValue();
        byte cols = Integer.valueOf(f.cols).byteValue();
        byte currentX = Integer.valueOf(f.currentX).byteValue();
        byte currentY = Integer.valueOf(f.currentY).byteValue();
        byte[] field = new byte[f.field.length];


        for (int i = 0; i < f.field.length; i++) {
            field[i] = Integer.valueOf(f.field[i]).byteValue();
        }

        byte[] currentBlock = new byte[f.currentBlock.length];
        for (int i = 0; i < f.currentBlock.length; i++) {
            currentBlock[i] = Integer.valueOf(f.currentBlock[i]).byteValue();
        }
        byte[] ret = new byte[5 + f.currentBlock.length + f.field.length + id.getBytes().length];
        ret[0] = blockLength;
        ret[1] = rows;
        ret[2] = cols;
        ret[3] = currentX;
        ret[4] = currentY;

        System.arraycopy(field, 0, ret, 5, field.length);
        System.arraycopy(currentBlock, 0, ret, 5 + field.length, currentBlock.length);
        System.arraycopy(id.getBytes(), 0, ret, 5 + field.length + currentBlock.length, id.getBytes().length);
        return ret;
    }
}