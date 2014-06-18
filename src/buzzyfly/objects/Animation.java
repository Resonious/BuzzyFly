package buzzyfly.objects;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Animation implements Serializable
{
	private static final long serialVersionUID = 1L;

	public transient Image[] images;
	private String[] fileNames;
	transient int currentIndex;
	
	public Animation()
	{
		images = new Image[0];
		fileNames = new String[0];
	}
	public Animation(String... fileNames)
	{
		this.fileNames = fileNames;
		loadImages();
	}
	public Animation(String fileNameMacro)
	{
		ArrayList<String> files = new ArrayList<String>();
		int count = 0;
		while(true)
		{
			String fileName = fileNameMacro.replace("{n}", Integer.toString(++count));
			File f = new File(fileName);
			if(f.exists())
				files.add(fileName);
			else
				break;
		}
		fileNames = files.toArray(new String[files.size()]);
		loadImages();
	}
	
	private void loadImages()
	{
		images = new Image[fileNames.length];
		for(int i = 0; i < fileNames.length; i++)
			images[i] = loadImage(fileNames[i]);
		currentIndex = 0;
	}

	private Image loadImage(String fileName)
	{
		return new ImageIcon(fileName).getImage();
	}
	
	public void setTo(Animation other)
	{
		if(fileNames.length != other.fileNames.length)
			this.fileNames = other.fileNames;
		loadImages();
	}

	public void draw(Graphics g, int x, int y)
	{
		if(images == null || fileNames.length != images.length)
			loadImages();
		if(images.length > 0 && images[currentIndex] != null)
		{
			g.drawImage(images[currentIndex], x, y, null);
		}
	}

	public void nextFrame()
	{
		if(images != null)
		{
			currentIndex++;
			if(currentIndex >= images.length)
				currentIndex = 0;
		}
	}
}
