package shidel.droneapp;

import android.os.AsyncTask;
import shidel.droneapp.USBInterface.MaestroSSC;

/**
 * Created by andrew on 1/3/15.
 */
public class Motors {
    private int m1Speed, m2Speed, m3Speed, m4Speed;
    private MaestroSSC maestro;
    public Motors(MaestroSSC maestro){
        this.maestro = maestro;
        m1Speed=m2Speed=m3Speed=m4Speed=0;
    }

    public void armEscs(MotorTaskCallback callback) {
        new ArmEscTask(callback).execute();
    }

    public void setSpeed(int motorID, int speed, MotorTaskCallback callback){
        motorID--;
        int startSpeed = 0;
        switch (motorID){
            case 1:
                startSpeed = m1Speed;
                m1Speed = speed;
                break;
            case 2:
                startSpeed = m1Speed;
                m2Speed = speed;
                break;
            case 3:
                startSpeed = m1Speed;
                m3Speed = speed;
                break;
            case 4:
                startSpeed = m1Speed;
                m4Speed = speed;
                break;
        }
        new SetTargetTask(callback).execute(motorID, startSpeed, speed);
    }
    public int getSpeed(int motorID){
        switch (motorID){
            case 1:
                return m1Speed;
            case 2:
                return m2Speed;
            case 3:
                return m3Speed;
            case 4:
                return m4Speed;
            default:
                return 0;
        }
    }

    private class ArmEscTask extends AsyncTask<Void, Void, Void> {
        MotorTaskCallback callback;
        public ArmEscTask(MotorTaskCallback callback) {
            this.callback = callback;
        }
        @Override
        protected Void doInBackground(Void... params) {
            maestro.setTarget(0, 1500);
            maestro.setTarget(1, 1500);
            maestro.setTarget(2, 1500);
            maestro.setTarget(3, 1500);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            maestro.setTarget(0, 0);
            maestro.setTarget(1, 0);
            maestro.setTarget(2, 0);
            maestro.setTarget(3, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void res) {
            callback.finished();
        }
    }
    private class SetTargetTask extends AsyncTask<Integer, Void, Void> {
        MotorTaskCallback callback;
        public SetTargetTask(MotorTaskCallback callback) {
            this.callback = callback;
        }

        /**
         * @param params 0=channel, 1=startSpeed, 2=endSpeed
         */
        @Override
        protected Void doInBackground(Integer... params) {
            int direction = params[2] > params[1] ? 1 : -1;
            for (int i=params[1]; (i<params[2]&&direction>0) || (i>params[2]&&direction<1); i+=direction) {
                maestro.setTarget(params[0], i);
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void res) {
            callback.finished();
        }
    }

    public interface MotorTaskCallback {
        public void finished();
    }
}
