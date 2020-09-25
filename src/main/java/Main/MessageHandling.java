package Main;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MessageHandling {
    private final static String argSeparator = " ";
    public final static boolean USE_INLINE = false;

    public static MessageEmbed commandHandler(MessageReceivedEvent event){
        EmbedBuilder eb = new EmbedBuilder();
        String[] argsList = getArgumentList(event.getMessage().getContentRaw());
        // Try to find the user's iCal group
        List<Role> userRoles = event.getGuild().getMember(event.getAuthor()).getRoles();
        iCal userCalendar = null;
        boolean groupFoundInMessage = false;
        for(Role e : userRoles){
            for(iCal c : Main.iCals){
                if(e.getName().toLowerCase().equals(c.identifier.toLowerCase())){
                    userCalendar = c;
                }
            }
        }
        // if a valid iCal was specified in the message, override the user's group found in their roles (if one was found)
        if(argsList.length >= 2){
            for(iCal c : Main.iCals){
                if(c.identifier.toLowerCase().equals(argsList[1].toLowerCase())){
                    userCalendar = c;
                    groupFoundInMessage = true;
                }
            }
        }
        if(userCalendar == null){
            eb.addField("Erreur", "Calendrier non spécifié / vous n'avez le rôle d'aucun calendrier.", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        }
        // message is "!edt". Return all the events for the current day :
        if(argsList.length == 1){
            return userCalendar.getAllEventsOf(LocalDate.now());
        }
        if(argsList.length == 2){
            return pickDate(argsList[1], userCalendar);
        } if(argsList.length == 3 && groupFoundInMessage){
            return pickDate(argsList[2], userCalendar);
        }
        eb.addField("Erreur", "Syntaxe de la commande incorrecte", USE_INLINE);
        eb.setColor(Color.RED);
        eb.setTitle("Erreur");
        return eb.build();
    }

    private static MessageEmbed pickDate(String s, iCal userCalendar) {
        switch(s.toLowerCase()){
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
            default:
                return userCalendar.getAllEventsOf(LocalDate.now());
        }
    }

    // Gets each argument (an argument is separated by argSeparator, here a space)
    public static String[] getArgumentList(String message){
        return message.split(argSeparator);
    }

    public static MessageEmbed addAuthorized(MessageReceivedEvent e){
        EmbedBuilder eb = new EmbedBuilder();
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.add(Long.parseLong(args[2]));
            System.out.println("[DEBUG] Added Discord user " + Long.parseLong(args[2]) + " to authorized list.");
            eb.addField("Done", "Added Discord user " + Long.parseLong(args[2]) + " to authorized list.", USE_INLINE);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        eb.addField("Error", "Arguments missing", USE_INLINE);
        eb.setColor(Color.RED);
        eb.setTitle("Erreur");
        return eb.build();
    }

    public static MessageEmbed removeAuthorized(MessageReceivedEvent e){
        EmbedBuilder eb = new EmbedBuilder();
        String[] args = getArgumentList(e.getMessage().getContentRaw());
        if(args.length == 3 && args[2] != null){
            Main.authorized.remove(Long.parseLong(args[2]));
            System.out.println("[DEBUG] Removed Discord user " + Long.parseLong(args[2]) + " to authorized list.");
            eb.addField("Done", "Removed Discord user " + Long.parseLong(args[2]) + " to authorized list.", USE_INLINE);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        eb.addField("Error", "Arguments missing", USE_INLINE);
        eb.setColor(Color.RED);
        return eb.build();
    }

    public static MessageEmbed sendAdminList() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Admin List");
        for(Long l : Main.authorized){
            eb.addField("", l.toString(), USE_INLINE);
        }
        return eb.build();
    }

    public static MessageEmbed addCalendar(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 3){
            eb.addField("Erreur", "Merci de donner l'URL et le nom du calendrier", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        } else {
            for(iCal i : Main.iCals){
                if(i.identifier.equals(args[2])){
                    eb.addField("Erreur", "Un calendrier avec ce nom existe déjà!", USE_INLINE);
                    eb.setColor(Color.RED);
                    eb.setTitle("Erreur");
                    return eb.build();
                }
            }
            try {
                iCal calendar = new iCal(args[1], args[2]);
                calendar.buildEventData();
                Main.iCals.add(calendar);
                eb.addField("Succès", "Calendrier " + calendar.identifier + " ajouté avec succès!", USE_INLINE);
                eb.setColor(Color.GREEN);
                eb.setTitle("Succès");
                return eb.build();
            } catch (IOException e) {
                eb.addField("Erreur", "URL de l'iCal invalide!", USE_INLINE);
                eb.setColor(Color.RED);
                eb.setTitle("Erreur");
                return eb.build();
            }
        }
    }

    public static MessageEmbed removeCalendar(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 2){
            eb.addField("Erreur", "Merci de spécifier quel calendrier supprimer.", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        } else {
            for(iCal c : Main.iCals){
                if(c.identifier.equals(args[1])){
                    Main.iCals.remove(c);
                    eb.addField("Succès", "Calendrier " + c.identifier + " supprimé.", USE_INLINE);
                    eb.setColor(Color.GREEN);
                    eb.setTitle("Succès");
                    return eb.build();
                }
            }
            eb.addField("Erreur", "Aucun calendrier n'a été trouvé avec l'identifiant " + args[1] + ".", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        }
    }

    public static MessageEmbed updateCalendar(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        String[] args = getArgumentList(event.getMessage().getContentRaw());
        if(args.length != 2){
            eb.addField("Erreur", "Merci de spécifier quel calendrier mettre à jour.", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        } else {
            for(iCal c : Main.iCals){
                if(c.identifier.equals(args[1])){
                    c.update();
                    eb.addField("Succès","Calendrier " + c.identifier + " mis a jour avec succès.", USE_INLINE);
                    eb.setColor(Color.GREEN);
                    return eb.build();
                }
            }
            eb.addField("Erreur","Aucun calendrier n'a été trouvé avec l'identifiant " + args[1] + ".", USE_INLINE);
            eb.setColor(Color.RED);
            eb.setTitle("Erreur");
            return eb.build();
        }
    }

    public static MessageEmbed listCalendar() {
        EmbedBuilder eb = new EmbedBuilder();
        if(Main.iCals.isEmpty()){
            eb.addField("Erreur", "Il n'y a aucun calendriers actuellement!", USE_INLINE);
            eb.setTitle("Erreur");
            eb.setColor(Color.RED);
            return eb.build();
        }
        eb.setTitle("Calendriers");
        for(iCal i : Main.iCals){
            eb.addField("Calendrier " + i.identifier, "Nombre d'évènements : " + i.events.size(), USE_INLINE);
        }
        eb.setColor(Color.GREEN);
        eb.setTitle("Liste des calendriers");
        return eb.build();
    }

    public static MessageEmbed rightNow() {
        EmbedBuilder eb = new EmbedBuilder();
        for(iCal i : Main.iCals){
            CalendarEvent currentEvent = i.getCurrentEvent();
            if(currentEvent != null){
                eb.addField(i.identifier, currentEvent.summary + "\n"
                        + currentEvent.location + "\n" +
                        currentEvent.start.toLocalTime().toString() + " à " + currentEvent.end.toLocalTime().toString(), USE_INLINE);
            } else {
                eb.addField(i.identifier, "Rien", USE_INLINE);
            }
        }
        eb.setTitle("Cours actuels - " + LocalDate.now() + " " + LocalTime.now().getHour() + "h" + LocalTime.now().getMinute());
        eb.setColor(Color.GREEN);
        return eb.build();
    }
}
