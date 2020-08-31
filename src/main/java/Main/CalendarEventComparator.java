package Main;

import java.util.Comparator;

public class CalendarEventComparator implements Comparator<CalendarEvent> {
    @Override
    public int compare(CalendarEvent o1, CalendarEvent o2) {
        return o1.start.compareTo(o2.start);
    }
}
