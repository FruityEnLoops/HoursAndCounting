package Main;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static Main.MessageHandling.USE_INLINE;

public class iCal implements Serializable {
    public String url;
    public String identifier;

    public String rawData;
    public ArrayList<CalendarEvent> events;

    // Construct the iCal object, and gets the rawData from url.
    public iCal(String url, String identifier) throws IOException {
        this.url = url;
        this.identifier = identifier;
        this.rawData = Parser.Parser.rawDataParser(url);
        this.events = new ArrayList<CalendarEvent>();
    }

    // builds the events ArrayList, containing all the event of the iCal.
    public void buildEventData(){
        this.events = Parser.Parser.parseRawData(this.rawData);
    }

    public boolean update(){
        try {
            this.rawData = Parser.Parser.rawDataParser(this.url);
            this.buildEventData();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Returns all events of the specified date
    public MessageEmbed getAllEventsOf(LocalDate date) {
        boolean found = false;
        EmbedBuilder eb = new EmbedBuilder();
        ArrayList<CalendarEvent> todaysEvents = new ArrayList<CalendarEvent>();
        for (CalendarEvent e : this.events) {
            if (e.start.toLocalDate().isEqual(date)) {
                todaysEvents.add(e);
                found = true;
            }
        }
        if(!found){
            eb.addField("Erreur", "Aucun évenement.", USE_INLINE);
            eb.setColor(Color.RED);
            return eb.build();
        }
        Collections.sort(todaysEvents, new CalendarEventComparator());
        for(CalendarEvent e : todaysEvents){
            eb.addField(e.summary, e.location + "\n" +
                    e.start.toLocalTime().toString() + " à " + e.end.toLocalTime().toString(), USE_INLINE);
        }
        eb.setColor(Color.green);
        eb.setTitle(date.toString() + " (" + this.identifier + ")");
        return eb.build();
    }

    public CalendarEvent getCurrentEvent() {
        for(CalendarEvent e : this.events){
            if(e.start.isBefore(LocalDateTime.now()) && e.end.isAfter(LocalDateTime.now())){
                return e;
            }
        }
        return null;
    }
}
