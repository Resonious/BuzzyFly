package buzzyfly.objects;

import java.awt.Color;
import java.awt.Graphics;

import physics.Rect;

public class Pilar
{
	private Rect upperHitBox;
	private Rect lowerHitBox;
	private Rect middleHitBox;

	public Pilar(int x, int upperHeight, int gap, int width, int worldHeight)
	{
		upperHitBox =  new Rect(x, 0,                width, upperHeight);
		lowerHitBox =  new Rect(x, upperHeight+gap,  width, worldHeight-upperHeight+gap);
		middleHitBox = new Rect(x, upperHeight,      width, gap);
	}

	public boolean collidesWith(Rect box)
	{
		return upperHitBox.overlaps(box) || lowerHitBox.overlaps(box);
	}
	public boolean checkBetween(Rect box)
	{
		return middleHitBox.overlaps(box);
	}

	public int getX()
	{ return (int) upperHitBox.x; }
	public int getWidth()
	{ return (int) upperHitBox.width; }

	public void draw(Graphics g)
	{
		draw(g, null);
	}
	public void draw(Graphics g, Camera c)
	{
		Rect up, lo;
		up = c.offset(upperHitBox);
		lo = c.offset(lowerHitBox);
		
		g.setColor(Color.GRAY);
		up.draw(g, true);
		lo.draw(g, true);
		// g.setColor(Color.BLUE);
		// c.offset(middleHitBox).draw(g, false);
	}
}
