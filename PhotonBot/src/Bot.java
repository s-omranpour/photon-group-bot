import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soroushomranpour on 8/5/2017 AD.
 */
public class Bot extends TelegramLongPollingBot {
    DataBaseClient dataBaseClient;
    ArrayList<Instructor> instructors;
    ArrayList<Message> messageToInstructor = new ArrayList<>();
    ArrayList<Message> messageToStudent = new ArrayList<>();

    public Bot() {
        dataBaseClient = new DataBaseClient();
        dataBaseClient.connect();
        instructors = dataBaseClient.loadInstructor();
    }

    @Override
    public String getBotUsername() {
        return "photongroupbot";
    }

    @Override
    public String getBotToken() {
        return "394042885:AAEV_iTAQFf2wX5S0dZOyaAiGy3uk7Y8hW8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            if (instructors.contains(new Instructor("",chatId))){
                SendMessage messageToUser = new SendMessage().setChatId(chatId);
                if (text.equals("/start")){
                    messageToUser.setText("please enter student's chatid:");
                }
                else if (text.matches("[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)")){
                    messageToUser.setText("now please enter your answer:");
                    Message message;
                    try {
                        message = new Message(chatId);
                        message.setMessage(new SendMessage().setChatId(Long.parseLong(text)));
                        messageToStudent.add(message);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if (messageToStudent.contains(new Message(chatId))){
                    Message message = messageToStudent.get(messageToStudent.indexOf(new Message(chatId)));
                    message.getMessage().setText("Answer from your instructor:\n"+text);
                    try {
                        sendMessage(message.getMessage());
                        messageToStudent.remove(message);
                        messageToUser.setText("your answer is sent to student!");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        messageToUser.setText("your answer was not sent due to an error,please use /start again!");
                    }
                }
                try {
                    sendMessage(messageToUser);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
            else {
                User user = update.getMessage().getFrom();
                SendMessage messageToUser = new SendMessage().setChatId(chatId);
                if (text.equals("/start")){
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    for (int i = 0; i < instructors.size(); i++) {
                        row.add(instructors.get(i).getName());
                    }
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);
                    messageToUser.setReplyMarkup(keyboardMarkup);
                    messageToUser.setText("Please choose the instructor you want to send message to:");
                }
                else if (text.length() <= 50 && instructors.contains(new Instructor(text,0))){
                    Instructor instructor = instructors.get(instructors.indexOf(new Instructor(text,0)));
                    Message message = new Message(chatId);
                    message.setMessage(new SendMessage().setChatId(instructor.getChatId()));
                    messageToInstructor.add(message);
                    messageToUser.setText("you chose "+instructor.getName()+"\nnow please enter your message:");
                }
                else if (messageToInstructor.get(messageToInstructor.indexOf(new Message(user.getId()))).getMessage().getChatId() != null){
                    Message message = messageToInstructor.get(messageToInstructor.indexOf(new Message(user.getId())));
                    message.getMessage().setText("message from "+user.getFirstName()+" "+user.getLastName()+" " +user.getUserName()+"\n"+
                            "sender chat id : "+user.getId()+"\n"+
                            text);
                    try {
                        sendMessage(message.getMessage());
                        messageToInstructor.remove(message);
                        messageToUser.setText("Your message is sent to your instructor!\nthank you for your message!");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        messageToUser.setText("Your message wasnt send to your instructor due to an error\nplease use /start again!");
                    }
                    dataBaseClient.insert(user.getFirstName(),user.getLastName(),user.getUserName(),user.getId(),text);
                }
                try {
                    sendMessage(messageToUser);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
