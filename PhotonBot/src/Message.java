import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.ArrayList;

/**
 * Created by soroushomranpour on 8/6/2017 AD.
 */
public class Message {
    private long sender;
    private SendMessage message;

    public Message(long sender) {
        this.sender = sender;
    }

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public SendMessage getMessage() {
        return message;
    }

    public void setMessage(SendMessage message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        Message message = (Message) obj;
        return this.sender == message.sender;
    }
}
