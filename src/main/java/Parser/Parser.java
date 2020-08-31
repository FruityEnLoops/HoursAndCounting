package Parser;

import Main.CalendarEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    private static final String SUMMARY = "SUMMARY";
    private static final String UID = "UID";
    private static final String DTSTART = "DTSTART";
    private static final String DTEND = "DTEND";
    private static final String LOCATION = "LOCATION";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String CATEGORY = "CATEGORY";

    private static final DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;


    // opens the stream, and returns the whole file.
    public static String rawDataParser(String url) throws IOException {
        StringBuilder rawData = new StringBuilder();
        String line;
        try (InputStream in = new URL(url).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                rawData.append(line).append(System.lineSeparator());
            }
        }
        return rawData.toString();
    }

    public static ArrayList<CalendarEvent> parseRawData(String rawData){
        ArrayList<String> rawEvents = getRawEventsList(rawData);
        ArrayList<CalendarEvent> res = new ArrayList<>();
        for(String e : rawEvents){
            CalendarEvent event = new CalendarEvent();
            Scanner sc = new Scanner(e);
            while(sc.hasNext()){
                String line = sc.nextLine();
                // Detect what that line is about, to add it to the right field in our event object
                if(line.startsWith(SUMMARY)){
                    event.setSummary(line.substring(line.indexOf(":") + 1));
                } else if(line.startsWith(UID)){
                    event.setUid(line.substring(line.indexOf(":") + 1));
                } else if(line.startsWith(DTSTART)){
                    String date = line.substring(line.indexOf(":") + 1);
                    event.setStart(parseDate(date));
                } else if(line.startsWith(DTEND)){
                    String date = line.substring(line.indexOf(":") + 1);
                    event.setEnd(parseDate(date));
                } else if(line.startsWith(LOCATION)){
                    event.setLocation(line.substring(line.indexOf(":") + 1));
                } else if(line.startsWith(DESCRIPTION)){
                    event.setDescription(line.substring(line.indexOf(":") + 1));
                } else if(line.startsWith(CATEGORY)){
                    event.setCategory(line.substring(line.indexOf(":") + 1));
                }
            }
            // don't forget to close the scanner after we're done with this object!
            sc.close();
            res.add(event);
        }
        return res;
    }

    private static LocalDateTime parseDate(String line) {
        Scanner sc = new Scanner(line);
        sc.useDelimiter("T|Z");
        LocalDate date = LocalDate.parse(sc.next(), formatter);
        LocalTime time;
        if(sc.hasNext()){
            time = LocalTime.parse(sc.next(), DateTimeFormatter.ofPattern("HHmmss")).plusHours(2);
        } else {
            time = LocalTime.of(0, 0);
        }
        return LocalDateTime.of(date, time);
    }

    public static ArrayList<String> getRawEventsList(String rawData) {
        Scanner sc = new Scanner(rawData);
        ArrayList<String> res = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sc.useDelimiter("\n");
        // skip to the first VEVENT
        boolean done = false;
        while(!done && sc.hasNext()){
            if(sc.nextLine().contains("BEGIN:VEVENT")){
                done = true;
            }
        }
        // add each VEVENT to the ArrayList
        while(sc.hasNext()){
            String line = sc.nextLine();
            sb.append(line);
            if(line.contains("END:VEVENT")){
                res.add(sb.toString());
                sb = new StringBuilder();
            }
            sb.append("\n");
        }
        sc.close();
        return res;
    }
}
