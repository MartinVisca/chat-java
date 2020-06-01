package edu.isistan.client;

import edu.isistan.chat.ChatGUI;
import edu.isistan.chat.gui.MainWindows;
import edu.isistan.common.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private static String PUERTO = "localhost";
    private Socket s;
    private DataOutputStream dos;
    protected ChatGUI gui;
    private String name;

    public Client(String name) {
        this.name = name;
        try {
            s = new Socket(PUERTO, 6663);
            dos = new DataOutputStream(s.getOutputStream());
            gui = MainWindows.launchOrGet(new Callback(dos));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client(args[0]);
        c.ejecutar();
    }

    private void setName(String arg) {
        name = arg;
    }

    private void ejecutar(){
        try{
            new Thread(()-> {
                try {
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    while (true) {
                        byte type = dis.readByte();
                        switch (type) {
                            case (Protocol.ADD_USER):
                                String user = dis.readUTF();
                                gui.addUser(user);
                                break;

                            case (Protocol.REMOVE_USER):
                                user = dis.readUTF();
                                gui.removeUser(user);
                                break;

                            case (Protocol.GENERAL_MSG):
                                user = dis.readUTF();
                                String text = dis.readUTF();
                                gui.addNewGeneralMsg(user, text);
                                break;

                            case (Protocol.PRIVATE_MSG):
                                String userName = dis.readUTF();
                                String userTo = dis.readUTF();
                                String textTo = dis.readUTF();
                                gui.addNewMsg(userName, textTo);
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            this.dos.writeByte(Protocol.HANDSHAKE);
            this.dos.writeUTF(name);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
