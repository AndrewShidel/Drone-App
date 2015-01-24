package drone.shidel.andrew.myapplication;

/**
 * Created by andrew on 1/3/15.
 */
public class Motors {
    private int m1Speed, m2Speed, m3Speed, m4Speed;
    public Motors(){
        m1Speed=m2Speed=m3Speed=m4Speed=0;
    }
    public void setSpeed(int motorID, int speed){
        //TODO: Send signal to motor
        switch (motorID){
            case 1:
                m1Speed = speed;
                break;
            case 2:
                m2Speed = speed;
                break;
            case 3:
                m3Speed = speed;
                break;
            case 4:
                m4Speed = speed;
                break;
        }
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
}
