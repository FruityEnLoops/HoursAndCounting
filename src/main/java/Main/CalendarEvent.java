package Main;

import java.time.LocalDateTime;

public class CalendarEvent {
    String category;
    String uid;
    LocalDateTime start;
    LocalDateTime end;
    String summary;
    String location;
    String description;

    public String toString(){
        return category +
                "\nDe " + start.toString() +
                " a " + end.toString() +
                "\n" + summary +
                "\nDans : " + location;
    }
}
