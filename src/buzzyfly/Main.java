package buzzyfly;

import gameNet.*;

public class Main extends GameCreator 
{
	public static final int SCREEN_HEIGHT = 480;
	
	@Override
	public GameNet_CoreGame createGame() 
	{
		return new FlyCore();
	}
	
	public static void main(String[] args)
	{
		Main _main = new Main();
		GameNet_UserInterface client = new FlyClient();

		_main.enterGame(client);
	}
}
