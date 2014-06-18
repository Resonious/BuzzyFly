package physics;

import java.io.Serializable;

public class Vector2 implements Serializable 
{
	final static long serialVersionUID = 1L;
	
	public double x, y;
	public Vector2()
	{
		x = 0;
		y = 0;
	}
	public Vector2(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public Vector2(double xy)
	{
		x = xy;
		y = xy;
	}

	public String toString()
	{
		return "" + x + ", " + y;
	}
	
	public double magnitude()
	{
		return Math.sqrt(x*x+y*y);
	}
	
	public Vector2 setMagnitude(double to)
	{
		return this.normalized().times(to);
	}

	public Vector2 normalized()
	{
		double mag = magnitude();
		return new Vector2(x/mag, y/mag);
	}

	public Vector2 plus(Vector2 other)
	{
		return new Vector2(x+other.x, y+other.y);
	}

	public Vector2 times(double scalar)
	{
		return new Vector2(x*scalar, y*scalar);
	}
	
	public Vector2 distanceFrom(Vector2 other)
	{
		return new Vector2(other.x - x, other.y - y);
	}
	
	public Vector2 interpolate(Vector2 other)
	{
		return interpolate(other, 10);
	}
	public Vector2 interpolate(Vector2 other, double smoothAmount)
	{
		Vector2 dist = distanceFrom(other);
		dist = dist.setMagnitude(dist.magnitude()/smoothAmount);
		return this.plus(dist);
	}
}
