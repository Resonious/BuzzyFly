package buzzyfly.objects;

import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import physics.Rect;
import buzzyfly.Drawable;

public class World implements Serializable, Drawable
{
	public class CollisionInfo
	{
		public boolean hit;
		public int currentPilar;
		
		private CollisionInfo()
		{}
		private CollisionInfo(boolean hit, int currentPilar)
		{
			this.hit = hit;
			this.currentPilar = currentPilar;
		}
	}
	
	private static final long serialVersionUID = 1L;

	transient ArrayList<Pilar> pilars;
	transient Random rand;
	transient int numPillars = 0;
	private long seed = 1000L;
	public int height;
	
	public World(int height, long seed)
	{
		pilars = new ArrayList<Pilar>();
		this.height = height;
		this.seed = seed;
	}
	
	public void update()
	{
		
	}
	
	public CollisionInfo checkCollision(Rect entity)
	{
		CollisionInfo info = new CollisionInfo(false,-1);
		for(int i = 0, count = pilars.size(); i < count; i++)
		{
			if(pilars.get(i).collidesWith(entity))
			{
				info.currentPilar = i;
				info.hit = true;
				return info;
			}
			else if(pilars.get(i).checkBetween(entity))
			{
				info.currentPilar = i;
				return info;
			}
		}
		return info;
	}
	
	public int pillarCount()
	{ return numPillars; }

	public void generatePilars(int count)
	{
		if(rand == null)
			rand = new Random(seed);
		for(int i = 0; i < count; i++)
			generatePilar(165, 140+rand.nextInt(40), 120);
	}

	public void generatePilar(int distance, int gap, int width)
	{
		if(pilars == null)
			pilars = new ArrayList<Pilar>();
		if(rand == null)
			rand = new Random(seed);

		int lastPilarX;
		if(pilars.size() == 0)
			lastPilarX = 300;
		else
		{
			Pilar lastPilar = pilars.get(pilars.size()-1);
			lastPilarX = lastPilar.getX() + lastPilar.getWidth();
		}

		int newPilarX = lastPilarX + distance;

		int maxUpperHeight = height - gap - 20;
		pilars.add( new Pilar(newPilarX, rand.nextInt(maxUpperHeight)+10, gap, width, height) );
		numPillars++;
	}

	public void draw(Graphics g, Camera c)
	{
		for(Pilar p : pilars)
			if(p.collidesWith(c.asRect()))
				p.draw(g, c);
	}
}
