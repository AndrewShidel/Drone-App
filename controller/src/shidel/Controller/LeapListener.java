package shidel.Controller;

import com.leapmotion.leap.*;
import shidel.droneapp.Polar3D;

public class LeapListener extends Listener {
    private XYZ pos = new XYZ();
    private XYZ basePos = null;
    private XYZ deltaPos =  null;

    /**
     * Initialize a new LeapListener
     */
    public LeapListener(){
        basePos = new XYZ();
        basePos.x = basePos.y = basePos.z = Float.MAX_VALUE;
    }

    /**
     * Zeros out the leap controller data
     */
    public void setBasePos() {
        basePos = pos.copy();
        deltaPos = new XYZ();
    }

    /**
     * Gets the hand position.
     * @return hand position in the form "x,y,z"
     */
    public String getPos() {
        return pos.toString();
    }

    /**
     * Get the distance from the origin.
     * @return distance in the form "x,y,z"
     */
    public String getDeltaPos() {
        if (deltaPos != null) {
            return deltaPos.toString();
        } else {
            return "0,0,0";
        }
    }

    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    /**
     * Called when a leap is connected
     * @param controller
     */
    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    /**
     * Called when the leap is disconnected
     * @param controller
     */
    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }


    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    /**
     * Called when new data is available.
     * @param controller
     */
    public void onFrame(Controller controller) {

        Frame frame = controller.frame();

        //Get hands
        for (Hand hand : frame.hands()) {
            Vector posVector = hand.palmPosition();
            pos.x = posVector.getX();
            pos.y = posVector.getY();
            pos.z = posVector.getZ();
            if (deltaPos != null) {
                deltaPos.x = pos.x - basePos.x;
                deltaPos.y = pos.y - basePos.y;
                deltaPos.z = pos.z - basePos.z;
            }
            //System.out.println("Pos: " + pos.toString());
            Polar3D polar3D = new Polar3D(pos.x, pos.y, pos.z);
            polar3D.toDegrees();
            System.out.print("\r"+polar3D.toString());
        }
    }

    /**
     * A 3D vector
     */
    private static class XYZ {
        public float x,y,z;
        public XYZ() {
            x = y = z = 0;
        }
        public XYZ(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return String.format("%.3f,%.3f,%.3f", x,y,z);
        }
        public XYZ copy() {
            return new XYZ(x,y,z);
        }
    }
}
