package buzzyfly;

import gameNet.GameNet_UserInterface;
import gameNet.GamePlayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import buzzyfly.objects.Camera;
import buzzyfly.objects.Fly;
import buzzyfly.objects.World;

public class FlyClient implements GameNet_UserInterface, KeyListener
{
	private FlyWindow window;
	private Camera camera;
	
	transient GamePlayer player;
	transient HashMap<String, Fly> flies;
	transient Fly ourFly;
	World world;
	transient boolean isServer = false;
	
	private boolean shuttingDown = false;
	
	private Drawable beginningMessage;
	private Drawable scoreMessage;
	
	public FlyClient()
	{
		beginningMessage = new Drawable() {
			public void draw(Graphics g, Camera c)
			{
				g.setColor(Color.BLUE);
				g.setFont(g.getFont().deriveFont(50f).deriveFont(Font.BOLD));
				g.drawString("See command line", 165, 190);
			}
		};
		scoreMessage = new Drawable() {
			public void draw(Graphics g, Camera c)
			{
				g.setColor(Color.YELLOW);
				g.setFont(g.getFont().deriveFont(24f).deriveFont(Font.BOLD));
				g.drawString(Integer.toString(ourFly.getPilarCount()), 75, 100);
			}
		};
		
		camera = new Camera(800, Main.SCREEN_HEIGHT);
		window = new FlyWindow(camera, beginningMessage);
		window.addKeyListener(this);
		window.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				if(player == null || !player.GameIsAlive())
					System.exit(0);
				print("Telling the server we're leaving");
				if(player != null && player.GameIsAlive())
				{
					shuttingDown = true;
					player.sendMessage(new Message("clientDisconnecting", player.getPlayerName()));
				}
			}
		});
		
		flies = new HashMap<String, Fly>();
	}
	
	private void print(Object... msg)
	{
		System.out.print("CLIENT: ");
		for(Object o : msg)
			System.out.print(o);
		System.out.println();
	}

	private boolean requestDeltaPrint = false;
	@Override
	public void keyPressed(KeyEvent event) 
	{
		if(event.getKeyChar() == 'y' && ourFly != null)
			print("Fly's y is ", ourFly.position.y);
		else if(event.getKeyChar() == 'x' && ourFly != null)
			print("Fly's x is ", ourFly.position.x);
		else if(event.getKeyChar() == 'd')
			requestDeltaPrint = true;
		else
		{
			if(world != null && ourFly != null)
			{
				if(!ourFly.dead)
					ourFly.buzz();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	@Override
	public void receivedMessage(Object ob) 
	{
		Message message = (Message)ob;
		message.execute(this);
	}

	@Override
	public void startUserInterface(GamePlayer player) 
	{
		this.player = player;
		print("Starting up!");
		player.sendMessage(new Message("createFly", player.getPlayerName()));
		player.sendMessage(new Message("requestWorld", player.getPlayerName()));
		player.sendMessage(new Message("requestFlies", player.getPlayerName()));
	}
	
	// Message functions:
	
	private double syncEvery = 0.1;
	private double syncTimer = 0.0;
	public void update(double deltaTime)
	{
		if(world != null)
		{
			if(ourFly != null && !ourFly.dead)
				camera.follow(ourFly.position);
			window.draw(world);
		}
		for(Fly fly : flies.values())
		{
			fly.update(deltaTime);
			window.draw(fly);
		}
		if(world != null)
		{
			if(ourFly != null && ourFly.hitBox != null)
			{
				window.draw(scoreMessage);
				World.CollisionInfo collision = world.checkCollision(ourFly.hitBox);
				if(collision.hit)
					ourFly.die();
				else
					ourFly.inPilar = collision.currentPilar!=-1;
				
				if(world.pillarCount() - ourFly.getPilarCount() < 5)
				{
					world.generatePilars(10);
				}
			}
		}
		syncTimer += deltaTime;
		if(syncTimer >= syncEvery)
		{
			syncTimer = 0;
			if(ourFly != null)
				player.sendMessage(new Message("syncFly", player.getPlayerName(), ourFly));
		}

		if(requestDeltaPrint)
		{
			print("Delta: ", deltaTime);
			requestDeltaPrint = false;
		}
		
		window.repaint();
	}
	
	public void serverError(String source, String message)
	{
		System.err.println("The server encountered an issue with the client: " + 
							source + "\n" + message);
	}

	public void receiveWorld(String to, World world)
	{
		if(to.equals(player.getPlayerName()))
		{
			print("Received world from server");
			this.world = world;
			ourFly.init();
			this.world.generatePilars(10);
			window.suspended = false;
		}
	}
	
	public void receiveFly(String owner, Fly fly)
	{
		if(!flies.containsKey(owner))
		{
			flies.put(owner, fly);
			fly.owned = owner.equals(player.getPlayerName());
			if(fly.owned)
			{
				print("Received our fly from server");
				ourFly = fly;
			}
			else
			{
				print("Added ", owner, "'s fly");
			}
		}
	}

	public void giveFly(String to)
	{
		if(to.equals(player.getPlayerName()))
			print("Other clients are receiving our fly");
		else
			print("Server wants us to give our fly to ", to);
		if(ourFly != null && !to.equals(player.getPlayerName()))
		{
			print("Sending fly info to ", to);
			player.sendMessage(new Message("giveFly", to, ourFly));
		}
	}

	public void syncFly(String owner, Fly fly)
	{
		if(player != null && !owner.equals(player.getPlayerName()))
			if(flies.containsKey(owner))
				flies.get(owner).sync(fly);
	}
	
	public void removeFly(String owner)
	{
		if(flies.containsKey(owner))
		{
			print(owner, " has left!");
			flies.remove(owner);
			if(shuttingDown)
			{
				player.doneWithGame();
				System.exit(0);
			}
		}
	}
}
