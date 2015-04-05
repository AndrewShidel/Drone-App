package shidel.droneapp;

import android.app.Activity;
import android.widget.TextView;

import java.io.IOException;

import shidel.droneapp.UDPHandler.UDPCallback;
import shidel.droneapp.adapters.DroneThreadAdapter;
import shidel.droneapp.adapters.ThreadAdapter;

/**
 * Created by andrew on 1/1/15.
 */
public class MainLoop {
    private double desX, desY, desZ;
    private double granularity = 10;
    private Activity act;
    private double z = 0;

    public MainLoop(Activity act) {
        this.act = act;
        desX = desY = desZ = 0;
    }

    public void start(Motors motors) throws IOException {
        Position pos = new Position(act);

        final TextView dbX = (TextView) act.findViewById(R.id.debugX);

        ThreadAdapter adapter = new DroneThreadAdapter(act);
        new UDPHandler("54.68.154.101", "d", adapter, new UDPCallback() {
            @Override
            public void onCommand(String cmd) {
                dbX.setText(cmd);

                switch (cmd) {
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
            }
        });

        while(true) {
            motors.setSpeed(3, (int) (motors.getSpeed(3) - pos.getRx()));
            motors.setSpeed(4, (int) (motors.getSpeed(4) - pos.getRx()));
            motors.setSpeed(1, (int) (motors.getSpeed(1) + pos.getRx()));
            motors.setSpeed(2, (int) (motors.getSpeed(2) + pos.getRx()));

            motors.setSpeed(1, (int) (motors.getSpeed(1) - pos.getRy()));
            motors.setSpeed(4, (int) (motors.getSpeed(4) - pos.getRy()));
            motors.setSpeed(2, (int) (motors.getSpeed(2) + pos.getRy()));
            motors.setSpeed(3, (int) (motors.getSpeed(3) + pos.getRy()));

            double delta = (desZ - pos.getZ());
            z = pos.getZ();
            desZ -= z;
            pos.zeroZ();
            motors.setSpeed(1, (int) (motors.getSpeed(1) + delta));
            motors.setSpeed(2, (int) (motors.getSpeed(2) + delta));
            motors.setSpeed(3, (int) (motors.getSpeed(3) + delta));
            motors.setSpeed(4, (int) (motors.getSpeed(4) + delta));

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void up() {
        desZ = z + granularity;
    }

    private void down() {
        desZ = z - granularity;
    }

    private void left() {
        desX -= granularity;
    }

    private void right() {
        desX += granularity;
    }

    private void forward() {
        desY += granularity;
    }

    private void backward() {
        desY -= granularity;
    }
}
