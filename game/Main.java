package game;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import framework.*;


public class Main extends Game
{
	private static int WIDTH = (int)(768 * 1.2);
	private static int HEIGHT = (int)(432 * 1.2);
	
	private UserPrefs preferences; 
	
	private boolean debug = false;
	private boolean mouseDown = false;
	
	private KeyState keys;
	
	private TestLevel levelA;
	private Menu menu;
	
	private Point mouseClick;
	
	
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
		
		getFrame().setSize(new Dimension(preferences.getScreenWidth(), preferences.getScreenHeight()));
		getCanvas().addKeyListener(new KeyListener() {
	        @Override
	        public void keyTyped(KeyEvent e) {
	        }

	        @Override
	        public void keyPressed(KeyEvent e) {
	        	int k = e.getKeyCode();
	        	if (k == getManager().getKeyMapping().getKey("Right"))
	        	{
	        		keys.right_key = true;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Left"))
	        	{
	        		keys.left_key = true;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Enter"))
	        	{
	        		keys.left_key = true;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Esc"))
	        	{
	        		keys.esc_key = true;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Jump"))
	        	{
	        		keys.jump_key = true;
	        	}
	        }

	        @Override
	        public void keyReleased(KeyEvent e) {
	        	int k = e.getKeyCode();
	        	if (k == getManager().getKeyMapping().getKey("Right"))
	        	{
	        		keys.right_key = false;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Left"))
	        	{
	        		keys.left_key = false;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Enter"))
	        	{
	        		keys.left_key = false;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Esc"))
	        	{
	        		keys.esc_key = false;
	        	}
	        	else if(k == getManager().getKeyMapping().getKey("Jump"))
	        	{
	        		keys.jump_key = false;
	        	}
	        }
	    });
		getCanvas().addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		    	mouseClick = e.getPoint();
		    }
		});
		
		keys = new KeyState();
		
		
		menu = new Menu(getManager(), GameLevels.MENU);
		levelA = new TestLevel(getManager(), GameLevels.TEST);
		
		getManager().setLevel(GameLevels.TEST.ordinal());
	}
	@Override
	public void update(double dt)
	{
		getManager().update(dt, keys, mouseDown);
		getManager().updateUI(mouseClick);
		mouseClick = null;
	}
	@Override
	public void render(Graphics2D g2d)
	{
		super.render(g2d);
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		getManager().render(g2d, debug);
		getManager().renderUI(g2d, debug);
	}
	
	public Main()
	{
		super(false, WIDTH, HEIGHT);
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
	
	@Override
	public void onResize(DrawPanel draw)
	{
		super.onResize(draw);
	}
	
	public static void main(String args[])
	{
		new Main();
	}
}