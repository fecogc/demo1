package com.demo.ex1;

import java.util.List;

public class User
{
	boolean ok;
	List<String> availableUsername;
	
	public boolean isOk()
	{
		return ok;
	}
	public void setOk(boolean ok)
	{
		this.ok = ok;
	}
	public List<String> getAvailableUsername()
	{
		return availableUsername;
	}
	public void setAvailableUsername(List<String> availableUsername)
	{
		this.availableUsername = availableUsername;
	}
	
	
}
