package Main;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;

import static Main.MessageHandling.USE_INLINE;
import static java.time.temporal.ChronoUnit.MILLIS;

public class Main extends ListenerAdapter {
    public static final String prefix = "!edt";
    public static ArrayList<iCal> iCals = new ArrayList<>();
    public static ArrayList<Long> authorized = new ArrayList<>();
    public static JDA jda;
    public static Timer saveThread = new Timer(true);
    public static final long saveDelay = 300000L; // 5 minutes
    public static Timer updateThread = new Timer(true);
    public static final long updateFrequency = 3600000L ; // an hour

    public static void main(String[] args) throws LoginException {
        if(args.length != 1){
            System.out.println("[ERROR] Token not provided, or too much arguments were provided.");
            System.exit(1);
        }

        authorized.add(146323264409567232L);

        System.out.println("[INFO] " + getCurrentTime() + " Started.");
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(args[0]);
        builder.addEventListener(new Main());
        Main.jda = builder.buildAsync();
        System.out.println("[INFO] " + getCurrentTime() + " Connected.");
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
                System.out.println("[DEBUG] " + getCurrentTime() + " Shutting down. Requested by " +
                        event.getAuthor().getName() +
                        " on " +
                        LocalDate.now() +
                        " " + LocalTime.now());
                // save the current calendars before shutting down
                Main.saveSerializedItemList();
                System.exit(0);
            }
            if(event.getMessage().getContentRaw().startsWith("!admin add")){
                event.getChannel().sendMessage(MessageHandling.addAuthorized(event)).queue();
                return;
            }
            if(event.getMessage().getContentRaw().startsWith("!admin remove")){
                event.getChannel().sendMessage(MessageHandling.removeAuthorized(event)).queue();
                return;
            }
            if(event.getMessage().getContentRaw().equals("!admin list")){
                event.getChannel().sendMessage(MessageHandling.sendAdminList()).queue();
                return;
            }
        }

        if(event.getMessage().getContentRaw().equals("!help")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("HoursAndCounting", "https://github.com/FruityEnLoops/HoursAndCounting");
            eb.setDescription("Commandes");
            eb.addField("!edt","Affiche votre emploi du temps du jour.", USE_INLINE);
            eb.addField("!edt <jour/demain>", "Affiche votre emploi du temps, un jour précis ou du lendemain.", USE_INLINE);
            eb.addField("!edt <nom du calendier>", "Affiche l'emploi du temps indiqué du jour.", USE_INLINE);
            eb.addField("!edt <nom du calendrier> <jour/demain>", "Affiche un jour précis de l'emploi du temps indiqué.", USE_INLINE);
            eb.setFooter("HoursAndCounting", jda.getSelfUser().getAvatarUrl());
            eb.setColor(Color.GREEN);
            event.getChannel().sendMessage(eb.build()).queue();
        }

        if(event.getMessage().getContentRaw().startsWith("!edtadd")){
            if (checkUserPerm(event, "iCal Editor")) {
                event.getChannel().sendMessage(MessageHandling.addCalendar(event)).queue();
                return;
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Erreur", "Vous n'avez pas les permissions nécéssaires pour éditer les calendriers.", USE_INLINE);
                eb.setColor(Color.RED);
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }
        }

        if(event.getMessage().getContentRaw().startsWith("!edtrm")){
            if (checkUserPerm(event, "iCal Editor")) {
                event.getChannel().sendMessage(MessageHandling.removeCalendar(event)).queue();
                return;
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.addField("Erreur", "Vous n'avez pas les permissions nécéssaires pour éditer les calendriers.", USE_INLINE);
                eb.setColor(Color.RED);
                event.getChannel().sendMessage(eb.build()).queue();
                return;
            }
        }

        if(event.getMessage().getContentRaw().startsWith("!edtlist")){
            event.getChannel().sendMessage(MessageHandling.listCalendar()).queue();
            return;
        }

        if(event.getMessage().getContentRaw().startsWith("!edtup")){
            event.getChannel().sendMessage(MessageHandling.updateCalendar(event)).queue();
            return;
        }

        if(event.getMessage().getContentRaw().startsWith(Main.prefix)){
            event.getChannel().sendMessage(MessageHandling.commandHandler(event)).queue();
        }
    }

    // Verifies if user has a role with the String name
    private boolean checkUserPerm(MessageReceivedEvent event, String name) {
        for(Role r : event.getGuild().getMember(event.getAuthor()).getRoles()){
            if(r.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<iCal> loadSerializedItemList(){
        System.out.println("[DEBUG] " + getCurrentTime() + " Attempting to load item list object...");
        FileInputStream file;
        try{
            file = new FileInputStream("calendars.ser");
            ObjectInputStream stream = new ObjectInputStream(file);
            ArrayList<iCal> object = (ArrayList<iCal>) stream.readObject();
            stream.close();
            System.out.println("[INFO] " + getCurrentTime() + " Success.");
            return object;
        } catch (ClassNotFoundException | IOException e) {
            // itemList is not present, corrupted, or failed to load for some reason. Return an empty list, warn the administrator
            System.out.println("[WARNING] " + getCurrentTime() + " Item list object \"calendars.ser\" failed to load. Defaulting to an empty list.");
            return new ArrayList<>();
        }
    }

    public static void saveSerializedItemList(){
        System.out.println("[DEBUG] " + getCurrentTime() + " Attempting to save item list object...");
        FileOutputStream file;
        try{
            file = new FileOutputStream("calendars.ser");
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeObject(iCals);
            stream.flush();
            stream.close();
            System.out.println("[INFO] " + getCurrentTime() + " Success.");
        } catch (IOException e) {
            // file is locked by another process, or file is non existent even though it was previously opened
            System.out.println("[WARNING] " + getCurrentTime() + " Item list object \"calendars.ser\" failed to save. File might be used by something else.");
        }
    }

    // Attempts to update each calendar, downloading the latest version from the url.
    public static void update() {
        System.out.println("[DEBUG] " + getCurrentTime() + " Starting calendar updates.");
        LocalTime startTime = LocalTime.now();
        int failcount = 0;
        for(iCal c : iCals){
            boolean success = c.update();
            if(!success){
                failcount++;
                System.out.println("[WARNING] " + getCurrentTime() + "Calendar " + c.identifier + " failed to update");
            }
        }
        System.out.println("[INFO] " + getCurrentTime() + " Finished updating calendars.\n" +
                "[INFO] Calendars parsed : " + Main.iCals.size() + "\n" +
                "[INFO] Calendar updates failed : " + failcount + "\n" +
                "[INFO] Update took : " + startTime.until(LocalTime.now(), MILLIS) + "ms");
    }

    public static String getCurrentTime(){
        return "(" + LocalDate.now() + " " + LocalTime.now() + ")";
    }
}
