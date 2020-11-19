package com.ajiranet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * ProcessController controls the flow from client to server and handles all the POST methods from the client
 * 
 * @author Preetha K
 *
 */
@Controller
@RequestMapping("/process")
public class ProcessController {
	
	@Autowired
	Network network;
	
	/**
	 * Handles all post request for creating the device
	 * 
	 * @param type
	 * @param name
	 * @return modelAndView
	 */
	@RequestMapping(value="/CREATE/devices", method=RequestMethod.POST)
	public ModelAndView createDevices(@RequestParam(value="type", required=false) String type, @RequestParam(value="name", required=false) String name) {
		Map<String, String> resultMap = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("display");
		resultMap = network.createDevice(name, type);
		modelAndView.addAllObjects(resultMap);
		return modelAndView;
	}
	
	/**
	 * Handles all post request for creating connections between devices
	 * 
	 * @param source
	 * @param targets
	 * @return modelAndView
	 */
	@RequestMapping(value="/CREATE/connections", method=RequestMethod.POST)
	public ModelAndView createConnection(@RequestParam(value="source", required=false) String source, @RequestParam(value="targets", required=false) String targets) {
		List<String> targetList = new LinkedList<>();
		Map<String, String> resultMap = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("display");
		String[] targsList = targets.split(",");
		for(String targ : targsList) {
			targetList.add(targ.trim());
		}
		resultMap = network.createConnections(source, targetList);
		modelAndView.addAllObjects(resultMap);
		return modelAndView;
	}
	
	/**
	 * Handles all post request for modifying the strength of a device
	 * 
	 * @param device
	 * @param strength
	 * @return modelAndView
	 */
	@RequestMapping(value={"/MODIFY/devices/{device}/strength", "/MODIFY/devices/"}, method=RequestMethod.POST)
	public ModelAndView modifyDevices(@PathVariable String device, @RequestParam(value="value", required=false) String strength) {
		Map<String, String> resultMap = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("display");
		resultMap = network.modifyDeviceStrength(device, strength);
		modelAndView.addAllObjects(resultMap);
		return modelAndView;
	}
	
	
	 /**
	  * Handles all post request for fetching a route from a device to another
	  * 
	  * @param from
	  * @param to
	  * @return modelAndView
	  */
	@RequestMapping(value="/FETCH/info-route", method=RequestMethod.POST)
	public ModelAndView fetchRoute(@RequestParam(value="from", required=false) String from, @RequestParam(value="to", required=false) String to) {
		Map<String, String> resultMap = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("display");
		resultMap = network.fetchRoute(from, to);
		modelAndView.addAllObjects(resultMap);
		return modelAndView;
	}
	
	/**
	 * Handles all post request for fetching all devices in a network
	 * 
	 * @return modelAndView
	 */
	@RequestMapping(value="/FETCH/devices", method=RequestMethod.POST)
	public ModelAndView fetchDevices() {
		Map<String, String> resultMap = new HashMap<>();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("display");
		resultMap = network.fetchDevices();
		modelAndView.addAllObjects(resultMap);
		return modelAndView;
	}
   
   
}