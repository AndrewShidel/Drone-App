package shidel.Controller;

import com.leapmotion.leap.*;

public class LeapListener extends Listener {
    private XYZ pos = new XYZ();
    private XYZ basePos = null;
    private XYZ deltaPos =  null;

    public LeapListener(){
        basePos.x = basePos.y = basePos.z = Float.MAX_VALUE;
    }

    public void setBasePos() {
        basePos = pos.copy();
        deltaPos = new XYZ();
    }

    public String getDeltaPos() {
        return deltaPos.toString();
    }

    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        System.out.println("Frame id: " + frame.id()
                + ", timestamp: " + frame.timestamp()
                + ", hands: " + frame.hands().count()
                + ", fingers: " + frame.fingers().count()
                + ", tools: " + frame.tools().count()
                + ", gestures " + frame.gestures().count());

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
        }
    }
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
