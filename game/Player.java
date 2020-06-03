package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.GameObject;
import framework.Image;
import framework.KeyMapping;
import framework.KeyState;
import framework.Level;
import framework.Wall;
import framework.WallTypes;
import framework.animation.Frame;
import framework.animation.Joint;
import framework.animation.SpriteSheet;
import framework.PhysicsType;
import framework.imageFilters.ImageFilter;
import framework.imageFilters.LightenFrom;
import framework.imageFilters.ShadeDir;

public class Player extends GameObject{
	private PlayerStates state, targetState;
	private boolean facingRight = true, idle = true;
	private Map<PlayerStates, SpriteSheet> animations;
	private Level level;
	
	//private Shape myShape;
	private Image img, img2, img3, eye_img;
	private Joint body, head, eye, r_shoulder, l_shoulder, r_elbow, l_elbow, r_upper_arm, l_upper_arm,
		r_lower_arm, l_lower_arm, r_hand, l_hand, r_hip, l_hip, r_upper_leg, l_upper_leg, r_knee, l_knee,
		r_lower_leg, l_lower_leg, r_foot, l_foot;
	private List<Joint> joints;
	private Frame one, two, three, four, five;
	private SpriteSheet sheet;
	private Rectangle2D floorCheck, rCheck, lCheck; 
	private List<Integer> jumpMotion; // List of y velocities for jumping
	
	private double x, y;
	private int SPEED = 3, CLIMBSPEED = 2, GRAVITY = 5;
	private boolean onGround, onRamp, canJump = false, jumping;
	private int jumpCount;
	
	public Player(Level level)
	{
		this.level = level;
		double s = level.getManager().getScale();
		SPEED *= s;
		CLIMBSPEED *= s;
		GRAVITY *= s;
		animations = new HashMap<PlayerStates, SpriteSheet>();
		this.x = 250;
		this.y = 300;
		
		jumpMotion = new ArrayList<Integer>();
		jumpMotion.add((int)(8 * s));
		jumpMotion.add((int)(9 * s));
		jumpMotion.add((int)(6 * s));
		jumpMotion.add((int)(4 * s));
		jumpMotion.add((int)(2 * s));
		jumpMotion.add((int)(2 * s));
		jumpMotion.add((int)(1 * s));
		jumpMotion.add(0);
		jumpMotion.add((int)(-1 * s));
		jumpMotion.add((int)(-2 * s));
		jumpMotion.add((int)(-3 * s));
		
		double w = 10 * s;
		double h = 8 * s;
		double xOffset = 0 * s;
		double yOffset = 22 * s;
		floorCheck = new Rectangle2D.Double(x - (w / 2), y + yOffset - (h / 2), w, h);
		
		xOffset = 8 * s;
		yOffset = 12 * s;
		w = 5 * s;
		h = 10 * s;
		rCheck = new Rectangle2D.Double(x + xOffset - (w / 2), y + yOffset - (h / 2), w, h);
		lCheck = new Rectangle2D.Double(x - xOffset - (w / 2), y + yOffset - (h / 2), w, h);
	}
	
