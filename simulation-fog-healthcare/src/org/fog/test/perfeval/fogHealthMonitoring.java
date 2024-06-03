package org.fog.test.perfeval;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.placement.Controller;
import org.fog.placement.ModuleMapping;
import org.fog.placement.ModulePlacementEdgewards;
import org.fog.placement.ModulePlacementMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;

public class fogHealthMonitoring {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	static int numOfFogDevices = 2; //NUMBER OF FOG NODES
	static int numOfSensorsPerFogDevice = 3; //NUMBER OF SENSORS PER FOG NODE
	static double SENSOR_TRANSMISSION_TIME = 10; //SENDS DATA IN EVERY 5 or 10 SECONDS
	private static boolean CLOUD = false;
	
	public static void main(String[] args) {
		Log.printLine("Starting IoT System...\n");
		try {
			Log.disable();
			int num_user = 1; // NUMBER OF CLOUD USERS
			
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events
			CloudSim.init(num_user, calendar, trace_flag);
			String appId = "dcns"; // identifier of the application
			FogBroker broker = new FogBroker("broker");
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			createFogDevices(broker.getId(), appId); //calling nodes start from here
			Controller controller = null;
			ModuleMapping moduleMapping = ModuleMapping.createModuleMapping(); // initializing a module mapping
			
			for(FogDevice device : fogDevices){
				if(device.getName().startsWith("c")){ // names of all sensors that start with 'c' 
					moduleMapping.addModuleToDevice("data-capture", device.getName());  // fixing 1 instance of the Data module to each Sensor
				}
			}
			
			for(FogDevice device : fogDevices){
				if(device.getName().startsWith("a")){ // names of all fog devices start with 'a' 
					moduleMapping.addModuleToDevice("data-filteration", device.getName());  // fixing 1 instance of the Data module to each Sensor
				}
			}

			if(CLOUD){
				// if the mode of deployment is cloud-based
				moduleMapping.addModuleToDevice("data-capture", "cloud"); // placing all instances of Object Detector module in the Cloud
				moduleMapping.addModuleToDevice("data-filteration", "cloud"); // placing all instances of Object Tracker module in the Cloud
			}
			
			controller = new Controller("master-controller", fogDevices, sensors, actuators);
			controller.submitApplication(application, 
					(CLOUD) ? (new ModulePlacementMapping(fogDevices, application, moduleMapping))
							: (new ModulePlacementEdgewards(fogDevices, sensors, actuators, application, moduleMapping)));

			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());
			CloudSim.startSimulation();
			CloudSim.stopSimulation();

			Log.printLine("Finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	private static void createFogDevices(int userId, String appId) {
		//ADDING CLOUD
		//name, MIPS, RAM(MB), Up-bandwidth, down-bandwidth, level in topology, rate-per-MIPS(cost), busy-power(Watt), idle-power(Watt)
		FogDevice cloud = createFogDevice("cloud", 15200, 64000, 100, 10000, 0, 0.01, 103.0, 83.25);
		cloud.setParentId(-1);
		fogDevices.add(cloud);

		//ADDING PROXY-SERVER
		FogDevice proxy = createFogDevice("proxy-server", 2800, 4000, 10000, 10000, 1, 0.0, 107.339, 83.4333);
		proxy.setParentId(cloud.getId());
		proxy.setUplinkLatency(100); // latency of connection between proxy-server and cloud is 100ms
		fogDevices.add(proxy);

		//NOW ADDING FOG NODES (ONE FOG DEVICE IN EACH AREA).
		for(int i=0; i<numOfFogDevices; i++){
			addDevice(i+"", userId, appId, proxy.getId());
		}
	}

	private static FogDevice addDevice(String id, int userId, String appId, int parentId){
		//THE NAME "fogNode" IS A FOG DEVICE
		FogDevice fogNode = createFogDevice("a-"+id, 54, 4, 4, 4, 2, 0.0, 71.0, 1.0); //ESP8266 parameters
		fogDevices.add(fogNode);
		fogNode.setUplinkLatency(2); // latency of connection between fogNode and proxy-server is 2ms
		
		//NOW ADDING SENSORS IN EACH FOG LAYER.
		for(int i=0; i<numOfSensorsPerFogDevice; i++){
			String mobileId = id+"-"+i;
			FogDevice edgeSensor = addSensor(mobileId, userId, appId, fogNode.getId()); // adding a edgeSensor to the physical topology. edgeSensors have been modeled as fog devices as well.
			edgeSensor.setUplinkLatency(2); // latency of connection between edgeSensor and fogNode is 2ms
			fogDevices.add(edgeSensor);
		}
		fogNode.setParentId(parentId);
		
		return fogNode;
	}
	
	private static FogDevice addSensor(String id, int userId, String appId, int parentId){
		//SENSOR PARAMETERS
		FogDevice edgeSensor = createFogDevice("c-"+id, 50, 4, 2, 2, 3, 0.0, 61.53, 1.2);
		edgeSensor.setParentId(parentId);
		
		// inter-transmission time of edgeSensor follows a deterministic distribution
		Sensor sensor = new Sensor("s-"+id, "SENSOR", userId, appId, new DeterministicDistribution(SENSOR_TRANSMISSION_TIME)); 
		sensors.add(sensor);
		
		sensor.setGatewayDeviceId(edgeSensor.getId());
		sensor.setLatency(1.0);  // latency of connection between edgeSensor and the parent Smart edgeSensor is 1ms

		Actuator smallActuator = new Actuator("ptz-"+id, userId, appId, "PTZ_CONTROL");
		actuators.add(smallActuator);

		smallActuator.setGatewayDeviceId(parentId);
		smallActuator.setLatency(1.0);  // latency of connection between smallActuator Control and the parent Smart edgeSensor is 1ms
		
		return edgeSensor;
	}
	
	/**
	 * Creates a vanilla fog device
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, long mips,int ram, long upBw, long downBw, int level, double ratePerMips, double busyPower, double idlePower) {
		List<Pe> peList = new ArrayList<Pe>();

		//Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 1000000; // host storage
		int bw = 10000;

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new StreamOperatorScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);
		
		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; 		// time zone this resource located
		double cost = 3.0; 				// the cost of using processing in this resource
		double costPerMem = 0.05; 		// the cost of using memory in this resource
		double costPerStorage = 0.001; 	// the cost of using storage in this resource
		double costPerBw = 0.0; 		// the cost of using bandwidth in this resource
		
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now
		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);
		FogDevice fogdevice = null;
		try {
			fogdevice = new FogDevice(nodeName, characteristics, new AppModuleAllocationPolicy(hostList), storageList, 10, upBw, downBw, 0, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		fogdevice.setLevel(level);
		
		return fogdevice;
	}

	/**
	 * @param appId unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	@SuppressWarnings({"serial" })
	private static Application createApplication(String appId, int userId){
		Application application = Application.createApplication(appId, userId);
		// Adding modules (vertices) to the application model (directed graph)
		application.addAppModule("data-capture", 1);
		application.addAppModule("data-filteration", 1);
		
		//Connecting the application modules (vertices) in the application model (directed graph) with edges
		application.addAppEdge("SENSOR", "data-capture", 10, 1, "SENSOR", Tuple.UP, AppEdge.SENSOR); // adding edge from edgeSensor to Data module carrying tuples of type SENSOR
		application.addAppEdge("data-capture", "data-filteration", 10, 1, "filter",Tuple.UP, AppEdge.MODULE); // adding edge from Slot Detector to PTZ CONTROL (actuator)
		application.addAppEdge("data-filteration", "PTZ_CONTROL", 10, 10, 1, "PTZ_PARAMS", Tuple.UP, AppEdge.ACTUATOR);
		application.addTupleMapping("data-capture", "SENSOR", "filter", new FractionalSelectivity(1.0));
		application.addTupleMapping("data-filteration", "filter", "PTZ_PARAMS", new FractionalSelectivity(1.0));
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){
			{
				add("SENSOR");
				add("data-capture");
				add("data-filteration");
				add("PTZ_CONTROL");
			}}
		);
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
		application.setLoops(loops);
		return application;
	}
}
