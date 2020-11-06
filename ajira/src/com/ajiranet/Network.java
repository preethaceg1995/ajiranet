package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public class Network {
	
	Connections connections = new Connections();
	private List<Device> devices = new LinkedList<>();

	/*
	 * create a device with name and type
	 */
	public Map<String, String> createDevice(String name, String type) {
		Map<String, String> resMap = new HashMap<>();
		
		//checks for DeviceType
		if(type.equals("COMPUTER") || type.equals("REPEATER")) {
			//check for existence of the device 
			Device device = new Device(name, type);
			for(Device dev : devices) {
				if(dev.getName().equals(name)) {
					resMap.put("code", "400");
					resMap.put("msg", "Device '"+name+"' already exists");
					return resMap;
				}
			}
			
			//adding device
			devices.add(device);
			resMap.put("code", "200");
			resMap.put("msg", "Successfully added "+name);
		} else {
			resMap.put("code", "400");
			resMap.put("msg", "type '"+type+"' is not supported");
		}
		
		return resMap;
	}
	
	/*
	 * creates connections between source and targets
	 */
	public Map<String, String> createConnections(String source, List<String> targets) {
		Map<String, String> resMap = new HashMap<>();
		
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
			resMap.put("code", "400");
			resMap.put("msg", "Node '"+source+"' not found");
			return resMap;
		}
		
		//check if any targets not found
		if(!targets.isEmpty()) {
			message.append("Cannot find devices : ");
			String comma = "";
			for(String name : targets) {
				message.append(comma+ name);
				comma = " , ";
			}
			resMap.put("code", "400");
			resMap.put("msg", message.toString());
			return resMap;
		}
		//creates connection
		return connections.createConnections(sourceDev, targetDevices);
	}
	
	/*
	 * modifies the strength of the device
	 */
	public Map<String, String> modifyDeviceStrength(String device, String value) {
		Map<String, String> resMap = new HashMap<>();
		for(Device dev : devices) {
			//check for existence of device
			if(dev.getName().equals(device)) {
				
				//checks device of type REPEATER
				if(dev.getType().equals(DeviceType.REPEATER)) {
					resMap.put("code", "400");
					resMap.put("msg", "Modification not supported for REPEATER");
					return resMap;
				}
				
				//check if the value is valid integer and modify
				try {
					int val = Integer.parseInt(value);
					if(val<0) {
						resMap.put("code", "400");
						resMap.put("msg", "value should be positive");
						return resMap;
					}
					
					//valid integer hence modified
					dev.setStrength(val);
					resMap.put("code", "200");
					resMap.put("msg", "Successfully defined strength");
					return resMap;
				} catch(NumberFormatException e) {
					resMap.put("code", "400");
					resMap.put("msg", "value should be an integer");
					return resMap;
				}
			}
		}
		resMap.put("code", "404");
		resMap.put("msg", "Device not found");
		return resMap;
	}
	
	public Map<String, String> fetchRoute(String from, String to) {
		Map<String, String> resMap = new HashMap<>();
		
		boolean isFromExists = false, isToExists = false;
		Device source = null, dest = null;
		Map<Device, Boolean> visited = new HashMap<>();
		
		//check if from and to are same
		if(from.equals(to)) {
			resMap.put("code", "400");
			resMap.put("msg", "Route is "+from+"->"+to);
			return resMap;
		}
		
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
					resMap.put("code", "400");
					resMap.put("msg", "Route cannot be calculated with repeater");
					return resMap;
				}
				dest = device;
			}
		}
		
		//checks if from device exixts
		if(!isFromExists) {
			resMap.put("msg", "Node '" + from + "' not found");
			resMap.put("code", "404");
			return resMap;
		}
		
		//checks if to device exists
		if(!isToExists) {
			resMap.put("msg", "Node '" + to + "' not found");
			resMap.put("code", "404");
			return resMap;
		}
		
		//makes visited=false for all devices
		for(Device device : devices)
			visited.put(device, false);
		
		//fetches the route
		return connections.fetchRoute(source, dest, visited);
	}
	
	/*
	 * fetches all the devices in the network
	 */
	public Map<String, String> fetchDevices() {
		
		Map<String, String> resMap = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		sb.append("\"devices\" : [");
		for(Device dev : devices) {
			String s = "{ 'type' : '"+dev.getType().toString()+"' , 'name' : '"+dev.getName()+"'}";
			sb.append("\n"+s);
		}
		sb.append("\n]");
		resMap.put("msg", sb.toString());
		resMap.put("code", "200");
		return resMap;
	}
}
