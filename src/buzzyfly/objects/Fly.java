package buzzyfly.objects;

import java.awt.Font;
import java.awt.Graphics;
import java.io.Serializable;

import physics.Rect;
import physics.Vector2;
import buzzyfly.Drawable;
import buzzyfly.Main;

public class Fly implements Serializable, Drawable
{
	static final long serialVersionUID = 1L;

	public Vector2 position;
	public Vector2 velocity;
	public transient Vector2 targetPosition;
	public transient double maxGravity;
	public transient double weight;
	public transient Rect hitBox;

	public transient double buzzStrength;

	public String owner;

	public transient boolean owned = false;
	public boolean paused = true;
	public boolean dead = false;

	public transient final int buzzingAnimation = 0;
	public transient final int dyingAnimation = 1;
	public transient Animation[] animations;
	public int currentAnimation;
	
	public transient boolean inPilar = false, wasInPilar = false;
	private int pilarCount;
	private int highestPilarCount = 0;
	private transient Vector2 screenPos;
	
	public Fly()
	{
		init();
	}
	public Fly(String name)
	{
		owner = name;
	}

	public void init()
	{
		paused = true;
		position = new Vector2(50, 150);
		velocity = new Vector2(100+(4 * highestPilarCount), 0);
		maxGravity = 500;
		weight = 1000;
		buzzStrength = 400;
		pilarCount = 0;

		if(animations == null)
			initAnimations();
		currentAnimation = buzzingAnimation;
	}
	public void initAnimations()
	{
		animations = new Animation[2];
		animations[buzzingAnimation] = new Animation("assets/fly/fly1.png", "assets/fly/fly2.png");
		animations[dyingAnimation] = new Animation("assets/fly/fly_die{n}.png");
	}

	private void syncHitBox()
	{
		if(hitBox == null)
			hitBox = new Rect(position, 24, 20);
		else
		{
			hitBox.x = position.x+4;
			hitBox.y = position.y+6;
		}
	}
	
	public void update(double deltaTime)
	{
		if(!paused)
		{
			if(owned)
			{
				if(!dead && wasInPilar && !inPilar)
					if(++pilarCount > highestPilarCount)
					{
						highestPilarCount = pilarCount;
						velocity.x += 4;
					}
				wasInPilar = inPilar;
				
				if(screenPos.x > 800)
					position.x = 0;
				if(position.y > Main.SCREEN_HEIGHT*1.7f)
					if(dead)
						revive();
					else
						position.y = -100;
				
				if(velocity.y < maxGravity && weight != 0)
				{
					velocity.y += weight * deltaTime * Math.pow(2, Math.abs(velocity.y)/1200);
					if(velocity.y > maxGravity)
						velocity.y = maxGravity;
				}
				syncHitBox();
			}
			else
			{
				if(position.distanceFrom(targetPosition).magnitude() > 300)
					position = targetPosition;
				else
					position = position.interpolate(targetPosition, 3);
			}
			position = position.plus(velocity.times(deltaTime));
			if(position.y < 0 && !dead)
				position.y = 0;
			animations[currentAnimation].nextFrame();
		}
		else if(!owned)
		{
			position = targetPosition;
		}
	}
	
	public int getPilarCount()
	{ return pilarCount; }

	public void sync(Fly otherFly)
	{
		if(animations == null)
			initAnimations();
		this.velocity          = otherFly.velocity;
		this.targetPosition    = otherFly.position;
		this.paused            = otherFly.paused;
		this.dead              = otherFly.dead;
		this.currentAnimation  = otherFly.currentAnimation;
		this.pilarCount        = otherFly.pilarCount;
		this.highestPilarCount = otherFly.highestPilarCount;
	}
	
	public void die()
	{
		dead = true;
		velocity.y = -500;
		velocity.x = -75;
		currentAnimation = dyingAnimation;
	}
	
	public void revive()
	{
		dead = false;
		init();
	}

	public void buzz()
	{
		if(paused)
			paused = false;
		else if(!dead)
			velocity.y = -buzzStrength;
	}

	//@Override
	public void draw(Graphics g, Camera c) 
	{
		if(position == null)
			return;

		screenPos = c.offset(position);
		
		int posX = (int)Math.round(screenPos.x);
		int posY = (int)Math.round(screenPos.y);
		animations[currentAnimation].draw(g, posX, posY);
		if(owner != null && !owner.isEmpty())
		{
			g.setFont(g.getFont().deriveFont(14f).deriveFont(Font.PLAIN));
			g.drawString(owner+" *"+highestPilarCount+"*", posX, posY+37);
		}
	}
}
