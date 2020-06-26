import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    public static final String prefix = "!edt";

    public static void main(String[] args) throws LoginException {
        if(args.length != 1){
            System.out.println("[ERROR] Token not provided, or too much arguments were provided.");
            System.exit(1);
        }
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        String appToken = args[0];
        builder.setToken(appToken);
        builder.addEventListener(new Main());
        builder.buildAsync();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // System.out.println("New message : " + event.getMessage().getContentDisplay() + " from " + event.getAuthor().getName());

        // Just to make sure the bot doesn't get stuck reading it's own message, just don't parse the bot's messages. And other bots.
        if(event.getAuthor().isBot()){
            return;
        }

        if(event.getMessage().getContentRaw().contains(Main.prefix)){
            System.out.println("Detected prefix.");
            event.getChannel().sendMessage("Detected prefix! Input command was : " + MessageHandling.getInputCommand(event)).queue();
            event.getChannel().sendMessage(MessageHandling.messageProcessing(event)).queue();
        }
    }
}
