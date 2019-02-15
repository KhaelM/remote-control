package com.itu.robot;

import java.awt.Robot;
import java.io.IOException;
import java.io.Serializable;

/**
 * RobotAction
 */
public interface RobotAction extends Serializable {

    Object execute(Robot robot) throws IOException;
}