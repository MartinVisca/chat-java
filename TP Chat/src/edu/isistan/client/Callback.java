package edu.isistan.client;

import edu.isistan.chat.IChat;
import edu.isistan.common.Protocol;

import java.io.DataOutputStream;
import java.io.IOException;

public class Callback implements IChat {
    private DataOutputStream dos;

    public Callback(DataOutputStream dos) {
        this.dos = dos;
    }

    @Override
    public void sendMsg(String text) {
        try {
            dos.writeByte(Protocol.GENERAL_MSG);
            dos.writeUTF(text);
        } catch (IOException e) {

        }
    }

    @Override
    public void sendMsg(String to, String text) {
        try {
            dos.writeByte(Protocol.PRIVATE_MSG);
            dos.writeUTF(to);
            dos.writeUTF(text);
        } catch (IOException e) {

        }
    }
}
