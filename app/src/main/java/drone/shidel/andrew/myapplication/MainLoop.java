package drone.shidel.andrew.myapplication;

import android.app.Activity;

import java.io.IOException;

/**
 * Created by andrew on 1/1/15.
 */
public class MainLoop {
    private double desX, desY, desZ;
    private double granularity = 10;
    private Activity act;
    private double z = 0;

    public MainLoop(Activity act){
        this.act = act;
        desX = desY = desZ = 0;
    }

    public void start(Controller user){
        Position pos = new Position(act);
        Motors moters = new Motors();
        UDPHandler connection;
        try {
            connection = new UDPHandler("54.68.154.101", "d");
        }catch(IOException e){
            System.out.println("Could not connect");
            return;
        }
        while (true){
            String command = connection.latestMessage;
            if (command==null || command==""){
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){}
                continue;
            }

            switch (command) {
                case "up":
                    up();
                    break;
                case "down":
                    down();
                    break;
                case "left":
                    left();
                    break;
                case "right":
                    right();
                    break;
                case "forward":
                    forward();
                    break;
                case "backward":
                    backward();
                    break;
            }
            moters.setSpeed(3, (int)(moters.getSpeed(3) - pos.getRx()));
            moters.setSpeed(4, (int)(moters.getSpeed(4) - pos.getRx()));
            moters.setSpeed(1, (int)(moters.getSpeed(1) + pos.getRx()));
            moters.setSpeed(2, (int)(moters.getSpeed(2) + pos.getRx()));

            moters.setSpeed(1, (int)(moters.getSpeed(1) - pos.getRy()));
            moters.setSpeed(4, (int)(moters.getSpeed(4) - pos.getRy()));
            moters.setSpeed(2, (int)(moters.getSpeed(2) + pos.getRy()));
            moters.setSpeed(3, (int)(moters.getSpeed(3) + pos.getRy()));

            double delta = (desZ - pos.getZ());
            z = pos.getZ();
            desZ -= z;
            pos.zeroZ();
            moters.setSpeed(1, (int)(moters.getSpeed(1) + delta));
            moters.setSpeed(2, (int)(moters.getSpeed(2) + delta));
            moters.setSpeed(3, (int)(moters.getSpeed(3) + delta));
            moters.setSpeed(4, (int)(moters.getSpeed(4) + delta));
        }
    }
    private void up(){
        desZ = z + granularity;
    }
    private void down(){
        desZ = z - granularity;
    }
    private void left(){
        desX-=granularity;
    }
    private void right(){
        desX+=granularity;
    }
    private void forward(){
        desY+=granularity;
    }
    private void backward(){
        desY-=granularity;
    }
}
