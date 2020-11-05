package ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
	
public class Connections {
	
	Map<Device, List<Device>> connection = new HashMap<>();
	
	public String createConnections(Device source, List<Device> targets) {
		
		List<Device> removedDevices = new LinkedList<>();
		List<Device> connectedDevices = connection.get(source);
		
		if(!connectedDevices.isEmpty()) {
			
			//check for already connected devices
			for(Device device : targets) {
				boolean isDeviceExixts = connectedDevices.contains(device);
				if(isDeviceExixts) {
					removedDevices.add(device);
					targets.remove(device);
				}
			}
			if(targets.isEmpty()) {
				return "Device already connected";
			}
			
			//add not connected devices
			targets.addAll(connectedDevices);
		}
		connection.put(source, targets);
		
		//create bidirectional connection
		for(Device device : targets) {
			connectedDevices = connection.get(device);
			if(connectedDevices.isEmpty())
				connectedDevices = new LinkedList<>();
			connectedDevices.add(source);
			connection.put(device, connectedDevices);
		}
		return "Succesfully Connected";
	}
	
	public String fetchRoute(Device from, Device to, Map<Device, Boolean> visited) {
		
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
			List<Device> revRoute = new LinkedList<>();
			Device pred = to;
			while(pred!=from) {
				revRoute.add(pred);
				if(pred.getType()==DeviceType.REPEATER)
					strength*=strength;
				else
					strength--;
				pred = path.get(pred);
			}
			revRoute.add(pred);
			if(strength<0)
				return "Route not found";
			String arrow = "";
			for(Device dev : revRoute) {
				route.append(arrow + dev.getName());
				arrow = "->";
			}
			return revRoute.toString();
		}
		return "Route not found";
	}
	
}
