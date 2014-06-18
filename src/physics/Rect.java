package physics;

import java.awt.Graphics;
import java.io.Serializable;

public class Rect implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public double x, y, width, height;
	
	public Rect()
	{
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}
	public Rect(double width, double height)
	{
		x = 0;
		y = 0;
		this.width = width;
		this.height = height;
	}
	public Rect(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public Rect(Vector2 position, double width, double height)
	{
		this.x = position.x;
		this.y = position.y;
		this.width = width;
		this.height = height;
	}
	
	public void draw(Graphics g, boolean fill)
	{
		if(fill)
			g.fill3DRect((int)Math.round(x),
					  (int)Math.round(y),
			          (int)Math.round(width),
			          (int)Math.round(height), true);
		else
			g.drawRect((int)Math.round(x),
					  (int)Math.round(y),
			          (int)Math.round(width),
			          (int)Math.round(height));
	}
	
	public Vector2 point1()
	{
		return new Vector2(x, y);
	}
	public Vector2 point2()
	{
		return new Vector2(x+width, y+height);
	}

	public boolean overlaps(Rect other)
	{
		if(other == null)
			return false;
		Vector2 p1 = point1();
		Vector2 p2 = point2();
		Vector2 p3 = other.point1();
		Vector2 p4 = other.point2();
		return !( p2.y < p3.y || p1.y > p4.y || p2.x < p3.x || p1.x > p4.x );
	}
}
