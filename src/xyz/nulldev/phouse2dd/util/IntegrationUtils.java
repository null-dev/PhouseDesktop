package xyz.nulldev.phouse2dd.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Project: Phouse2DD
 * Created: 14/12/15
 * Author: nulldev
 */
public class IntegrationUtils {
    static Robot robot = null;
    static Robot initAndGetRobot() {
        if(robot == null) try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Failed to setup robot!", e);
        }
        return robot;
    }
    public static void moveMouse(int x, int y) {
        initAndGetRobot().mouseMove(x, y);
    }
    static double maxX = -1;
    static double maxY = -1;
    public static void setupSizes() {
        if(maxX == -1) maxX = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        if(maxY == -1) maxY = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }
    public static void proportionalMouseMove(float x, float y) {
        setupSizes();
        int targetX;
        if(x > 1) targetX = (int) maxX;
        else if(x < 0) targetX = 0;
        else targetX = (int) (maxX * x);
        int targetY;
        if(y > 1) targetY = (int) maxY;
        else if(y < 0) targetY = 0;
        else targetY = (int) (maxY * y);
        moveMouse(targetX, targetY);
    }
    public static void mouseScroll(int steps, int direction) {
        //Currently only support vertical scrolling
        if(direction == 2) {
            initAndGetRobot().mouseWheel(steps);
        }
    }

    public static void mouseDown() {
        initAndGetRobot().mousePress(InputEvent.BUTTON1_MASK);
    }

    public static void mouseUp() {
        initAndGetRobot().mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public static void rightMouseDown() {
        initAndGetRobot().mousePress(InputEvent.BUTTON3_MASK);
    }

    public static void rightMouseUp() {
        initAndGetRobot().mouseRelease(InputEvent.BUTTON3_MASK);
    }

    public static void keyPress(char target) {
        if(Character.isUpperCase(target)) {
            initAndGetRobot().keyPress(KeyEvent.VK_SHIFT);
        }

        keyEvent(KeyEvent.getExtendedKeyCodeForChar(target));

        if(Character.isUpperCase(target)) {
            initAndGetRobot().keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    public static void keyEvent(int keycode) {
        initAndGetRobot().keyPress(keycode);
        initAndGetRobot().keyRelease(keycode);
    }

    static JFrame frame = null;
    public static void setupTarget(int x, int y) {
        if(frame == null) {
            setupSizes();
            if(x < 50) {
                x += 50;
            }
            if(x > maxX - 50) {
                x -= 50;
            }
            if(y < 50) {
                y += 50;
            }
            if(y > maxY - 50) {
                y -= 50;
            }
            frame = new JFrame();
            frame.setSize(100,100);
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setBackground(Color.RED);
            frame.setLocation(x + 50, y + 50);
        } else {
            teardownTarget();
            setupTarget(x, y);
        }
    }
    public static void teardownTarget() {
        if(frame != null) {
            frame.setVisible(false);
            frame = null;
        }
    }

    public static void spawnTarget(float x, float y) {
        int targetX;
        if(x > 1) targetX = (int) maxX;
        else if(x < 0) targetX = 0;
        else targetX = (int) (maxX * x);
        int targetY;
        if(y > 1) targetY = (int) maxY;
        else if(y < 0) targetY = 0;
        else targetY = (int) (maxY * y);
        setupTarget(targetX, targetY);
    }
}
