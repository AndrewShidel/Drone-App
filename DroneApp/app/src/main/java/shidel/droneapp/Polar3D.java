package shidel.droneapp;

/**
 * Created by andrew on 5/11/15.
 */
public class Polar3D {
    public double radius = 0;
    public double roll = 0; // x-axis rotation, to the left is negative
    public double pitch = 0; // z-axis rotation, toward the screen is negative

    public Polar3D (double x, double y, double z) {
        radius = Math.sqrt(x*x + y*y + z*z);
        roll = Math.atan(x/y);
        pitch = Math.acos(z/y)-Math.PI/2;
    }
    public String toString() {
        return "Pitch: " + pitch + ", Roll: " + roll + ", Radius: " + radius;
    }
    public void toDegrees() {
        roll = Math.toDegrees(roll);
        pitch = Math.toDegrees(pitch);
    }
}
