package buzzyfly.objects;

import physics.Rect;
import physics.Vector2;

public class Camera 
{
	double position;
	int width, height;
	
	public Camera(int width, int height)
	{
		this.width = width;
		this.height = height;
		position = 0;
	}
	
	public int getWidth()
	{ return width; }
	public int getHeight()
	{ return height; }

	public Rect asRect()
	{ return new Rect(position, 0, width, height); }

	public void follow(Vector2 target)
	{
		position = target.x - width / 2f;
		if(position < 0)
			position = 0;
	}

	public Vector2 offset(Vector2 vec)
	{
		return new Vector2(vec.x-position, vec.y);
	}
	public Rect offset(Rect rect)
	{
		return new Rect(rect.x-position, rect.y,rect.width,rect.height);
	}
}
