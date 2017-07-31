package com.demo.ex1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Size;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class UserController
{
	@Autowired
	InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	Set<String> restricted = new HashSet<>();
	
	/**
	 * The restricted list constructor
	 */
	@PostConstruct
	public void init()
	{
		restricted.add("cannabis");
		restricted.add("abuse");
		restricted.add("crack");
		restricted.add("damn");
	}
	
	@RequestMapping("/")
	@ResponseBody
    public String page() {		
		return "Go to <br>/check/{username}";
    }
	
	@RequestMapping("/check/{username}")
    public User userExists(@PathVariable("username") @Size(min = 6, max = 12) String username ) {
		boolean exist = inMemoryUserDetailsManager.userExists(username);
		
		User user = new User();
		
		boolean hasRestricted = restricted.contains(username.toLowerCase()); // check if username exists
		
		if(exist || hasRestricted)
		{
			if(hasRestricted)
				username = generateNewUsername(); //generate new username if its restricted
			user.setOk(false);
			user.setAvailableUsername(generateUsernames(username));
		}
		else
		{
			user.setOk(true);
		}
		
		return user;
    }
	
	/**
	 * Generate 14 random usernames
	 * @param username
	 * @return
	 */
	public List<String> generateUsernames(String username)
	{
		List<String> usernameList = new ArrayList<>();
		int i = 1; 
		
		while(usernameList.size() < 14)
		{
			if(!inMemoryUserDetailsManager.userExists(username + i))
				usernameList.add(username + i);
			i++;
		}
		return usernameList;
	}
	
	/**
	 * Generate a random username
	 * @return
	 */
	public String generateNewUsername() {
		RandomStringGenerator generator = new RandomStringGenerator.Builder()
			     .withinRange('a', 'z').build();
		while(true)
		{
			String str = generator.generate(6);
			if(!inMemoryUserDetailsManager.userExists(str))
				return str;
		}		
	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String handleResourceNotFoundException(ConstraintViolationException e) {
	    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
	    StringBuilder strBuilder = new StringBuilder("Username ");
	    for (ConstraintViolation<?> violation : violations ) {
	        strBuilder.append(violation.getMessage() + "\n");
	    }
	    return strBuilder.toString();
	}
}
