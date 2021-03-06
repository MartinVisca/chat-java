package edu.isistan.server;

import edu.isistan.common.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    private Socket s;
    private Server server;
    private DataOutputStream dos;
    private String userName;

    public Client(Socket s, Server server) {
        this.s = s;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(this.s.getInputStream());
            dos = new DataOutputStream(this.s.getOutputStream());
            byte type = dis.readByte();
            if (type == Protocol.HANDSHAKE) {
                userName = dis.readUTF();
                if(!this.server.addClient(userName, this)) {
                    userName = null;
                    s.close();
                    return;
                }
            }
            while (true) {
                type = dis.readByte();
                switch (type) {
                    case (Protocol.GENERAL_MSG):
                        String text = dis.readUTF();
                        this.server.sendGeneralMsg(userName, text);
                        break;

                    case (Protocol.ADD_USER):
                        this.server.addClient(userName, this);
                        break;

                    case (Protocol.REMOVE_USER):
                        this.server.removeUser(userName);
                        break;

                    case (Protocol.PRIVATE_MSG):
                        String userTo = dis.readUTF();
                        text = dis.readUTF();
                        this.server.sendPrivateMsg(userName, userTo, text);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(userName != null) {
                this.server.removeUser(userName);
            }
        }
    }

    public void removeUser(String userName) {
        try {
            this.dos.writeByte(Protocol.REMOVE_USER);
            this.dos.writeUTF(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String userName) {
        try {
            this.dos.writeByte(Protocol.ADD_USER);
            this.dos.writeUTF(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGeneralMsg(String userName, String text) {
        try {
            dos.writeByte(Protocol.GENERAL_MSG);
            dos.writeUTF(userName);
            dos.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMsg(String userName, String userTo, String text) {
        try {
            dos.writeByte(Protocol.PRIVATE_MSG);
            dos.writeUTF(userName);
            dos.writeUTF(userTo);
            dos.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
