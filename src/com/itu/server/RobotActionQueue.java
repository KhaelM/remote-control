package com.itu.server;

import java.util.LinkedList;

import com.itu.robot.RobotAction;

/**
 * RobotActionQueue
 */
public class RobotActionQueue {
    
    private LinkedList<RobotAction> jobs = new LinkedList<RobotAction>();

    public RobotAction next() throws InterruptedException {
        synchronized (jobs) {
            while (jobs.isEmpty()) {
                jobs.wait();
            }
        }
        return jobs.removeFirst();
    }

    public void add(RobotAction action) {
        synchronized(jobs) {
            jobs.add(action);
            System.out.println("jobs = " + jobs);
            jobs.notifyAll();
        }
    }
}