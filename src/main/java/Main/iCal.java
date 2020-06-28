package Main;

import java.io.IOException;
import java.util.ArrayList;

public class iCal {
    public String url;
    public String identifier;

    public String rawData;
    public ArrayList<CalendarEvent> events;

    // Construct the iCal object, and gets the rawData from url.
    public iCal(String url, String identifier) throws IOException {
        this.url = url;
        this.identifier = identifier;
        this.rawData = Parser.Parser.rawDataParser(url);
    }

    // builds the events ArrayList, containing all the event of the iCal.
    public void buildEventData(){
        this.events = Parser.Parser.parseRawData(this.rawData);
    }
}
