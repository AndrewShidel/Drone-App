package shidel.droneapp.adapters;

import android.app.Activity;

import shidel.droneapp.UDPHandler;

/**
 * Created by andrew on 1/28/15.
 */
public class DroneThreadAdapter implements ThreadAdapter {
    private Activity act;
    public DroneThreadAdapter(Activity act){
        this.act = act;
    }
    public void run(final Object... args) {
        act.runOnUiThread(new Runnable() {
            public void run() {
                UDPHandler.UDPCallback callback = (UDPHandler.UDPCallback) args[0];
                callback.onCommand((String)args[1]);
            }
        });
    }
}
