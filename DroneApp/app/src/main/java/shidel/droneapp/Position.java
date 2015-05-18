package shidel.droneapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.Arrays;

public class Position {
    private float[] mValuesMagnet      = new float[3];
    private float[] mValuesAccel       = new float[3];
    private float[] mValuesOrientation = new float[3];
    private float[] mRotationMatrix    = new float[9];

    public enum Type {
        ORIENTATION
    }
    public Position(Type sensorType, Context ctx) {
        if (sensorType == Type.ORIENTATION){
            OrientationListener listener = new OrientationListener();
            listener.start(ctx);
        }
    }
    public float[] getRotation() {
        SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
        SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);
        return mValuesOrientation;
    }
    private class OrientationListener implements SensorEventListener {
        public void start(Context ctx) {
            SensorManager mSensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
            Sensor mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        public void onSensorChanged(SensorEvent event) {
            // Handle the events for which we registered
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    System.arraycopy(event.values, 0, mValuesAccel, 0, 3);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    System.arraycopy(event.values, 0, mValuesMagnet, 0, 3);
                    break;
            }
            Log.d("Rotation", Arrays.toString(getRotation()));
        }
    }
}
