package com.ajiranet;

/**
 * Device class defines properties of a device
 * 
 * @author Preetha K
 *
 */
public class Device {
	
	private String name;
	private DeviceType type;
	private int strength;
	
	//initializes a Device
	Device(String name, String type) {
		this.name = name;
		if(type.equals("COMPUTER")) {
			this.type = DeviceType.COMPUTER;
			this.strength = 5;
		}
		if(type.equals("REPEATER")) {
			this.type = DeviceType.REPEATER;
		}
	}
	
	public String getName() {
		return name;
	}
//	public void setName(String name) {
//		this.name = name;
//	}
	public DeviceType getType() {
		return type;
	}
//	public void setType(DeviceType type) {
//		this.type = type;
//	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
}
