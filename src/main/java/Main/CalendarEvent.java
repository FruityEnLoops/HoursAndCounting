package Main;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CalendarEvent implements Serializable {
    private final String UNDEFINED_DEFAULT = "undefined";

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
        this.start = null;
        this.end = null;
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
