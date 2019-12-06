package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player implements Runnable {
    boolean inGame = true;
    private Object lock;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    DataPackage dataPackage;
    private PlayerState playerState = PlayerState.WaitForStart;

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public enum PlayerState {
        Receive, Send, WaitForStart, NotYourTurn, EndGame
    }


    public Player(Socket socket) {
        try {
            is = new ObjectInputStream(socket.getInputStream());
            os = new ObjectOutputStream(socket.getOutputStream());
            sendTurnInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendTurnInfo() {
        try {
            switch (playerState) {
                case WaitForStart:
                    send(new DataPackage("Wait for start", DataPackage.Info.Turn));
                    break;
                case NotYourTurn:
                    send(new DataPackage("Not your turn", DataPackage.Info.Turn));
                    break;
                default:
                    send(new DataPackage("Your turn", DataPackage.Info.Turn));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void send(DataPackage dataPackage) throws IOException {
        os.writeObject(dataPackage);
        os.flush();
        os.reset();
    }

    public void receive() throws IOException, ClassNotFoundException {
        dataPackage = (DataPackage) is.readObject();
        switch (playerState) {
            case WaitForStart:
                send(new DataPackage("Wait for start", DataPackage.Info.Turn));
                break;
            case NotYourTurn:
                send(new DataPackage("Not your turn", DataPackage.Info.Turn));
                break;
            default:
                send(new DataPackage("Your turn",DataPackage.Info.Turn));
                synchronized (lock){
                    lock.notify();
                }
        }
    }


    @Override
    public void run() {
        System.out.println("Lets Play!");
        while (inGame) {
            try {
                receive();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
