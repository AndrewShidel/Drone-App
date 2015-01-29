package shidel.droneapp.adapters;

import shidel.droneapp.UDPHandler;

/**
 * Created by andrew on 1/28/15.
 */
public class ControllerThreadAdapter implements ThreadAdapter {
    public void run(Object... args){
        UDPHandler.UDPCallback callback = (UDPHandler.UDPCallback) args[0];
        callback.onCommand((String)args[1]);
    }
}
