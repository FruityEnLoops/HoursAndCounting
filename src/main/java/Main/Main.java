package Main;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Main extends ListenerAdapter {
    public static final String prefix = "!edt";
    public static ArrayList<iCal> iCals = new ArrayList<>();
    public static ArrayList<Long> authorized = new ArrayList<>();
    public static JDA jda;

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
        // TODO
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // Just to make sure the bot doesn't get stuck reading it's own message, just don't parse the bot's messages. And other bots.
        if(event.getAuthor().isBot()){
            return;
        }

        // admin commands : reads from an ArrayList of authorized people. Authorized people can modify the ArrayList.
        if(authorized.contains(event.getAuthor().getIdLong())){
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
            }
            if(event.getMessage().getContentRaw().contains("!admin remove")){
                MessageHandling.removeAuthorized(event);
            }
            if(event.getMessage().getContentRaw().contains("!admin list")){
                MessageHandling.sendAdminList(event);
            }
        }

        if(event.getMessage().getContentRaw().contains(Main.prefix)){
            event.getChannel().sendMessage(MessageHandling.messageProcessing(event)).queue();
        }
    }
}
