package Main;

import java.util.TimerTask;

public class UpdateThread extends TimerTask {
    public void run() {
        Main.update();
    }
}
