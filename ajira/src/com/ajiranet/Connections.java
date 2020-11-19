package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * This class has the connections between devices and performs all operations related to connections
 * @author Preetha K
 *
 */
public class Connections {
	
	Map<Device, List<Device>> graph = new HashMap<>();
	
	/**
	 * creates a new connection from source device and target devices
	 * 
	 * @param source
	 * @param targets
	 * @return resultMap
	 */
	public Map<String, String> createConnections(Device source, List<Device> targets) {
		
		Map<String, String> resultMap = new HashMap<>();
		List<Device> connectedDevices = graph.get(source);
		
		if(connectedDevices!=null) {
			
			//check for already connected devices
			for(Device device : targets) {
				boolean isDeviceExixts = connectedDevices.contains(device);
				if(isDeviceExixts) {
					targets.remove(device);
				}
			}
			if(targets.isEmpty()) {
				resultMap.put("code", "400");
				resultMap.put("message", "Device already connected");
				return resultMap;
			}
			
			//add not connected devices
			targets.addAll(connectedDevices);
		}
		graph.put(source, targets);
		
		//create bidirectional connection
		for(Device device : targets) {
			connectedDevices = graph.get(device);
			if(connectedDevices==null)
				connectedDevices = new LinkedList<>();
			connectedDevices.add(source);
			graph.put(device, connectedDevices);
		}
		resultMap.put("code", "200");
		resultMap.put("message", "Succesfully Connected");
		return resultMap;
	}
	
	/**
	 * fetches a route from source to destination in that network
	 * 
	 * @param from
	 * @param to
	 * @param visited
	 * @return resultMap
	 */
	public Map<String, String> fetchRoute(Device from, Device to, Map<Device, Boolean> visited) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		//checks is there exists atleast a single connection
		if(graph.isEmpty()) {
			resultMap.put("code", "400");
			resultMap.put("message", "msg : Route not found");
		    return resultMap;
		}
		
		boolean hasRoute = false;
		Queue<Device> queue = new LinkedList<>();
		Map<Device, Device> path = new HashMap<>();
		queue.add(from);
		visited.put(from, true);
		int strength = from.getStrength();
		
		//using BFS algo to fetch route
		while(!queue.isEmpty()) {
			Device src = queue.remove();
			//check if a device reached to device
			if(src==to) {
				hasRoute = true;
				break;
			}
			for(Device dest : graph.get(src)) {
				//add not visited adjacent devices
				if(!visited.get(dest)) {
					queue.add(dest);
					visited.put(dest, true);
					path.put(dest, src);
				}
			}
		}
		
		
		//return route if exists
		if(hasRoute) {
			StringBuilder route = new StringBuilder();
			Stack<Device> revRoute = new Stack<>();
			Device pred = to;
			while(pred!=from) {
				revRoute.push(pred);
				if(pred.getType()==DeviceType.REPEATER)
					strength*=strength;
				else
					strength--;
				pred = path.get(pred);
			}
			revRoute.push(pred);
			
			//checks for strength of a device after traveling which shouldn't be less than zero
			if(strength<0 && from.getType()!=DeviceType.REPEATER) {
				resultMap.put("code", "404");
				resultMap.put("message", "Route not found");
				return resultMap;
			}
			
			//builds the route in a string
			String arrow = "";
			while(!revRoute.isEmpty()) {
				Device pop = revRoute.pop();
				route.append(arrow+pop.getName());
				arrow="->";
			}
			resultMap.put("code", "400");
			resultMap.put("message", route.toString());
			return resultMap;
		}
		resultMap.put("code", "404");
		resultMap.put("message", "Route not found");
		return resultMap;
	}
	
}
