package shidel.Controller;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.*;

import shidel.droneapp.UDPHandler;
import com.leapmotion.leap.*;

public class Main extends JPanel {
    private static final int PREF_W = 400;
    private static final int PREF_H = PREF_W;
    private static LeapListener listener;

    private UDPHandler controller;

    public Main() {
        try {
            controller = new UDPHandler("c", null, null);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                switch (key) {
                    case KeyEvent.VK_LEFT:
                        sendCmd("left");
                        break;
                    case KeyEvent.VK_RIGHT:
                        sendCmd("right");
                        break;
                    case KeyEvent.VK_UP:
                        sendCmd("forward");
                        break;
                    case KeyEvent.VK_DOWN:
                        sendCmd("backward");
                        break;
                    case KeyEvent.VK_SHIFT:
                        sendCmd("up");
                        break;
                    case KeyEvent.VK_CONTROL:
                        sendCmd("down");
                        break;
                    case KeyEvent.VK_SPACE:
                        listener.setBasePos();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        sendCmd("stop");
                        System.exit(0);
                        break;
                }
            }
        });
        Runnable mainLoop =  () -> { startLoop(); };
        new Thread(mainLoop).start();
    }

    /**
     * Uses a background thread to check leap for new data.
     */
    public void startLoop() {
        while (true) {
            String posStr = listener.getPos();
            if (!posStr.contains("0.000")) {
                sendCmd("leapd||" + posStr);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a message through the server to the UAV.
     * @param cmd The command/message to be send
     */
    public void sendCmd(String cmd){
        System.out.println(cmd);
        try {
            controller.sendMessage(cmd);
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    private static void createAndShowGui() {
        Main mainPanel = new Main();
        JFrame frame = new JFrame("ArrowTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        listener = new LeapListener();
        controller.addListener(listener);

        SwingUtilities.invokeLater(() -> createAndShowGui());

        // Prevent from exiting.
        while (true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}