package shidel.droneapp;

import android.os.AsyncTask;
import shidel.droneapp.USBInterface.MaestroSSC;


public class Motors {
    private int m1Speed, m2Speed, m3Speed, m4Speed;
    public MaestroSSC maestro;
    public Motors(MaestroSSC maestro){
        this.maestro = maestro;
        m1Speed=m2Speed=m3Speed=m4Speed=1000;
    }

    public void armEscs(MotorTaskCallback callback) {
        new ArmEscTask(callback).execute();
    }

    public void setSpeed(int motorID, int speed) {
        setSpeed(motorID, speed, null);
    }
    public void setSpeed(int motorID, int speed, MotorTaskCallback callback){
        motorID--;
        int startSpeed = 0;
        switch (motorID){
            case 0:
                startSpeed = m1Speed;
                m1Speed = speed;
                break;
            case 1:
                startSpeed = m1Speed;
                m2Speed = speed;
                break;
            case 2:
                startSpeed = m1Speed;
                m3Speed = speed;
                break;
            case 3:
                startSpeed = m1Speed;
                m4Speed = speed;
                break;
        }
        new SetTargetTask(callback).execute(motorID, startSpeed, speed);
    }
    public void setDeltaSpeed(int s1, int s2, int s3, int s4) {
        m1Speed += s1;
        m2Speed += s4;
        m3Speed += s3;
        m4Speed += s2;
        maestro.setTarget(0, m1Speed);
        maestro.setTarget(1, m2Speed);
        maestro.setTarget(2, m3Speed);
        maestro.setTarget(3, m4Speed);
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

            for (int i=0; i<1000; i+=2) {
                maestro.setTarget(0, i);
                maestro.setTarget(1, i);
                maestro.setTarget(2, i);
                maestro.setTarget(3, i);

                try {
                    Thread.sleep(5);
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
            if (callback!=null) {
                callback.finished();
            }
        }
    }

    public interface MotorTaskCallback {
        public void finished();
    }
}
