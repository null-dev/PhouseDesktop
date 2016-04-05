package xyz.nulldev.phouse2dd.io;

import xyz.nulldev.phouse2dd.SHARED.FastKeyPacket;
import xyz.nulldev.phouse2dd.SHARED.FastScrollPacket;
import xyz.nulldev.phouse2dd.SHARED.FastTargetPacket;
import xyz.nulldev.phouse2dd.util.IntegrationUtils;
import xyz.nulldev.phouse2dd.SHARED.FastMousePacket;
import xyz.nulldev.phouse2dd.util.Utils;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: Phouse2
 * Created: 18/11/15
 * Author: nulldev
 */
public class WIFIServerIOMonitor implements Runnable {
    WIFIServer server;
    Action action;
    LinkedBlockingQueue<Byte[]> toWrite;
    Socket socket;
    BufferedInputStream bufferedInput;
    BufferedOutputStream bufferedOutput;
    final AtomicBoolean die = new AtomicBoolean(false);

    public WIFIServerIOMonitor(WIFIServer server, Action action, Socket socket) {
        this.server = server;
        this.action = action;
        this.socket = socket;
        if(action.equals(Action.WRITE)) {
            toWrite = new LinkedBlockingQueue<>();
        }
        try {
            bufferedInput = new BufferedInputStream(socket.getInputStream());
            bufferedOutput = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("[WSIOM] Exception starting monitor!");
            e.printStackTrace();
        }
    }

    public void destroy() {
        die.set(true);
        server = null;
    }

    @Override
    public void run() {
        while(!die.get()) {
            try {
                if(action.equals(Action.READ)) {
                    byte[] buffer = new byte[10];
                    bufferedInput.read(buffer);
                    handleRead(buffer);
                } else if (action.equals(Action.WRITE)) {
                    if(!toWrite.isEmpty())
                        bufferedOutput.write(Utils.unbox(toWrite.poll()));
                }
                //Sleep a bit to prevent wasting the CPU
//                try {Thread.sleep(100);} catch(InterruptedException ignored) {return;}
            } catch(Throwable t) {
                System.out.println("[WSIOM] Unhandled exception, ignoring!");
                t.printStackTrace();
            }
        }
    }

    public void handleRead(byte[] bytes) {
        if(bytes == null || bytes[0] == 0) {
            server.disconnect(socket.getInetAddress().toString());
            return;
        }
        if (bytes[0] == 5) {
            FastMousePacket fastMousePacket = FastMousePacket.fromPacket(bytes);
            IntegrationUtils.proportionalMouseMove(fastMousePacket.getX(), fastMousePacket.getY());
        }
        if(bytes[0] == 6) {
            IntegrationUtils.mouseDown();
        }
        if(bytes[0] == 7) {
            IntegrationUtils.mouseUp();
        }
        if(bytes[0] == 8) {
            IntegrationUtils.rightMouseDown();
        }
        if(bytes[0] == 9) {
            IntegrationUtils.rightMouseUp();
        }
        if(bytes[0] == 10) {
            IntegrationUtils.keyPress(FastKeyPacket.fromPacket(bytes).getKey());
        }
        if(bytes[0] == 11) {
            IntegrationUtils.keyEvent(KeyEvent.VK_BACK_SPACE);
        }
        if(bytes[0] == 12) {
            IntegrationUtils.keyEvent(KeyEvent.VK_ENTER);
        }
        if(bytes[0] == 20) {
            System.out.println("DATA: " + Arrays.toString(bytes));
            FastScrollPacket fastScrollPacket = FastScrollPacket.fromPacket(bytes);
            System.out.println("FLOAT: " + fastScrollPacket.getAmount());
            IntegrationUtils.mouseScroll(Math.round(fastScrollPacket.getAmount()), fastScrollPacket.getDirection());
        }
        if(bytes[0] == 51) {
            FastTargetPacket fastTargetPacket = FastTargetPacket.fromPacket(bytes);
            IntegrationUtils.spawnTarget(fastTargetPacket.getX(), fastTargetPacket.getY());
        }
        if(bytes[0] == 52) {
            IntegrationUtils.teardownTarget();
        }
//        System.out.println("READ: " + Arrays.toString(bytes));
    }

    //TODO MAYBE LATER
    public void doWrite(String data) {}

    public Action getAction() {
        return action;
    }

    public enum Action {
        READ, WRITE
    }
}
