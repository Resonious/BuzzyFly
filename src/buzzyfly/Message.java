package buzzyfly;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String function;
	private Object[] params;

	public Message(String function, Object... params)
	{
		this.function = function;
		this.params = params;
	}
	
	public String getFunctionName()
	{
		return function;
	}
	
	private void print(Object... msg)
	{
		System.out.print("MESSAGE: ");
		for(Object o : msg)
			System.out.print(o);
		System.out.println();
	}
	
	public Object execute(Object receiver)
	{
		boolean error = false;
		Method[] methods = receiver.getClass().getMethods();
		for(Method method : methods)
		{
			if(method.getName().equals(function))
			{
				try {
					return method.invoke(receiver, params);
				}
				catch(IllegalArgumentException e)
				{
					error = true;
					System.out.println("Got message with bad arguments");
				}
				catch (IllegalAccessException | InvocationTargetException e) 
				{
					error = true;
					print("Function: ", function, "; Error: ", e.getClass());
					e.printStackTrace();
				}
			}
		}
		if(!error)
			print("Failed to find ", function, " for ", receiver.getClass().getName());
		return null;
	}
}
