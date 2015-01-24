package drone.shidel.andrew.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by andrew on 1/1/15.
 */
public class Position implements SensorEventListener{
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
    private float nonAccelerationTimeStart = 0;


    public Position(Activity act){
        this.act = act;
        senSensorManager = (SensorManager) act.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        x=y=z=0;
        dbX = (TextView)act.findViewById(R.id.debugX);
        dbY = (TextView)act.findViewById(R.id.debugY);
        dbZ = (TextView)act.findViewById(R.id.debugZ);
        dbX.setText("bla");
    }

    public double getTiltX(){
        return 0;
    }
    public double getTiltY(){
        return 0;
    }
    public double getX(){
        return 0;
    }
    public double getY(){
        return 0;
    }
    public double getZ(){
        return z;
    }
    public double getAccX(){
        return 0;
    }
    public double getAccY(){
        return 0;
    }
    public double getAccZ(){
        return 0;
    }

    public void zeroZ(){
        z = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_LINEAR_ACCELERATION:
                processAccel(event);
                break;
            case Sensor.TYPE_GYROSCOPE:
                processGyro(event);
                break;
            case Sensor.TYPE_ORIENTATION:
                float[] vals = event.values;
                theta = 0;
                rx = vals[1];
                ry = vals[2];
                dbX.setText(Float.toString(vals[0]));
                dbY.setText(Float.toString(vals[1]));
                dbZ.setText(Float.toString(vals[2]));
                break;
        }
    }
    private void processAccel(SensorEvent event){
        if (calebrating){
            if (event.timestamp - startTime > 10000000000f){
                calebrating = false;
                accCount = 0;
                Toast.makeText(act, "Offset: "+accOffset,
                        Toast.LENGTH_SHORT).show();
                accTime = event.timestamp;
            }else{
                accOffset = (accOffset*accCount+event.values[2])/(accCount+1);
                accCount++;
            }
            return;
        }

        //TODO: Adjust for tilt (trig)
        event.values[2] -= accOffset;

        if (Math.abs(event.values[2])<.1) {
            event.values[2] = 0;
            if (nonAccelerationTimeStart==0) {
                nonAccelerationTimeStart = event.timestamp;
            }else if(event.timestamp - nonAccelerationTimeStart > .1*Math.pow(10, 9)){
                velocity = 0;
            }
        }else{
            nonAccelerationTimeStart = 0;
        }

        double deltaT = (event.timestamp - accTime)/Math.pow(10, 9);

        velocity += event.values[2]*deltaT;
        z += velocity*deltaT;


        dbX.setText(Double.toString(z));
        dbY.setText(Double.toString(velocity));
        dbZ.setText(Double.toString(event.values[2]));

        accTime = event.timestamp;
        /*
        if (event.timestamp-accTime > 400000000){
            velocity += accAverage;
            z += velocity;
            accTime = event.timestamp;

            dbX.setText(Double.toString(z));
            dbY.setText(Double.toString(velocity));
            dbZ.setText(Double.toString(accAverage));

            accAverage = 0;
            accCount = 0;
        }else{
            accAverage = (accAverage*accCount+event.values[2])/(accCount+1);
            accCount++;
        }*/


    }

    private void processGyro(SensorEvent event){
        // This timestep's delta rotation to be multiplied by the current rotation
        // after computing it from the gyro sample data.
        if (timestamp != 0) {
            final float dT = (event.timestamp - timestamp) * NS2S;
            // Axis of the rotation sample, not normalized yet.
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float)Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > EPSILON) {
                axisX /= omegaMagnitude;
                axisY /= omegaMagnitude;
                axisZ /= omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * dT / 2.0f;
            float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * axisX;
            deltaRotationVector[1] = sinThetaOverTwo * axisY;
            deltaRotationVector[2] = sinThetaOverTwo * axisZ;
            deltaRotationVector[3] = cosThetaOverTwo;
        }

        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;

        average = averageArrays(average, deltaRotationMatrix, averageCount);
        if (event.timestamp - uiTimestamp > 500000000) {
            for (int i=0; i<average.length; i++) {
                average[i] = (float)Math.toDegrees(average[i]);
            }
            dbX.setText(Arrays.toString(average)+ " test");
            uiTimestamp=event.timestamp;
            averageCount = 0;
            average[0]=average[1]=average[2]=0;
        }

        timestamp = event.timestamp;
    }

    private float[] averageArrays(float[] oldArr, float[] newArr, int count){
        float[] returnArr = new float[3];
        for (int i=0; i<oldArr.length; i++){
            returnArr[i] = ((oldArr[i]*count+newArr[i])/(count+1));
        }
        return returnArr;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
