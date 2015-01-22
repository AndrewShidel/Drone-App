package drone.shidel.andrew.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by andrew on 1/3/15.
 */
public class Test implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private double x,y,z;
    private double velocity = 0;
    private double accAverage = 0;
    private int accCount = 0;
    private float accTime = 0;

    public double getRx() {
        return rx;
    }

    public double getRy() {
        return ry;
    }
    public double theta() {
        return theta;
    }

    private double rx, ry, theta;
    private Activity act;
    private TextView dbX, dbY, dbZ;
    private static final float NS2S = 1.0f / 1000000000.0f;
    public static final float EPSILON = 0.000000001f;
    private final float[] deltaRotationVector = new float[4];
    private float timestamp;
    private float uiTimestamp=0;
    private float[] average = new float[3];
    private int averageCount = 0;
    private Boolean calebrating = true;
    private double accOffset = 0;
    private float startTime = System.nanoTime();

    public Test(Activity act) {
        this.act = act;
        senSensorManager = (SensorManager) act.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        x=y=z=0;
        dbX = (TextView)act.findViewById(R.id.debugX);
        dbY = (TextView)act.findViewById(R.id.debugY);
        dbZ = (TextView)act.findViewById(R.id.debugZ);
        dbX.setText("bla");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Toast.makeText(act, Arrays.toString(event.values),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
