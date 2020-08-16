package Main;

import java.util.TimerTask;

public class SaveThread extends TimerTask {
    public void run() {
        Main.saveSerializedItemList();
    }
}
