package buzzyfly;

import gameNet.GameControl;
import gameNet.GameNet_CoreGame;

public class FlyCore extends GameNet_CoreGame implements Runnable
{
	private GameControl gameControl;
	private FlyServer server;
	
	public FlyCore()
	{
		server = new FlyServer();
	}
	
	public void startGame(GameControl g)
	{
		gameControl = g;
		Thread t = new Thread(this);
        t.start();
	}
	@Override
	public void run() 
	{
		long begin = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		long deltaTimeLong = 0;
		while(true)
		{
			try
			{
				begin = System.currentTimeMillis();
                gameControl.putMsgs(new Message("update", deltaTimeLong/1000.0));
                Thread.sleep(10);
                end = System.currentTimeMillis();
                deltaTimeLong = end - begin;
            }
            catch (InterruptedException e){}
		}
	}
	
	@Override
	public Object process(Object ob) 
	{ return server.process(ob); }

	
}
