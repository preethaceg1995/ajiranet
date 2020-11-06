package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
	
public class Connections {
	
	Map<Device, List<Device>> connection = new HashMap<>();
	
	public Map<String, String> createConnections(Device source, List<Device> targets) {
		
		Map<String, String> resMap = new HashMap<>();
		List<Device> connectedDevices = connection.get(source);
		
		if(connectedDevices!=null) {
			
			//check for already connected devices
			for(Device device : targets) {
				boolean isDeviceExixts = connectedDevices.contains(device);
				if(isDeviceExixts) {
					targets.remove(device);
				}
			}
			if(targets.isEmpty()) {
				resMap.put("code", "400");
				resMap.put("msg", "Device already connected");
				return resMap;
			}
			
			//add not connected devices
			targets.addAll(connectedDevices);
		}
		connection.put(source, targets);
		
		//create bidirectional connection
		for(Device device : targets) {
			connectedDevices = connection.get(device);
			if(connectedDevices==null)
				connectedDevices = new LinkedList<>();
			connectedDevices.add(source);
			connection.put(device, connectedDevices);
		}
		resMap.put("code", "200");
		resMap.put("msg", "Succesfully Connected");
		return resMap;
	}
	
	public Map<String, String> fetchRoute(Device from, Device to, Map<Device, Boolean> visited) {
		
		Map<String, String> resMap = new HashMap<>();
		boolean hasRoute = false;
		Queue<Device> queue = new LinkedList<>();
		Map<Device, Device> path = new HashMap<>();
		queue.add(from);
		visited.put(from, true);
		int strength = from.getStrength();
		while(!queue.isEmpty()) {
			Device src = queue.remove();
			//check if a device reached to device
			if(src==to) {
				hasRoute = true;
				break;
			}
			for(Device dest : connection.get(src)) {
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
			if(strength<0) {
				resMap.put("code", "404");
				resMap.put("msg", "Route not found");
				return resMap;
			}
			String arrow = "";
			while(!revRoute.isEmpty()) {
				Device pop = revRoute.pop();
				route.append(arrow+pop.getName());
				arrow="->";
			}
			resMap.put("code", "400");
			resMap.put("msg", route.toString());
			return resMap;
		}
		resMap.put("code", "404");
		resMap.put("msg", "Route not found");
		return resMap;
	}
	
}