	@Override
	public void rescale()
	{
		
	}
	@Override
	public PhysicsType getPhysicsType()
	{
		return PhysicsType.NONE;
	}
	public double getX()
	{ return x; }
	public double getY()
	{ return y; }
	@Override
	public void update(double dt, KeyState keys) {
		int xVel = 0;
		int yVel = 0;
//		animations.get(state).update(dt, keys);
		idle = false;
		if (keys.right_key)
		{
			targetState = PlayerStates.WALK_R;
			xVel = SPEED;
		}
		if (keys.left_key)
		{
			targetState = PlayerStates.WALK_L;
			xVel = -SPEED;
		}
		if (keys.jump_key)
		{
			if (canJump)
			{
				jumping = true;
				jumpCount = 0;
			}
		}
		
		if (!keys.right_key
				&& !keys.left_key)
		{
			idle = true;
		}
		
		if (idle)
		{
			if (facingRight)
			{
				state = PlayerStates.IDLE_R;
			}
			else
			{
				state = PlayerStates.IDLE_L;
			}
		}
		
		if (targetState != null && targetState != state)
		{
			state = targetState;
			targetState = null;
//			animations.get(state).reset();
		}
		switch(state)
		{
		case IDLE_R:
			break;
		case IDLE_L:
			break;
		case WALK_R:
			facingRight = true;
			break;
		case WALK_L:
			facingRight = false;
		default:
			break;
		}
		
		onGround = false;
		onRamp = false;
		canJump = false;
		for (int i = 0; i < level.getObjects().size(); i++)
		{
			GameObject obj = level.getObjects().get(i);
		
			if (obj.getClass() == Wall.class)
			{
				Wall wall = (Wall)obj;
				if (floorCheck.intersectsLine(wall.getLine()))
				{
					if (wall.getType() == WallTypes.FLOOR)
					{
						onGround = true;
						canJump = true;
					}
					else if (wall.getType() == WallTypes.RAMP)
					{
						canJump = true;
					}
				}
				if (rCheck.intersectsLine(wall.getLine()))
				{
					if (wall.getType() == WallTypes.RAMP)
					{
						onRamp = true;
						if (xVel > 0)
						{
							xVel = CLIMBSPEED;
							yVel = -CLIMBSPEED;
							
						}
					}
					else if (wall.getType() == WallTypes.WALL)
					{
						if (xVel > 0)
						{
							xVel = 0;
						}
					}
				}
				if (lCheck.intersectsLine(wall.getLine()))
				{
					if (wall.getType() == WallTypes.RAMP)
					{
						onRamp = true;
						if (xVel < 0)
						{
							xVel = -CLIMBSPEED;
							yVel = -CLIMBSPEED;
							
						}
					}
					else if (wall.getType() == WallTypes.WALL)
					{
						if (xVel < 0)
						{
							xVel = 0;
						}
					}
				}
			}
		}
		if (onGround && !onRamp)
		{
			yVel = 0;
		}
		else if (!onGround && !onRamp && !jumping)
		{
			yVel = GRAVITY;
		}
		if (jumping)
		{
			yVel = -jumpMotion.get(jumpCount);
			jumpCount++;
			if (jumpCount == jumpMotion.size())
			{
				jumpCount = 0;
				jumping = false;
			}
		}
		this.y += yVel;
		this.x += xVel;
		translate(xVel, yVel);
	}

	@Override
	public void render(Graphics2D g2d, boolean debug) {
//		animations.get(state).render(g2d, debug);
		if (debug)
		{
			g2d.setColor(new Color(155, 155, 155, 155));
			g2d.fillRect((int)floorCheck.getX(), (int)floorCheck.getY(), (int)floorCheck.getWidth(), (int)floorCheck.getHeight());
			g2d.fillRect((int)rCheck.getX(), (int)rCheck.getY(), (int)rCheck.getWidth(), (int)rCheck.getHeight());
			g2d.fillRect((int)lCheck.getX(), (int)lCheck.getY(), (int)lCheck.getWidth(), (int)lCheck.getHeight());
		}
	}
	public void translate(double xVel, double yVel)
	{
		this.x += xVel;
		this.y += yVel;
//		for (SpriteSheet sheet : animations.values()) {
//			sheet.setTranslated(false);
//		}
//		for (SpriteSheet sheet : animations.values()) {
//			if (!sheet.getTranslated())
//			{
//				sheet.translate((int)xVel, (int)yVel);
//			}
//		}
		floorCheck = new Rectangle2D.Double(floorCheck.getX() + xVel, floorCheck.getY() + yVel, floorCheck.getWidth(), floorCheck.getHeight());
		rCheck = new Rectangle2D.Double(rCheck.getX() + xVel, rCheck.getY() + yVel, rCheck.getWidth(), rCheck.getHeight());
		lCheck = new Rectangle2D.Double(lCheck.getX() + xVel, lCheck.getY() + yVel, lCheck.getWidth(), lCheck.getHeight());
	}
}
