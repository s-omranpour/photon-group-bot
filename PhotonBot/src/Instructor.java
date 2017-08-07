/**
 * Created by soroushomranpour on 8/5/2017 AD.
 */
public class Instructor {
    private String name;
    private long chatId;

    public Instructor(String name, long chatId) {
        this.name = name;
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object obj) {
        Instructor temp = (Instructor) obj;
        return this.name.equals(temp.name) || this.chatId == temp.chatId;
    }

    public String getName() {
        return name;
    }

    public long getChatId() {
        return chatId;
    }
}
