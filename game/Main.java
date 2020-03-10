package game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import framework.*;


public class Main extends Game
{
	private static final long serialVersionUID = 1L;
	private static int WIDTH = (int)(768 * 1.2);
	private static int HEIGHT = (int)(432 * 1.2);
	private double scale;
	
	private UserPrefs preferences; 
	
	private boolean debug = false;
	private Thread thread;
	private boolean running = false;
	private boolean mouseDown = false;
	
	private boolean initReady = false;
	
	private LevelManager manager;
	private KeyState keys;
	
	private TestLevel levelA;
	private Menu menu;
	
	private Point mouseClick;
	

	
	private GraphicsConfiguration gc;
	
	@Override
	public void init()
	{	
		File prefFile = new File("preferences.txt");
		if (prefFile.exists())
		{
			try {
				FileInputStream fileInStream = new FileInputStream("preferences.txt");
				ObjectInputStream prefStream = new ObjectInputStream(fileInStream);
		        preferences = (UserPrefs) prefStream.readObject();
		        prefStream.close();
		        prefStream.close();
			} 
			catch (ClassNotFoundException | IOException e1) {
				System.out.println("What should never have happened, happened");
			}
		}
		else
		{
			preferences = new UserPrefs();
		}
		
		scale = Double.valueOf(preferences.getScreenWidth()) / WIDTH; // Need to cast to double here, or it will round
		
		System.out.println(preferences.getScreenWidth());
		System.out.println(getFrame().getWidth());
		getFrame().setSize(new Dimension(preferences.getScreenWidth(), preferences.getScreenHeight()));
		System.out.println(getFrame().getWidth());
		getFrame().addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	        }

	        @Override
	        public void keyPressed(KeyEvent e) {
	        	int k = e.getKeyCode();
	        	if (k == manager.getKeyMapping().getKey("Right"))
	        	{
	        		keys.right_key = true;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Left"))
	        	{
	        		keys.left_key = true;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Enter"))
	        	{
	        		keys.left_key = true;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Esc"))
	        	{
	        		keys.esc_key = true;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Jump"))
	        	{
	        		keys.jump_key = true;
	        	}
	        }

	        @Override
	        public void keyReleased(KeyEvent e) {
	        	int k = e.getKeyCode();
	        	if (k == manager.getKeyMapping().getKey("Right"))
	        	{
	        		keys.right_key = false;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Left"))
	        	{
	        		keys.left_key = false;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Enter"))
	        	{
	        		keys.left_key = false;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Esc"))
	        	{
	        		keys.esc_key = false;
	        	}
	        	else if(k == manager.getKeyMapping().getKey("Jump"))
	        	{
	        		keys.jump_key = false;
	        	}
	        }
	    });
		getFrame().addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		    	mouseClick = e.getPoint();
		    }
		});
		
		keys = new KeyState();
		
		manager = new LevelManager();
		manager.setScale(scale);
		
		menu = new Menu(manager, GameLevels.MENU);
		levelA = new TestLevel(manager, GameLevels.TEST);
		
		manager.setLevel(GameLevels.TEST.ordinal());
	}
	@Override
	public void update()
	{
		manager.update(keys, mouseDown);
		manager.updateUI(mouseClick);
		mouseClick = null;
	}
	@Override
	public void render(Graphics2D g2d)
	{
		super.render(g2d);
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		manager.render(g2d, debug);
		manager.renderUI(g2d, debug);
	}
	
	public Main()
	{
		super();
	}

	@Override
	public void onClose()
	{
		preferences.setScreenWidth(getFrame().getWidth());
		preferences.setScreenHeight(getFrame().getHeight());
		try
		{
		    FileOutputStream fileOutputStream
		      = new FileOutputStream("preferences.txt");
		    ObjectOutputStream objectOutputStream 
		      = new ObjectOutputStream(fileOutputStream);
		    objectOutputStream.writeObject(preferences);
		    objectOutputStream.flush();
		    objectOutputStream.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		super.onClose();
	}
	
	public static void main(String args[])
	{
		new Main();
	}
}