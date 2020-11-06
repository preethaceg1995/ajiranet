package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*
 * ProcessController controls the flow from client to server
 */
@Controller
@RequestMapping("/process")
public class ProcessController {
	
	Network network = new Network();
	
	/*
	 * Handles all the POST methods from the client
	 */
   @RequestMapping(method = RequestMethod.POST)
   public ModelAndView process(@RequestBody String request, 
		   @RequestHeader(value="Command") String headCommand) {
	   
	   Map<String, String> resMap = new HashMap<>();
	   ModelAndView modelAndView = new ModelAndView();
	   modelAndView.setViewName("display");

       //check for proper valid commands
	   if(headCommand.isEmpty() || (!headCommand.contains("FETCH") && request.isEmpty())) {
		   modelAndView.addObject("code", "HTTP Response Code : 400");
		   modelAndView.addObject("message", "msg : Invalid Command");
		   return modelAndView;
	   }
	   
	   request = request.replaceAll("[{},\"]", "");
	   String[] commands = headCommand.split("/");
	   String commandType = commands[0].trim();
	   
	   //CREATE command
	   if(commandType.equals("CREATE")) {
		   String command = commands[1].trim();
		   
		   //CREATE -> connection command
		   if(command.equals("connections")) {
			   
			   //checks if source and targets exists
			   if(!request.contains("source") || !request.contains("targets")) {
				   modelAndView.addObject("code", "HTTP Response Code : 400");
				   modelAndView.addObject("message", "msg : Invalid Command");
				   return modelAndView;
			   }
			   
			   //parses the request and extracts the source and targets
			   String target = request.substring(request.indexOf("[")+1, request.indexOf("]"));
			   request = request.substring(0, request.indexOf("targets")).trim();
			   String source = request.split(":")[1].trim();
			  
			   String[] targets = target.split(" ");
			   List<String> targList = new LinkedList<>();
			   for(String s : targets) {
				   targList.add(s);
			   }
			   
			 //check if source and target are same
				if(targList.contains(source)) {
					resMap.put("code", "400");
					resMap.put("msg", "Cannot connect to itself");
				} else
					//create connection
					resMap = network.createConnections(source, targList);
			   
		   }
		   
		   //CREATE -> devices command
		   if(command.equals("devices")) {
			  		   
			   //checks for type and name of an device
			   if(!request.contains("type") || !request.contains("name")) {
				   modelAndView.addObject("code", "HTTP Response Code : 400");
				   modelAndView.addObject("message", "msg : Invalid Command");
				   return modelAndView;
			   }
			   
			   //parses the request and extracts type and name
			   String type = request.substring(0, request.indexOf("name")).trim().split(":")[1].trim();
			   String name = request.substring(request.indexOf("name")).trim().split(":")[1].trim();
			   //creates device
			   resMap = network.createDevice(name, type);
		   }
	   } 
	   
	   //MODIFY command
	   else if(commandType.equals("MODIFY")) {
		   
		   //parses the request and gets the value of strength
		   String device = commands[2].trim();
		   String value = request.split(":")[1].trim();
		   //modifies the strength
		   resMap = network.modifyDeviceStrength(device, value);
	   } 
	   
	   //FETCH command
	   else if(commandType.equals("FETCH")) {
		  
		   String command = commands[1].trim();
		   
		   //fetches all the devices
		   if(command.equals("devices")) {
			   resMap = network.fetchDevices();
		   }
		   
		   //checks for valid syntax and checks for the route from source to destination
		   if(command.contains("info-route") && command.contains("from") && command.contains("to")) {
			   
			   //gets from and to devices
			   int fIdx = commands[1].indexOf("=");
			   int tIdx = commands[1].lastIndexOf("=");
			   
			   //checks if from and to exists
			   if(fIdx!=-1 && tIdx!=-1 && fIdx!=tIdx) {
				   int andIdx = commands[1].indexOf("&");
				   String from = commands[1].substring(fIdx+1, andIdx).trim();
				   String to = commands[1].substring(tIdx+1).trim();
				   //fetches the route
				   resMap = network.fetchRoute(from, to);
			   }
			   else {
				   resMap.put("code", "400");
				   resMap.put("msg", "Invalid Syntax");
			   }
		   }
	   } 
	   
	   //returns the model view based on the command type
	   modelAndView.addObject("code", "HTTP Response Code : "+resMap.get("code"));
	   modelAndView.addObject("message", "msg : "+resMap.get("msg"));
	   return modelAndView;
	   
   }
   
}