package air_tek.flights_cargos;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * @chongxiang
 * June/27/2020
 */
public class Flights_Cargos_Schedule 
{
	private static Utilities oUtilities = new Utilities();
	
	private static JSON oJSON = new JSON();
	
	private static Logger Log = LogManager.getLogger(Flights_Cargos_Schedule.class.getName());
	
	private Map<String, String> m_Filghts = new TreeMap<String, String>();
	
	private Map<String, Integer> m_Flights_Cap = new TreeMap<String, Integer>();
	
	private Map<String, String> m_Orders = new TreeMap<String, String>();
	
    public static void main( String[] args )throws Exception 
    {
    	Flights_Cargos_Schedule oFlights_Cargos_Schedule = new Flights_Cargos_Schedule();
    	
    	
    	oFlights_Cargos_Schedule.func_Initialize();
    	
    	oFlights_Cargos_Schedule.func_Present_Filghts_Schedule();
    	
    	oFlights_Cargos_Schedule.func_Set_Order_Cargo_List();
    	
    }
    
    public void func_Initialize()throws Exception {
    	
    	String s_Flights_FilePath = "/filghts/flights.json";
    	this.func_Load_Flights_Schedule(s_Flights_FilePath);
    	
    	String s_Order_FilePath = "/filghts/coding-assigment-orders.json";
    	this.func_Load_Package_Orders(s_Order_FilePath);
    	
    }
    
    public void func_Present_Filghts_Schedule() throws Exception{
    	
    	try {
        	if(this.m_Filghts.size() > 0) {
        		String s_Schedule = "Flight" + "," + "Depature" + "," + "Arrival" + "," + "Time" + "\n";
        		for(String s_Flight: this.m_Filghts.keySet()) {
        			String[] arr_Flight_Data = this.m_Filghts.get(s_Flight).split("-");
        			s_Schedule = s_Schedule + s_Flight.replace(" ", ": ") + ", "
        						+ "depature: " +  arr_Flight_Data[0] + ", "
        						+ "arrival: " + arr_Flight_Data[1] + ", "
        						+ "day: " + arr_Flight_Data[2].replace("Day", "") + "\n";
        					
        		}
        		System.out.println("=====================Flights Schedule List=====================");
        		System.out.println(s_Schedule);
        		
        		oUtilities.funcGenerateOutputFile("/reports/Flight_Schedule.csv", s_Schedule);
        	}
    	}catch(Exception e) {
    		oUtilities.funcLogInfo(Log, "Something went wrong when it tried to handle flights schedule. Please check logs", true);
    		oUtilities.funcLogInfo(Log, e.toString(), false);
    	}

    }
    
    public void func_Set_Order_Cargo_List()throws Exception {
    	
    	try {
    		
    		if(this.m_Orders.size() > 0 && this.m_Filghts.size() > 0) {
    			String s_Order_Cargo = "Order_Id" + "," + "Flight" + "," + "Depature" + "," + "Arrival" + "," + "Time" + "\n"; 
    			for(String s_Order: this.m_Orders.keySet()) {
    				String s_Destination = this.m_Orders.get(s_Order);
    				boolean bool_Processed = false;
    				
    				for(String s_Flight: this.m_Filghts.keySet()) {
    					String[] arr_Flight_Data = this.m_Filghts.get(s_Flight).split("-");
    					int int_Capacity = this.m_Flights_Cap.get(s_Flight);
    					if(arr_Flight_Data[1].equalsIgnoreCase(s_Destination) && int_Capacity > 0) {
    						s_Order_Cargo = s_Order_Cargo + "order: " + s_Order + ", " 
    								+ s_Flight.replace("Flight ", "flightnumber: ") + ", "
            						+ "depature: " +  arr_Flight_Data[0] + ", "
            						+ "arrival: " + arr_Flight_Data[1] + ", "
            						+ "day: " + arr_Flight_Data[2].replace("Day", "") + "\n";
    						
    						int_Capacity = int_Capacity - 1;
    						this.m_Flights_Cap.replace(s_Flight, int_Capacity);
    						bool_Processed = true;
    						break;
    					}		
            		}
    				
    				if(!bool_Processed) {
    					s_Order_Cargo = s_Order_Cargo + s_Order + "," + "flightnumber: not scheduled" + "\n";
    				}
    				
    			}
    			System.out.println("=====================Order Cargo List=====================");
    			System.out.println(s_Order_Cargo);
    			
    			oUtilities.funcGenerateOutputFile("/reports/Order_Cargo_List.csv", s_Order_Cargo);
    			
    		}else if(this.m_Orders.size() <= 0){
    			oUtilities.funcLogInfo(Log, "The order list is empty. Please check", true);
    		}else {
    			oUtilities.funcLogInfo(Log, "The flights list is empty. Please check", true);
    		}
    		
    		
    	}catch(Exception e) {
    		oUtilities.funcLogInfo(Log, "Something went wrong when it tried to handle order cargos. Please check logs", true);
    		oUtilities.funcLogInfo(Log, e.toString(), false);
    	}
    }
    
    public void func_Load_Flights_Schedule(String s_FilePath) throws Exception{
    	
    	try {
    		String s_Flights_Schedule_Data = oUtilities.funcReadDataFromFiles(System.getProperty("user.dir") + s_FilePath);
        	
        	ArrayList<String> arr_Flights = oJSON.funcGetValue_JSON(s_Flights_Schedule_Data, "flights");
        	
        	if(arr_Flights.size() > 0) {
        		for(String s_Flight_Data: arr_Flights) {
        			String s_Filght = oJSON.funcGetValue_JSON(s_Flight_Data, "flight").get(0);
        			String s_From = oJSON.funcGetValue_JSON(s_Flight_Data, "from").get(0);
        			String s_To = oJSON.funcGetValue_JSON(s_Flight_Data, "to").get(0);
        			String s_Time = oJSON.funcGetValue_JSON(s_Flight_Data, "time").get(0);
        			int int_Capacity = Integer.parseInt(oJSON.funcGetValue_JSON(s_Flight_Data, "capacity").get(0));
        			
        			this.m_Filghts.put(s_Filght
        							  , s_From + "-" + s_To + "-" + s_Time);
        			
        			this.m_Flights_Cap.put(s_Filght, int_Capacity);
        			
        		}
        	}
    		
    	}catch(Exception e) {
    		oUtilities.funcLogInfo(Log, "Something went wrong when it tried to load flights schedule. Please check logs", true);
    		oUtilities.funcLogInfo(Log, e.toString(), false);
    	}
    	
    	
    }
    
    public void func_Load_Package_Orders(String s_FilePath) throws Exception {
    	try {
    		String s_Order_Data = oUtilities.funcReadDataFromFiles(System.getProperty("user.dir") + s_FilePath);
        	
    		ArrayList<String> arr_Keys = oJSON.funcGetKeysFromJSONObject(s_Order_Data);
    		
    		Collections.sort(arr_Keys, String.CASE_INSENSITIVE_ORDER);
    		
    		if(arr_Keys.size() > 0) {
    			for(String s_Key: arr_Keys) {
    				this.m_Orders.put(s_Key, oJSON.funcGetValue_JSON(s_Order_Data, s_Key+".destination").get(0));
    			}
    		}

    	}catch(Exception e) {
    		oUtilities.funcLogInfo(Log, "Something went wrong when it tried to load order data. Please check logs", true);
    		oUtilities.funcLogInfo(Log, e.toString(), false);
    	}
    }
    
}
