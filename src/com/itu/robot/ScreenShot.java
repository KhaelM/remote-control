package com.itu.robot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * ScreenShot
 */
public class ScreenShot implements RobotAction {

    private static final long serialVersionUID = 1L;

    @Override
    public Object execute(Robot robot) throws IOException {
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Rectangle shotArea = new Rectangle(defaultToolkit.getScreenSize());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        BufferedImage image = robot.createScreenCapture(shotArea);
        ImageIO.write(image, "jpg", bout); // changed
        return bout.toByteArray();
    }

    @Override
    public String toString() {
        return "ScreenShot";
    }
}