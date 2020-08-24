package Main;

import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MessageHandling {
    private final static String argSeparator = " ";

    public static String commandHandler(MessageReceivedEvent event){
        String[] argsList = getArgumentList(event.getMessage().getContentRaw());
        // Try to find the user's iCal group
        List<Role> userRoles = event.getAuthor().getJDA().getRoles();
        iCal userCalendar = null;
        boolean groupFoundInMessage = false;
        for(Role e : userRoles){
            for(iCal c : Main.iCals){
                if(e.getName().equals(c.identifier)){
                    userCalendar = c;
                }
            }
        }
        // if a valid iCal was specified in the message, override the user's group found in their roles (if one was found)
        if(argsList.length >= 2){
            for(iCal c : Main.iCals){
                if(c.identifier.equals(argsList[1])){
                    userCalendar = c;
                    groupFoundInMessage = true;
                }
            }
        }
        if(userCalendar == null){
            return "Erreur : calendrier non spécifié / vous n'avez le rôle d'aucun calendrier";
        }
        // message is "!edt". Return all the events for the current day :
        if(argsList.length == 1){
            return userCalendar.getAllEventsOf(LocalDate.now());
        }
        if(argsList.length == 2){
            switch(argsList[1]){
                case "demain":
                    return userCalendar.getAllEventsOf(LocalDate.now().plusDays(1));
                case "lundi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
                case "mardi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)));
                case "mercredi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)));
                case "jeudi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)));
                case "vendredi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
                case "samedi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
                case "dimanche":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
            }
        } if(argsList.length == 3 && groupFoundInMessage){
            switch(argsList[2]){
                case "demain":
                    return userCalendar.getAllEventsOf(LocalDate.now().plusDays(1));
                case "lundi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
                case "mardi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)));
                case "mercredi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY)));
                case "jeudi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY)));
                case "vendredi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)));
                case "samedi":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
                case "dimanche":
                    return userCalendar.getAllEventsOf(LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)));
            }
        }
        return userCalendar.getAllEventsOf(LocalDate.now());
    }

    // Gets each argument (an argument is separated by argSeparator, here a space)
    public static String[] getArgumentList(String message){
        return message.split(argSeparator);
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

    public static String addCalendar(MessageReceivedEvent event) {
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 3){
            return "Erreur : merci de donner l'URL et le nom du calendrier";
        } else {
            for(iCal i : Main.iCals){
                if(i.identifier.equals(args[2])){
                    return "Erreur : un calendrier avec ce nom existe déjà!";
                }
            }
            try {
                iCal calendar = new iCal(args[1], args[2]);
                calendar.buildEventData();
                Main.iCals.add(calendar);
                return "Calendrier " + calendar.identifier + " ajouté avec succès!";
            } catch (IOException e) {
                return "Erreur : URL de l'iCal invalide!";
            }
        }
    }

    public static String removeCalendar(MessageReceivedEvent event) {
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 2){
            return "Erreur : merci de spécifier quel calendrier supprimer";
        } else {
            for(iCal c : Main.iCals){
                if(c.identifier.equals(args[1])){
                    Main.iCals.remove(c);
                    return "Calendrier " + c.identifier + " supprimé.";
                }
            }
            return "Erreur : aucun calendrier n'a été trouvé avec l'identifiant " + args[1] + ".";
        }
    }

    public static String updateCalendar(MessageReceivedEvent event) {
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 2){
            return "Erreur : merci de spécifier quel calendrier supprimer.";
        } else {
            for(iCal c : Main.iCals){
                if(c.identifier.equals(args[1])){
                    c.update();
                    return "Calendrier " + c.identifier + " mis a jour avec succès.";
                }
            }
            return "Erreur : aucun calendrier n'a été trouvé avec l'identifiant " + args[1] + ".";
        }
    }
}
