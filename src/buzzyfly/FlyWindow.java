package buzzyfly;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.JFrame;

import buzzyfly.objects.Camera;

public class FlyWindow extends JFrame
{
	private static final long serialVersionUID = -4668597884708323487L;
	
	private Image offScreenImage = null;
	private Dimension previousSize = null;
	private List<Drawable> drawables;
	private Camera camera;
	
	public boolean suspended = true;
	
	private Drawable defaultMessage;
	
	public FlyWindow(Camera camera, Drawable messageOnEmpty)
	{
		super("Buzzy Fly!");
		drawables = Collections.synchronizedList(new ArrayList<Drawable>());
		defaultMessage = messageOnEmpty;
		
		this.camera = camera;
		setSize(camera.getWidth(), camera.getHeight());
		setVisible(true);
	}
	
	public void draw(Drawable drawable)
	{
		drawables.add(drawable);
	}
	
	int test_x = 0;
	boolean painting = false;
	public void paint(Graphics screen)
	{
		if(painting)
			return;
		Dimension d = getSize();
		
		if(!suspended && drawables.isEmpty())
		{
			screen.drawImage(offScreenImage, 0,0, this);
			return;
		}
		if (offScreenImage==null || !d.equals(previousSize))
		{
			offScreenImage = createImage(d.width, d.height);
			previousSize = d;
		}
		Graphics g = offScreenImage.getGraphics();
		
		g.setColor(Color.CYAN);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.GRAY);
		
		//draw shit
		if(suspended)
			defaultMessage.draw(g, camera);
		try
		{
			for(Drawable drawable : drawables)
				drawable.draw(g, camera);
		}
		catch(ConcurrentModificationException e)
		{ }
			
		drawables.clear();
		
		//g.fillRect(test_x++, 200, 100, 100);
		
		screen.drawImage(offScreenImage, 0,0, this);
		painting = false;
	}
}
