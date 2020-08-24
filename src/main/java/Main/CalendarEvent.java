package Main;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CalendarEvent implements Serializable {
    private final String UNDEFINED_DEFAULT = "undefined";
    private final LocalDateTime UNDEFINED_DEFAULT_DATE = LocalDateTime.of(LocalDate.of(0,1,1), LocalTime.of(0,0));

    String category;
    String uid;
    LocalDateTime start;
    LocalDateTime end;
    String summary;
    String location;
    String description;

    public CalendarEvent() {
        this.category = UNDEFINED_DEFAULT;
        this.uid = UNDEFINED_DEFAULT;
        this.start = UNDEFINED_DEFAULT_DATE;
        this.end = UNDEFINED_DEFAULT_DATE;
        this.summary = UNDEFINED_DEFAULT;
        this.location = UNDEFINED_DEFAULT;
        this.description = UNDEFINED_DEFAULT;
    }

    public String toString(){
        return category +
                "\nDe " + start +
                " a " + end +
                "\n" + summary +
                "\nDans : " + location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
