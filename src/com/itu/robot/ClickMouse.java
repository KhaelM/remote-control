package com.itu.robot;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * ClickMouse
 */
public class ClickMouse implements RobotAction {

    private final static long serialVersionUID = 1L;

    private int mouseButtonsPattern;
    int clickedButton;
    private int clicks;

    @Override
    public Object execute(Robot robot) throws IOException {
        // if its not a right click then it's a left click
        clickedButton = ((mouseButtonsPattern & InputEvent.BUTTON2_DOWN_MASK) == InputEvent.BUTTON2_DOWN_MASK) ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON1_DOWN_MASK;

        for (int i = 0; i < clicks; i++) {
            robot.mousePress(this.clickedButton);
            robot.mouseRelease(this.clickedButton);
        }

        return null;
    }

    @Override
    public String toString() {
        return "ClickMouse: " + clickedButton + ", " + clicks;
    }

    public ClickMouse(int mouseButtonsPattern, int clicks) {
        this.mouseButtonsPattern = mouseButtonsPattern;
        this.clicks = clicks;
    }

    public ClickMouse(MouseEvent event) {
        this(event.getModifiersEx() , event.getClickCount());
    }
}