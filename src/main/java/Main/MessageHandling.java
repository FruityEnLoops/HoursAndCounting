package Main;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageHandling {
    private final static String argSeparator = " ";

    // A prefix debugging tool. See what the methods return, and see why this or that parameter isn't what you wanted.
    public static String messageProcessing(MessageReceivedEvent event){
        return getInputCommand(event) + "\nArgument count : " + getArgumentCount(event.getMessage().getContentRaw()) + "\nArgument list : " + getArgListAsString(getArgumentList(event.getMessage().getContentRaw()));
    }

    // Arg size. Useful for commands with fixed argument count, to counter badly formed commands
    public static int getArgumentCount(String message){
        return message.split(argSeparator).length - 1;
    }

    // Gets each argument (an argument is separated by argSeparator, here a space)
    public static String[] getArgumentList(String message){
        return message.split(argSeparator);
    }

    // Just some formatting, for better readability.
    public static String getArgListAsString(String[] argList){
        StringBuilder ret = new StringBuilder();
        if(argList.length > 1){
            ret = new StringBuilder(argList[1]);
            for(int i = 2; i < argList.length; i++){
                ret.append(", ").append(argList[i]);
            }
        }
        return ret.toString();
    }

    // Formatting entire input to be displayed without prefix
    // kinda useless
    public static String getInputCommand(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("!edt") || event.getMessage().getContentRaw().equals("!edt ")){
            return "No arguments were passed.";
        } else {
            return event.getMessage().getContentRaw().substring(Main.prefix.length() + 1);
        }
    }

    public static void addAuthorized(MessageReceivedEvent e){
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.add(Long.parseLong(args[2]));
            System.out.println("[DEBUG] Added Discord user " + Long.parseLong(args[2]) + " to authorized list.");
        }
    }

    public static void removeAuthorized(MessageReceivedEvent e){
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.remove(Long.parseLong(args[2]));
            System.out.println("[DEBUG] Removed Discord user " + Long.parseLong(args[2]) + " to authorized list.");
        }
    }

    public static void sendAdminList(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Current authorized Discord UIDs are: " + Main.authorized.toString()).queue();
    }
}
