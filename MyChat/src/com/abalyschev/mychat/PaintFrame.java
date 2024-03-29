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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class PaintFrame extends JFrame {

	Logger log = LoggerFactory.getLogger(PaintFrame.class);
	
	// режим работы доски ( расшаривание, просмотр, без соединения )
	public static enum Mode { SHARING, VIEWING, NONE };
	private final Mode mode;
	
	// логин вызывателя и получателя
	private String login;
	private String toLogin;
	
	// доступные цвета
	private static final String RED_BTN = "red_btn";
    private static final String BLUE_BTN = "blue_btn";
    private static final String GREEN_BTN = "green_btn";
    private static final String BLACK_BTN = "black_btn";
	
	private static final String COMMAND_EXIT = ".exit";
	
    List<Line2D.Float> lines = new ArrayList<>();
    // fix!!!
    List<Color> colors = new ArrayList<>();
    Point lastPoint;
    JPanel pane = new DrawPane();
    Color color = Color.RED;
    
    // сокет для соединения с сервером 
	private Socket paintDeskSk;
	private BufferedReader in;
	private PrintWriter out;
    
    // обработчик входных команд
    private DataHandler dataHandler;
    
    /**
     * Конструктор для сетевого соединения (на предоставление)
     */
    public PaintFrame(final String login, final Mode mode, final Socket paintDeskSk) {
    	this(mode);
    	this.login			= login;
    	this.paintDeskSk 	= paintDeskSk;
    	try {
    		this.in 	= new BufferedReader(new InputStreamReader(this.paintDeskSk.getInputStream()));
        	this.out 	= new PrintWriter(this.paintDeskSk.getOutputStream());
        	
        	// инициализация доски на предоставление
        	this.out.write(login + ":sharing\n");
            this.out.flush();

    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Конструктор для сетевого соединения (на просмотр)
     */
    public PaintFrame(final String login, final String toLogin, final Mode mode, final Socket paintDeskSk) {
    	this(mode);
    	this.login			= login;
    	this.toLogin		= toLogin;
    	this.paintDeskSk 	= paintDeskSk;
    	try {
    		this.in 	= new BufferedReader(new InputStreamReader(this.paintDeskSk.getInputStream()));
        	this.out 	= new PrintWriter(this.paintDeskSk.getOutputStream());
        	// инициализация лоски на просмотр
        	this.out.write(login + ":viewing:" + this.toLogin + "\n");
            this.out.flush();
            // запуск обработчика данных
        	this.dataHandler = new DataHandler();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }
    
    /**
     * Создание линии
     */
    public void drawLine(final Point lstPoint, final Point crntPoint) {
    	lines.add(new Line2D.Float(lstPoint, crntPoint));
        // fix!!!
        colors.add(color);
        lastPoint = crntPoint;
        // перерисовываем область окна
        pane.repaint();
    }
    
    /**
     * установка текущего цвета
     */
    public void setLineColor(final String command) {
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
    
    public void drawTestLine() {
    	drawLine(new Point(20, 20), new Point(120, 20));
    	drawLine(new Point(120, 20), new Point(120, 220));
    	drawLine(new Point(20, 40), new Point(120, 70));
    	drawLine(new Point(20, 20), new Point(120, 200));
    }
    
    /**
     * Конструктор для работы вне сети
     */
    public PaintFrame(final Mode mode) {
        super("Paint Frame");
        
        // режим работы
        this.mode = mode;
        
        int close_mode = JFrame.DISPOSE_ON_CLOSE;
        if ( mode == Mode.SHARING ) {
        	close_mode = JFrame.DO_NOTHING_ON_CLOSE;
        }
        
        setDefaultCloseOperation(close_mode);
        setSize(500, 500);
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
                
                // в режиме просмотра манипуляции с доской невозможны 
                if ( mode == Mode.VIEWING ) {
                	return;
                }
                
                if (lastPoint == null) {
                    lastPoint = e.getPoint();
                    return;
                }
                
                // данные о положении последней и текущей точек
                String line = "drawLine:" + lastPoint.x + "&" + lastPoint.y + ":" + e.getPoint().x + "&" + e.getPoint().y; 
                
                // в режиме предоставления передаем данные о положении мыши на сервер
                if ( mode == Mode.SHARING ) {
                	out.write(line+"\n");
                    out.flush();
                }
                
                // данные для создания и отрисовки новой линии
                drawLine(lastPoint, e.getPoint());
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
            PaintFrame.this.setLineColor(command);
            if ( mode == Mode.SHARING ) {
            	out.write("setLineColor:" + command + "\n");
            	out.flush();
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
    
    // -------------------------------INNER CLASS--------------------------
    /**
     * Класс обработчик данных, присылаемых с расшаренной доски
     */
    private class DataHandler implements Runnable {
    	private Thread thread;
    	
    	public DataHandler() {
    		this.thread		= new Thread(this);
    		this.thread.start();
    	}
    	
    	@Override
    	public void run() {
    		log.info("Do sharing data handling");
    		try {
    			String line = null;
    			log.info("isClosed: " 		+ paintDeskSk.isClosed() 
    					+ " isConnected: " 	+ paintDeskSk.isConnected() 
    					+ " isBound: " 		+ paintDeskSk.isBound());
        		while ( (line = in.readLine()) != null ) {
        			log.info("Desk viewing info: " + line);
        			// выполняем команду
        			DeskCmdDecoder.executeCmd(line, PaintFrame.this);
        			if ( COMMAND_EXIT.equals(line) ) {
        				// выход из цикла
        				break;
        			}
        		}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
}
