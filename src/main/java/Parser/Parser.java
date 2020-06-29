package Parser;

import Main.CalendarEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Parser {
    // opens the stream, and returns the whole file.
    public static String rawDataParser(String url) throws IOException {
        String buffer;
        StringBuilder rawData = new StringBuilder();
        BufferedReader read = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
        while((buffer = read.readLine()) != null){
            rawData.append(buffer);
        }
        return rawData.toString();
    }

    public static ArrayList<CalendarEvent> parseRawData(String rawData){
        // TODO : parse raw data function
        return null;
    }
}
