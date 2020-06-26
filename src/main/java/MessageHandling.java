import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageHandling {
    private final static String argSeparator = " ";

    public static String messageProcessing(MessageReceivedEvent event){

        return "Argument count : " + getArgumentCount(event.getMessage().getContentRaw()) + "\nArgument list : " + getArgListAsString(getArgumentList(event.getMessage().getContentRaw()));
    }

    public static int getArgumentCount(String message){
        int count = 0;
        for(int i = 0; i < message.length(); i++){
            if(message.charAt(i) == ' '){
                count++;
            }
        }
        return count;
    }

    public static String[] getArgumentList(String message){
        return message.split(argSeparator);
    }

    public static String getArgListAsString(String[] argList){
        String ret = "";
        if(argList.length > 1){
            ret = argList[1];
            for(int i = 2; i < argList.length; i++){
                ret += ", " + argList[i];
            }
        }
        return ret;
    }

    public static String getInputCommand(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equals("!edt") || event.getMessage().getContentRaw().equals("!edt ")){
            return "No arguments were passed.";
        } else {
            return event.getMessage().getContentRaw().substring(Main.prefix.length() + 1);
        }
    }
}
