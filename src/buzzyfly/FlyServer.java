package buzzyfly;

import java.util.Calendar;

import buzzyfly.objects.Fly;
import buzzyfly.objects.World;

public class FlyServer 
{
	public World world;

	FlyServer()
	{
		world = new World(Main.SCREEN_HEIGHT, Calendar.getInstance().getTimeInMillis());
	}
	
	private void print(Object... msg)
	{
		System.out.print("SERVER: ");
		for(Object o : msg)
			System.out.print(o);
		System.out.println();
	}
	
	public Object process(Object ob)
	{
		Message message = (Message)ob;
		//print("Received ", message.getFunctionName(), " message from a client");
		return message.execute(this);
	}
	
	Message reportError(String source, String message)
	{
		return new Message("serverError", source, message);
	}
	
	// Message functions:
	
	public Message createFly(String name)
	{
		print("Processing fly creation request for ", name);
		
		Fly newFly = new Fly(name);
		return new Message("receiveFly", name, newFly);
	}

	public Message requestWorld(String requester)
	{
		return new Message("receiveWorld", requester, world);
	}

	public Message requestFlies(String requester)
	{
		print(requester, " has requested flies");
		return new Message("giveFly", requester);
	}
	public Message giveFly(String to, Fly fly)
	{
		print("Giving fly to ", to);
		return new Message("receiveFly", fly.owner, fly);
	}

	public Message syncFly(String owner, Fly fly)
	{
		return new Message("syncFly", owner, fly);
	}
	
	public Message clientDisconnecting(String name)
	{
		return new Message("removeFly", name);
	}
	
}
