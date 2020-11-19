package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Network class has all devices in a network and performs all operations related to devices
 * 
 * @author Preetha K
 *
 */
public class Network {
	
	@Autowired
	Connections connections;
	private List<Device> devices = new LinkedList<>();

	/**
	 * create a device with provided name and type
	 * 
	 * @param name
	 * @param type
	 * @return resultMap
	 */
	public Map<String, String> createDevice(String name, String type) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		//checks for valid syntax
		if(type==null || name==null || type.isEmpty() || name.isEmpty()){
			return getInvalidMessage(resultMap);
		}
		
		//checks for DeviceType
		if(type.equals("COMPUTER") || type.equals("REPEATER")) {
			//check for existence of the device 
			Device device = new Device(name, type);
			for(Device dev : devices) {
				if(dev.getName().equals(name)) {
					resultMap.put("code", "400");
					resultMap.put("message", "msg : Device '"+name+"' already exists");
					return resultMap;
				}
			}
			
			//adding device
			devices.add(device);
			resultMap.put("code", "200");
			resultMap.put("message", "msg : Successfully added "+name);
		} else {
			resultMap.put("code", "400");
			resultMap.put("message", "msg : type '"+type+"' is not supported");
		}
		
		return resultMap;
	}
	
	/**
	 * creates connections between source and target devices
	 * 
	 * @param source
	 * @param targets
	 * @return resultMap
	 */
	public Map<String, String> createConnections(String source, List<String> targets) {
		Map<String, String> resultMap = new HashMap<>();
		
		//checks for valid syntax
		if(source==null || targets==null || source.isEmpty() || targets.isEmpty()) {
			return getInvalidMessage(resultMap);
		}
		
		//checks if source and target are same
		if(targets.size()==1 && source.equals(targets.get(0))) {
			resultMap.put("code", "400");
			resultMap.put("message", "Cannot connect to itself");
			return resultMap;
		}
		
		Device sourceDev = null;
		StringBuilder message = new StringBuilder();
		List<Device> targetDevices = new LinkedList<>();
		
		for(Device device : devices) {
			String name = device.getName();
			//checks for source device
			if(source.equals(name))
				sourceDev = device;
			//add existing target devices to a list
			if(targets.contains(name)) {
				targetDevices.add(device);
				targets.remove(name);
			}
		}
		//checks if source exists
		if(sourceDev==null) {
			resultMap.put("code", "400");
			resultMap.put("message", "msg : Node '"+source+"' not found");
			return resultMap;
		}
		
		//check if any targets not found
		if(!targets.isEmpty()) {
			message.append("Cannot find devices : ");
			String comma = "";
			for(String name : targets) {
				message.append(comma+ name);
				comma = " , ";
			}
			resultMap.put("code", "400");
			resultMap.put("message", "msg : "+message.toString());
			return resultMap;
		}
		//creates connection
		return connections.createConnections(sourceDev, targetDevices);
	}
	
	/**
	 * modifies the strength of the device
	 * 
	 * @param device
	 * @param value
	 * @return resultMap
	 */
	public Map<String, String> modifyDeviceStrength(String device, String value) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		//checks for valid syntax
		if(device==null || value==null || device.isEmpty() || value.isEmpty()) {
			return getInvalidMessage(resultMap);
		}
		
		for(Device dev : devices) {
			//check for existence of device
			if(dev.getName().equals(device)) {
				
				//checks device of type REPEATER
				if(dev.getType().equals(DeviceType.REPEATER)) {
					resultMap.put("code", "400");
					resultMap.put("message", "msg : Modification not supported for REPEATER");
					return resultMap;
				}
				
				//check if the value is valid integer and modify
				try {
					int val = Integer.parseInt(value);
					if(val<0) {
						resultMap.put("code", "400");
						resultMap.put("message", "msg : value should be positive");
						return resultMap;
					}
					
					//valid integer hence modified
					dev.setStrength(val);
					resultMap.put("code", "200");
					resultMap.put("message", "msg : Successfully defined strength");
					return resultMap;
				} catch(NumberFormatException e) {
					resultMap.put("code", "400");
					resultMap.put("message", "msg : value should be an integer");
					return resultMap;
				}
			}
		}
		resultMap.put("code", "404");
		resultMap.put("message", "msg : Device not found");
		return resultMap;
	}
	
	/**
	 * fetches the route from a source to a destination
	 * @param from
	 * @param to
	 * @return resultMap
	 */
	public Map<String, String> fetchRoute(String from, String to) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		//checks for valid syntax
		if(from==null || to==null || from.isEmpty() || to.isEmpty()) {
			return getInvalidMessage(resultMap);
		}
		
		boolean isFromExists = false, isToExists = false;
		Device source = null, dest = null;
		Map<Device, Boolean> visited = new HashMap<>();
		
		
		//check if device exists
		for(Device device : devices) {
			if(from.equals(device.getName())) {
				isFromExists = true;
				source = device;
			}
			if(to.equals(device.getName())) {
				isToExists = true;
				//REPEATER cannot be a destination
				if(device.getType()==DeviceType.REPEATER) {
					resultMap.put("code", "400");
					resultMap.put("message", "msg : Route cannot be calculated with repeater");
					return resultMap;
				}
				dest = device;
			}
		}
		
		//checks if from device exixts
		if(!isFromExists) {
			resultMap.put("message", "msg : Node '" + from + "' not found");
			resultMap.put("code", "404");
			return resultMap;
		}
		
		//checks if to device exists
		if(!isToExists) {
			resultMap.put("message", "msg : Node '" + to + "' not found");
			resultMap.put("code", "404");
			return resultMap;
		}
		
		//check if from and to are same
		if(from.equals(to)) {
			resultMap.put("code", "400");
			resultMap.put("message", "msg : Route is "+from+"->"+to);
			return resultMap;
		}
				
		//makes visited=false for all devices
		for(Device device : devices)
			visited.put(device, false);
		
		//fetches the route
		return connections.fetchRoute(source, dest, visited);
	}
	
	/**
	 * fetches all the devices in the network
	 * 
	 * @return resultMap
	 */
	public Map<String, String> fetchDevices() {
		
		Map<String, String> resultMap = new HashMap<>();
		
		//checks if there exists a device in the network
		if(devices.isEmpty()) {
			resultMap.put("message", "msg : No devices in the network");
			resultMap.put("code", "400");
			return resultMap;
		}
		
		//fetches the route
		String comma = " ";
		StringBuilder sb = new StringBuilder();
		sb.append("\"devices\" : [");
		
		for(Device dev : devices) {
			String s = "{ 'type' : '"+dev.getType().toString()+"' , 'name' : '"+dev.getName()+"'}";
			sb.append("\n"+comma+s);
			comma = " , ";
		}
		sb.append("\n]");
		resultMap.put("message", "msg : "+sb.toString());
		resultMap.put("code", "200");
		return resultMap;
	}
	
	/**
	 * generates a map for the default message for invalid syntax
	 * @param resultMap
	 * @return resultMap
	 */
	private Map<String, String> getInvalidMessage(Map<String, String> resultMap) {
			resultMap.put("code", "400");
			resultMap.put("message", "msg : Invalid Syntax");
		   return resultMap;
	   }
}
