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

    public void start(final Motors motors) throws IOException {
        final Position pos = new Position(act);

        final TextView dbX = (TextView) act.findViewById(R.id.debugX);

        ThreadAdapter adapter = new DroneThreadAdapter(act);
        new UDPHandler("54.68.154.101", "d", adapter, new UDPCallback() {
            @Override
            public void onCommand(String cmd) {
                dbX.setText(cmd);
                String[] parts = cmd.split("||");
                if (parts.length == 2 && parts[0].equals("leap")) {
                    XYZ<Integer> xyz = parseXYZ(parts[1]);
                    int m1, m2, m3, m4;
                    m1 = m2 = m3 = m4 = mmToTagetV(xyz.y);

                    int xDisp = mmToTagetV(xyz.x);
                    m1 += xDisp;
                    m4 += xDisp;
                    m2 -= xDisp;
                    m3 -= xDisp;

                    int zDisp = mmToTagetV(xyz.z);
                    m1 += zDisp;
                    m2 += zDisp;
                    m3 -= zDisp;
                    m4 -= zDisp;

                    motors.setSpeed(1, m1);
                    motors.setSpeed(2, m2);
                    motors.setSpeed(3, m3);
                    motors.setSpeed(4, m4);
                    return;
                }
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

    private XYZ<Integer> parseXYZ(String xyzStr) {
        XYZ<Integer> xyz = new XYZ(Integer.class);
        String[] parts = xyzStr.split(",");
        xyz.x = (int) Double.parseDouble(parts[0]);
        xyz.y = (int) Double.parseDouble(parts[1]);
        xyz.z = (int) Double.parseDouble(parts[2]);
        return xyz;
    }

    private class XYZ<T> {
        public T x,y,z;
        public XYZ(Class<T> clazz) {
            x = y = z = clazz.cast(0);
        }
    }

    private int mmToTagetV(int mm) {
        return (mm/600)*3000;
    }
}
