package shidel.droneapp;

import android.app.Activity;
import android.widget.TextView;
import java.io.IOException;
import java.util.regex.Pattern;

import shidel.droneapp.UDPHandler.UDPCallback;
import shidel.droneapp.adapters.DroneThreadAdapter;
import shidel.droneapp.adapters.ThreadAdapter;

public class MainLoop {
    private int desPower = 1000;
    private double desPitch, desRoll;
    private double granularity = Math.PI/90;
    private Activity act;
    private int accel = 300;
    private boolean stop = false;

    public MainLoop(Activity act) {
        this.act = act;
        desPitch = desRoll = 0;
    }

    public void start(final Motors motors) throws IOException {
        final Position position = new Position(Position.Type.ORIENTATION, act);

        final TextView dbX = (TextView) act.findViewById(R.id.debugX);

        ThreadAdapter adapter = new DroneThreadAdapter(act);
        new UDPHandler("d", adapter, new UDPCallback() {
            @Override
            public void onCommand(String cmd) {
                if (stop) {
                    return;
                }
                if (cmd.contains("stop")) {
                    motors.setSpeed(1, 0);
                    motors.setSpeed(2, 0);
                    motors.setSpeed(3, 0);
                    motors.setSpeed(4, 0);
                    stop = true;
                    return;
                }

                double rollDiff, pitchDiff;
                int m1, m2, m3, m4;
                float[] rotationMatrix = position.getRotation(); // [azimuth, pitch, roll]

                String[] parts = cmd.split(Pattern.quote("||"));
                if (parts.length == 2 && parts[0].equals("leapd")) {
                    String[] xyz = parts[1].split(",");
                    Polar3D rot = new Polar3D(Double.parseDouble(xyz[0]),Double.parseDouble(xyz[1]),Double.parseDouble(xyz[2]));

                    desPower = mmToTargetV((int) rot.radius);
                    desRoll = rot.roll;
                    desPitch = rot.pitch;
                }else {
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
                m1 = m2 = m3 = m4 = desPower;
                rollDiff = rotationMatrix[2] - desRoll;
                pitchDiff = rotationMatrix[1] - desPitch;

                m1 += rollDiff * accel;
                m2 -= rollDiff * accel;
                m3 -= rollDiff * accel;
                m4 += rollDiff * accel;

                m1 -= pitchDiff * accel;
                m2 -= pitchDiff * accel;
                m3 += pitchDiff * accel;
                m4 += pitchDiff * accel;

                motors.maestro.setTarget(0, m1);
                motors.maestro.setTarget(1, m4);
                motors.maestro.setTarget(2, m3);
                motors.maestro.setTarget(3, m2);
            }
        });
    }

    private void up() {
       desPower += 100;
    }

    private void down() {
       desPower -= 100;
    }

    private void left() {
        desRoll -= granularity;
    }

    private void right() {
        desRoll += granularity;
    }

    private void forward() {
        desPitch += granularity;
    }

    private void backward() {
        desPitch -= granularity;
    }

    private int mmToTargetV(int mm) {
        return Math.min(3000, (mm-80)*4+1000); // Subtract 80 to bring min mm to 0. Multiply by 4 to bring range to [0-2000]. Add 1000 to bring range to [1000-3000].
    }
}
