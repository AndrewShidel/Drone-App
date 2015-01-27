package shidel.Controller;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import shidel.droneapp.UDPHandler;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

public class Main extends JPanel {
    private static final int PREF_W = 400;
    private static final int PREF_H = PREF_W;
    private static final String serverURL = "54.68.154.101";
    private UDPHandler controller;

    public Main() {
        try {
            controller = new UDPHandler(serverURL, "c");
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //myKeyEvt(e, "keyTyped");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //myKeyEvt(e, "keyReleased");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                myKeyEvt(e, "keyPressed");
            }

            private void myKeyEvt(KeyEvent e, String text) {
                int key = e.getKeyCode();

                switch (key) {
                    case KeyEvent.VK_LEFT:
                        sendCmd("left");
                        break;
                    case KeyEvent.VK_RIGHT:
                        sendCmd("right");
                        break;
                    case KeyEvent.VK_UP:
                        sendCmd("up");
                        break;
                    case KeyEvent.VK_DOWN:
                        sendCmd("down");
                        break;
                }
            }
        });
    }

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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGui();
            }
        });
    }
}