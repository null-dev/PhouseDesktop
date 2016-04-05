package xyz.nulldev.phouse2dd.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: Phouse2
 * Created: 18/11/15
 * Author: nulldev
 */
public class WIFIServer {

    public static WIFIServer INSTANCE = null;

    HashMap<String, ClientHandler> handlers = new HashMap<>();

    ServerSocket activeSocket = null;
    String bindIP = null;

    AtomicBoolean die = new AtomicBoolean(false);

    public WIFIServer() {INSTANCE = this;}

    public boolean start(String ip) {
        System.out.println("[WIFIServer] Binding to ip: " + ip);
        try {
            activeSocket = new ServerSocket(29992, 50 , InetAddress.getByName(ip));
        } catch (IOException e) {
            System.out.println("[WIFIServer] Bind error!");
            e.printStackTrace();
            return false;
        }
        bindIP = ip;
        die.set(false);
        new Thread(this::serverLoop, "Phouse2 > Server Loop").start();
        return true;
    }

    public void serverLoop() {
        while(!die.get()) {
            try {
                Socket socket = activeSocket.accept();
                System.out.println("[WIFIServer] Client connected! (" + socket.getInetAddress() + ")");
                WIFIServerIOMonitor writeMonitor = new WIFIServerIOMonitor(this, WIFIServerIOMonitor.Action.WRITE, socket);
                WIFIServerIOMonitor readMonitor = new WIFIServerIOMonitor(this, WIFIServerIOMonitor.Action.READ, socket);
                Thread writeMonitorThread = new Thread(writeMonitor, "Phouse2 > Write Thread");
                Thread readMonitorThread = new Thread(readMonitor, "Phouse2 > Read Thread");
                writeMonitorThread.start();
                readMonitorThread.start();
                ClientHandler handler = new ClientHandler(writeMonitor,
                        readMonitor,
                        writeMonitorThread,
                        readMonitorThread);
                handlers.put(socket.getInetAddress().toString(), handler);

            } catch (IOException e) {
                System.out.println("[WIFIServer] Unknown error!");
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        System.out.println("[WIFIServer] WIFIServer shutting down...");
        if(activeSocket != null) {
            die.set(true);
            try {
                activeSocket.close();
                bindIP = null;
                activeSocket = null;
            } catch (IOException e) {
                System.out.println("[WIFIServer] Disconnect error!");
                e.printStackTrace();
            }
        }
    }

    public void disconnect(String socket) {
        ClientHandler handler = handlers.remove(socket);
        if(handler == null) return;
        handler.getReadMonitor().destroy();
        handler.getWriteMonitor().destroy();
        handler.getReadMonitorThread().interrupt();
        handler.getWriteMonitorThread().interrupt();
    }

    /*public void write(String string) {
        if(activeSocket != null) {
            writeMonitor.doWrite(string);
        }
    }*/

    public HashMap<String, ClientHandler> getHandlers() {
        return handlers;
    }

    public String getBindIP() {
        return bindIP;
    }

    public void setBindIP(String bindIP) {
        this.bindIP = bindIP;
    }

    public AtomicBoolean getDie() {
        return die;
    }

    public void setDie(AtomicBoolean die) {
        this.die = die;
    }

    class ClientHandler {
        WIFIServerIOMonitor writeMonitor;
        WIFIServerIOMonitor readMonitor;
        Thread writeMonitorThread;
        Thread readMonitorThread;

        public ClientHandler(WIFIServerIOMonitor writeMonitor, WIFIServerIOMonitor readMonitor, Thread writeMonitorThread, Thread readMonitorThread) {
            this.writeMonitor = writeMonitor;
            this.readMonitor = readMonitor;
            this.writeMonitorThread = writeMonitorThread;
            this.readMonitorThread = readMonitorThread;
        }

        public WIFIServerIOMonitor getWriteMonitor() {
            return writeMonitor;
        }

        public void setWriteMonitor(WIFIServerIOMonitor writeMonitor) {
            this.writeMonitor = writeMonitor;
        }

        public WIFIServerIOMonitor getReadMonitor() {
            return readMonitor;
        }

        public void setReadMonitor(WIFIServerIOMonitor readMonitor) {
            this.readMonitor = readMonitor;
        }

        public Thread getWriteMonitorThread() {
            return writeMonitorThread;
        }

        public void setWriteMonitorThread(Thread writeMonitorThread) {
            this.writeMonitorThread = writeMonitorThread;
        }

        public Thread getReadMonitorThread() {
            return readMonitorThread;
        }

        public void setReadMonitorThread(Thread readMonitorThread) {
            this.readMonitorThread = readMonitorThread;
        }
    }
}
