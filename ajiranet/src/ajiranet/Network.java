package ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Network {
	
	//@AutoWired
	Connections connections = new Connections();
	private List<Device> devices = new LinkedList<>();

	public String createDevice(String name, String type) {
		//check for device type
		if(type!="COMPUTER" || type!="REPEATOR") {
			return "type '"+type+"' is not supported";
		}
		
		//check for existence of the device 
		Device device = new Device(name, type);
		if(devices.contains(device)) {
			return "Device '"+name+"' already exists";
		}
		
		//adding device
		devices.add(device);
		return "Successfully added "+name;
	}
	
	public String createConnections(String source, List<String> targets) {
		
		Device sourceDev = null;
		StringBuilder message = new StringBuilder();
		List<Device> targetDevices = new LinkedList<>();
		
		for(Device device : devices) {
			String name = device.getName();
			//check if source device exists
			if(source.equals(name))
				sourceDev = device;
			//add existing target devices to a list
			if(targets.contains(name)) {
				targetDevices.add(device);
				targets.remove(name);
			}
		}
		
		if(sourceDev==null)
			return "Node '"+source+"' not found";
		
		//check if source and target are same
		if(targets.contains(source))
			return "Cannot connect to itself";
		
		//check if any targets not found
		if(!targets.isEmpty()) {
			message.append("Cannot find devices : ");
			String comma = "";
			for(String name : targets) {
				message.append(comma+ name);
				comma = " , ";
			}
		}
		
		String connected = connections.createConnections(sourceDev, targetDevices);
		message.append("\n"+connected);
		return message.toString();
	}
	
	public String modifyDeviceStrength(String device, String value) {
		
		for(Device dev : devices) {
			//check for existence of device
			if(dev.getName()==device) {
				
				//check for device not REPEATER
				if(dev.getType()==DeviceType.REPEATER) {
					return "Modification not supported for REPEATER";
				}
				
				//check if the value is valid integer and modify
				try {
					int val = Integer.parseInt(value);
					if(val<0)
						return "value should be positive";
					dev.setStrength(val);
					return "Successfully defined strength";
				} catch(NumberFormatException e) {
					return "value should be an integer";
				}
			}
		}
		return "Device not found";
	}
	
	public String fetchRoute(String from, String to) {
		
		boolean isFromExists = false, isToExists = false;
		Device source = null, dest = null;
		Map<Device, Boolean> visited = new HashMap<>();
		
		//check if from and to are same
		if(from.equals(to))
			return "Route is "+from+" -> "+to;
		
		//check if device exists
		for(Device device : devices) {
			if(from.equals(device.getName())) {
				isFromExists = true;
				source = device;
			}
			if(to.equals(device.getName())) {
				isToExists = true;
				if(device.getType()==DeviceType.REPEATER)
					return "Route cannot be calculated with repeater";
				dest = device;
			}
		}
		if(!isFromExists)
			return "Node '" + from + "' not found";
		if(!isToExists)
			return "Node '" + to + "' not found";
		
		for(Device device : devices)
			visited.put(device, false);
		return connections.fetchRoute(source, dest, visited);
	}
	
	public List<Device> fetchDevices() {
		return devices;
	}
}
