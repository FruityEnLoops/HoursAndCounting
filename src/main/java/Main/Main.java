package Main;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static java.time.temporal.ChronoUnit.MINUTES;

public class Main extends ListenerAdapter {
    public static final String prefix = "!edt";
    public static ArrayList<iCal> iCals = new ArrayList<>();
    public static ArrayList<Long> authorized = new ArrayList<>();
    public static JDA jda;
    public static Timer saveThread = new Timer(true);
    public static final long saveDelay = 300000L; // 5 minutes
    public static Timer updateThread = new Timer(true);
    public static final long updateFrequency = 3600000L; // an hour

    public static void main(String[] args) throws LoginException {
        if(args.length != 1){
            System.out.println("[ERROR] Token not provided, or too much arguments were provided.");
            System.exit(1);
        }

        authorized.add(146323264409567232L);

        System.out.println("[DEBUG] Started.");
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(args[0]);
        builder.addEventListener(new Main());
        Main.jda = builder.buildAsync();
        System.out.println("[DEBUG] Connected.");
        Main.jda.getPresence().setPresence(Game.playing("lire des calendriers"), false);

        // Initialize saved iCals from serialized list
        iCals = loadSerializedItemList();
        // Initialize SaveThread
        saveThread.schedule(new SaveThread(), saveDelay, saveDelay);
        // Initialize UpdateThread
        updateThread.schedule(new UpdateThread(), updateFrequency, updateFrequency);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // Just to make sure the bot doesn't get stuck reading it's own message, just don't parse the bot's messages. And other bots.
        if(event.getAuthor().isBot()){
            return;
        }

        // admin commands : reads from an ArrayList of authorized people. Authorized people can modify the ArrayList.
        if(authorized.contains(event.getAuthor().getIdLong())
                && !event.getChannelType().isGuild()
                && event.getMessage().getContentRaw().startsWith("!admin")){
            if(event.getMessage().getContentRaw().equals("!admin shutdown")){
                System.out.println("[DEBUG] Shutting down. Requested by " +
                        event.getAuthor().getName() +
                        " on " +
                        LocalDate.now() +
                        " " + LocalTime.now());
                System.exit(0);
            }
            if(event.getMessage().getContentRaw().contains("!admin add")){
                MessageHandling.addAuthorized(event);
                return;
            }
            if(event.getMessage().getContentRaw().contains("!admin remove")){
                MessageHandling.removeAuthorized(event);
                return;
            }
            if(event.getMessage().getContentRaw().contains("!admin list")){
                MessageHandling.sendAdminList(event);
                return;
            }
        }

        if(event.getMessage().getContentRaw().startsWith("!edtadd") && checkUserPerm(event, "iCal Editor")){
            event.getChannel().sendMessage(MessageHandling.addCalendar(event)).queue();
        }

        if(event.getMessage().getContentRaw().startsWith("!edtrm")){
            event.getChannel().sendMessage(MessageHandling.removeCalendar(event)).queue();
        }

        if(event.getMessage().getContentRaw().startsWith(Main.prefix)){
            event.getChannel().sendMessage(MessageHandling.commandHandler(event)).queue();
        }
    }

    // Verifies if user has a role with the String name
    private boolean checkUserPerm(MessageReceivedEvent event, String name) {
        List<Role> authorRoles = event.getAuthor().getJDA().getRoles();
        for(Role r : authorRoles){
            if(r.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<iCal> loadSerializedItemList(){
        System.out.println("[DEBUG] Attempting to load item list object...");
        FileInputStream file;
        try{
            file = new FileInputStream("calendars.ser");
            ObjectInputStream stream = new ObjectInputStream(file);
            ArrayList<iCal> object = (ArrayList<iCal>) stream.readObject();
            stream.close();
            System.out.println("[DEBUG] Success.");
            return object;
        } catch (ClassNotFoundException | IOException e) {
            // itemList is not present, corrupted, or failed to load for some reason. Return an empty list, warn the administrator
            System.out.println("[WARNING] Item list object \"calendars.ser\" failed to load. Defaulting to an empty list.");
            return new ArrayList<>();
        }
    }

    public static void saveSerializedItemList(){
        System.out.println("[DEBUG] Attempting to save item list object...");
        FileOutputStream file;
        try{
            file = new FileOutputStream("calendars.ser");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(iCals);
            stream.flush();
            stream.close();
            System.out.println("[DEBUG] Success.");
        } catch (IOException e) {
            // file is locked by another process, or file is non existent even though it was previously opened
            System.out.println("[WARNING] Item list object \"calendars.ser\" failed to save. File might be used by something else.");
        }
    }

    // Attempts to update each calendar, downloading the latest version from the url.
    public static void update() {
        System.out.println("[DEBUG] (" + LocalDate.now() + " " + LocalTime.now() + ") Starting calendar updates.");
        LocalTime startTime = LocalTime.now();
        int failcount = 0;
        for(iCal c : iCals){
            boolean success = c.update();
            if(!success){
                failcount++;
                System.out.println("[WARNING] Calendar " + c.identifier + " failed to update");
            }
        }
        System.out.println("[DEBUG] (" + LocalDate.now() + " " + LocalTime.now() + ") Finished updating calendars.\n" +
                "Calendars parsed : " + Main.iCals.size() + "\n" +
                "Calendar updates failed : " + failcount + "\n" +
                "Update took : " + LocalTime.now().until(startTime, MINUTES));
    }
}
