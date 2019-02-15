package com.itu.robot;

import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * MoveMouse
 */
public class MoveMouse implements RobotAction {
    private final static long serialVersionUID = 1L;

    private int x;
    private int y;

    @Override
    public Object execute(Robot robot) throws IOException {
        robot.mouseMove(x, y);
        return null;
    }

    public MoveMouse(Point to) {
        x = (int) to.getX();
        y = (int) to.getY();
    }

    public MoveMouse(MouseEvent event) {
        this(event.getPoint());
    } 

    @Override
    public String toString() {
        return "MoveMouse: x=" + x + ", y=" + y;
    }
}