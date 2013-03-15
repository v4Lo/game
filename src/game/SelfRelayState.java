package game;

import server.NetworkGameField;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import server.GameFieldData;

/**
 *
 * @author V
 */
public class SelfRelayState extends BasicGameState {

    private int stateID;
    private GameField theGame;
    private Thread t;
    private HashMap<String, NetworkGameField> netGames;
    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
    private DatagramSocket clientSocket;
    String uniqueID;
    Random random = new Random();
    InetAddress IPAddress;

    public SelfRelayState(int id) {
        uniqueID = String.valueOf(random.nextInt(50000));
        netGames = new HashMap<>();

        stateID = id;
    }

    @Override
    public int getID() {
        return stateID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        container.setVSync(true);
        container.setAlwaysRender(true);

        theGame = new GameField();
        //gfs = new GameFieldSynchronizer(theGame, "muhmuh");
        theGame.start();
        try {
            IPAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            Logger.getLogger(SelfRelayState.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(SelfRelayState.class.getName()).log(Level.SEVERE, null, ex);
        }

        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] sendData = GameFieldData.encode(theGame, uniqueID);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                    clientSocket.send(sendPacket);
                    //System.out.println("sent data..");
                } catch (IOException ex) {
                    Logger.getLogger(SelfRelayState.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] receiveData;
                //System.out.println("receive thread is started");
                while (true) {
                    receiveData = new byte[256];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    try {
                        //System.out.println("listening..");
                        clientSocket.receive(receivePacket);
                        //System.out.println("received a packet");
                    } catch (IOException ex) {
                        Logger.getLogger(SelfRelayState.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    GameFieldData data = new GameFieldData(receivePacket.getData());
                    NetworkGameField ngf;

                    if (!netGames.containsKey(data.id)) {
                        ngf = new NetworkGameField();
                        netGames.put(data.id, ngf);
                    } else {
                        ngf = netGames.get(data.id);
                    }
                    synchronized (ngf) {
                        ngf.lastUpdate = System.currentTimeMillis();
                        ngf.currentX = data.currentX;
                        ngf.currentY = data.currentY;
                        ngf.setCurrentBlock(data.currentBlock);
                        ngf.field = data.field;
                        System.out.println("Printing known IDs");
                        for (Map.Entry<String, NetworkGameField> entry : netGames.entrySet()) {
                            System.out.println(entry.getKey());
                        }
                    }
                    System.out.println("Known games:" + netGames.size());
                }
            }
        });

        t.start();

        ses.scheduleAtFixedRate(
                new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis() - 10000;
                for (Map.Entry<String, NetworkGameField> entry : netGames.entrySet()) {
                    if (entry.getValue().lastUpdate < time) {
                        if (entry.getValue().markedForTimeOut) {
                            netGames.remove(entry.getKey());
                        } else {
                            entry.getValue().markedForTimeOut = true;
                        }
                    } else {
                        entry.getValue().markedForTimeOut = false;
                    }
                }
            }
        }, 0, 10000, TimeUnit.MILLISECONDS);
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
        g.translate(20f, 50f);
        theGame.render(g);
        g.resetTransform();
        int i = 1;
        for (Map.Entry<String, NetworkGameField> entry : netGames.entrySet()) {
            g.translate(400f * i, 50f);
            i++;
            
            entry.getValue().render(g);
            g.resetTransform();
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        theGame.update(delta);
    }
}