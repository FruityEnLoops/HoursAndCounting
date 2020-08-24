package Main;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

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
    public String getAllEventsOf(LocalDate date) {
        StringBuilder res = new StringBuilder();
        for (CalendarEvent e : this.events) {
            if (e.start.toLocalDate().isEqual(date)) {
                res.append(e);
            }
        }
        if(res.toString().isEmpty()){
            return "Aucun évenement.";
        }
        return res.toString();
    }

    public String debugPrintCalendarStats(){
        return "Calendar identifier : " + identifier +
                "\nCalendar url : " + url +
                "\nCalendar event count : " + this.events.size();
    }
}
