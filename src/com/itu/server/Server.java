package com.itu.server;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.itu.robot.ClickMouse;
import com.itu.robot.MoveMouse;
import com.itu.robot.PressKey;
import com.itu.robot.RobotAction;
import com.itu.robot.ScreenShot;

/**
 * Server
 */
public class Server extends JFrame {
    private final static long serialVersionUID = 1L;
    
    public final static int PORT = 5555;
    public final static int SCREEN_SHOT_PERIOD = 500;
    public final static int SCREEN_SHOT_DELAY = 3;
    public final static int WINDOW_HEIGHT = 400;
    public final static int WINDOW_WIDTH = 500;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientName;
    private JLabel iconLabel = new JLabel();
    private RobotActionQueue jobs = new RobotActionQueue();
    private Thread writer;
    private Timer timer;
    private boolean running = true;
    private Socket socket;

    public Server(Socket socket) throws IOException, ClassNotFoundException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        clientName = (String) in.readObject();
        this.setupUI();

        createReaderThread();
        timer = createScreenShotThread();
        writer = createWriterThread();


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.cancel();

                try {
                    socket.close();
                    out.close();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    in.close();
                } catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        System.out.println("Connexion a " + socket + " fermee avec succes!");
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    private Thread createWriterThread() {
        Thread writer = new Thread("Writer") {
            @Override
            public void run() {
                try {
                    while(true) {
                        RobotAction action = jobs.next();
                        out.writeObject(action);
                        out.flush();
                    }
                } catch (Exception e) {
                    System.out.println("Connexion a " + clientName + " fermee (" + e + ")");
                    setTitle(getTitle() + " - deconnecte");
                }
            }
        };
        writer.start();
        return writer;
    }

    private Timer createScreenShotThread() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
        
            @Override
            public void run() {
                jobs.add(new ScreenShot());     
            }
        }, SCREEN_SHOT_DELAY, SCREEN_SHOT_PERIOD);
        return timer;
    }

    private void setupUI() {
        this.setTitle("Capture de l'ecran de " + clientName);
        this.getContentPane().add(new JScrollPane(iconLabel));

        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if(running) {
                    jobs.add(new MoveMouse(event));
                    jobs.add(new ClickMouse(event));
                    jobs.add(new ScreenShot());
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        iconLabel.addMouseMotionListener(new MouseMotionListener(){
        
            @Override
            public void mouseMoved(MouseEvent event) {
                if(running) {
                    jobs.add(new MoveMouse(event));
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        
            @Override
            public void mouseDragged(MouseEvent event) {}
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(running) {
                    jobs.add(new PressKey(e));
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setVisible(true);
    }

    private void createReaderThread() {
        Thread readThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        byte[] img = (byte[]) in.readObject();
                        System.out.println("Capture d'ecran de "+ clientName + " recu. [" + img.length + " bytes]");
                        showIcon(img);
                    } catch (Exception e) {
                        System.out.println("Exception levee: " + e);
                        writer.interrupt();
                        timer.cancel();
                        running = false;
                        return;
                    }
                }
            }

        };
        readThread.start();
    }

    private void showIcon(byte[] byteImage) throws IOException {
        ByteArrayInputStream bin = new ByteArrayInputStream(byteImage);
        BufferedImage img = ImageIO.read(bin);
        SwingUtilities.invokeLater(new Runnable(){
        
            @Override
            public void run() {
                iconLabel.setIcon(new ImageIcon(img));
            }
        });
    }


    public static void main(String[] args) {
        Socket socket = null;
        ServerSocket serverSocket = null;
        ArrayList<Server> list = new ArrayList<Server>();
        try {
            serverSocket = new ServerSocket(PORT);
            while(true) {
                socket = serverSocket.accept();
                System.out.println("Tentative de connexion de " + socket);
                list.add(new Server(socket));
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}