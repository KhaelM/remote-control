package com.itu.robot;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * PressKey
 */
public class PressKey implements RobotAction {

    private final static long serialVersionUID = 1L;

    private int keyCode;

    @Override
    public Object execute(Robot robot) throws IOException {
        robot.keyPress(this.keyCode);
        robot.keyRelease(this.keyCode);
        return null;
    }

    public PressKey(int keyCode) {
        this.keyCode = keyCode;
    }

    public PressKey(KeyEvent e) {
        this(e.getKeyCode());
    }

    @Override
    public String toString() {
        return "Press key: " + keyCode;
    }
}