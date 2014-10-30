/**
 * alexbalu-alpha7@mail.ru
 */
package com.abalyschev.mychat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 */
public class PaintFrame extends JFrame {

	// режим работы доски ( расшаривание или просмотр )
	public static enum Mode { SHARING, VIEWING };
	private final Mode mode;
	
	// сокет для соединения с сервером 
	private Socket paintDeskSk;
	private BufferedReader in;
	private PrintWriter out;
	
	private String login;
	
    List<Line2D.Float> lines = new ArrayList<>();
    // fix!!!
    List<Color> colors = new ArrayList<>();
    Point lastPoint;
    JPanel pane = new DrawPane();
    Color color = Color.RED;

    /**
     * Конструктор для сетевого соединения
     */
    public PaintFrame(final String login, final Mode mode, final Socket paintDeskSk) {
    	this(mode);
    	this.login			= login;
    	this.paintDeskSk 	= paintDeskSk;
    	try {
    		this.in 	= new BufferedReader(new InputStreamReader(this.paintDeskSk.getInputStream()));
        	this.out 	= new PrintWriter(this.paintDeskSk.getOutputStream());
        	// сообщение инициализации
        	this.out.write(login + ":sharing\n");
        	this.out.flush();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Конструктор для работы вне сети
     */
    public PaintFrame(final Mode mode) {
        super("Paint Frame");
        
        // режим работы
        this.mode = mode;
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        add(new ButtonPanel(), BorderLayout.NORTH);

        DrawListener listener = new DrawListener();
        pane.addMouseListener(listener);
        pane.addMouseMotionListener(listener);
    }


    // class to handle mouse action from DrawPane
    private class DrawListener extends MouseAdapter {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (lastPoint == null) {
                    lastPoint = e.getPoint();
                    return;
                }
                
                // данные о положении последней и текущей точек
                String line = lastPoint.x + ":" + lastPoint.y + ":" + e.getPoint().x + ":" + e.getPoint().y; 
                
                lines.add(new Line2D.Float(lastPoint, e.getPoint()));
                // fix!!!
                colors.add(color);
                lastPoint = e.getPoint();

                // передаем данные о положении мыши на сервер
                out.write(line+"\n");
                out.flush();
                
                // перерисовываем область окна
                pane.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                lastPoint = null;
            }
    }

    class DrawPane extends JPanel{
        public void paintComponent(Graphics g){
            // delete line below :)
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // error
            //g2d.setColor(color);
            // fix!!!
            for (Line2D.Float line : lines) {
            	int ndx = lines.indexOf(line);
            	if (ndx != -1 && ndx < colors.size()) {
            		g2d.setColor(colors.get(ndx));
            	} else {
            		g2d.setColor(color);
            	}
                g2d.draw(line);
            }
        }
    }

    class ButtonPanel extends JPanel implements ActionListener {
        JButton btnRed;
        JButton btnBlue;
        JButton btnGreen;
        JButton btnKey;

        private static final String RED_BTN = "red_btn";
        private static final String BLUE_BTN = "blue_btn";
        private static final String GREEN_BTN = "green_btn";
        private static final String BLACK_BTN = "black_btn";

        public ButtonPanel() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            btnRed = new JButton("red");
            btnRed.setBackground(Color.RED);
            btnRed.setActionCommand(RED_BTN);
            btnRed.addActionListener(this);

            btnBlue = new JButton("blue");
            btnBlue.setBackground(Color.BLUE);
            btnBlue.setActionCommand(BLUE_BTN);
            btnBlue.addActionListener(this);

            btnGreen = new JButton("green");
            btnGreen.setBackground(Color.GREEN);
            btnGreen.setActionCommand(GREEN_BTN);
            btnGreen.addActionListener(this);

            btnKey = new JButton("black");
            btnKey.setBackground(Color.BLACK);
            btnKey.setActionCommand(BLACK_BTN);
            btnKey.addActionListener(this);

            add(btnRed);
            add(btnGreen);
            add(btnBlue);
            add(btnKey);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case RED_BTN:
                    color = Color.RED;
                    break;
                case BLUE_BTN:
                    color = Color.BLUE;
                    break;
                case GREEN_BTN:
                    color = Color.GREEN;
                    break;
                case BLACK_BTN:
                    color = Color.BLACK;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        javax.swing.JFrame frame = new PaintFrame(Mode.SHARING);
        frame.setVisible(true);
    }
}
