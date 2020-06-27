package air_tek.flights_cargos;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSON {

	private static Utilities oUtilities = new Utilities();

	private static Logger Log = LogManager.getLogger(JSON.class.getName());
	
	/**
	 * 
	 * @param s_JSON_doc
	 * @param s_terms
	 * @return
	 * @throws Exception
	 */
		public ArrayList<String> funcGetValue_JSON(String s_JSON_doc, String s_terms)throws Exception{
			
			//DOMConfigurator.configure("log4j.xml");
			
			ArrayList<String> arr_results = new ArrayList<String>();
			
			try {
				
				if(this.funcIsJSON(s_JSON_doc)) {
					String[] arr_terms = s_terms.split("\\.");
					String[] arr_result_top = this.funcGetValue_JSON_S(s_JSON_doc, arr_terms).split(",");
					for(String s_result:arr_result_top) {
						arr_results.add(s_result.replace("#_#", ":").replace(";", ",").replace("##",";"));
					}
				}else {
					arr_results.add("null");
				}

			}catch(Exception e) {
				oUtilities.funcLogInfo(Log,"Something went wrong. Please checkt the logs", true);
				oUtilities.funcLogInfo(Log, e.toString(),false);
				arr_results.add("null");
			}

			if(arr_results.size() == 1 && arr_results.get(0).equalsIgnoreCase("null")) {
				arr_results.clear();
			}
			return arr_results;
		}
		
	/**
	 * 
	 * @param s_JSON_doc
	 *  the JSON formatted String
	 * @param arr_terms
	 *  for example, "userdata.firstname"
	 * @return
	 *  return ArrayList<String>. default "null"
	 * @throws Exception
	 * 
	 * General function to get value from JSON
	 */
		private String funcGetValue_JSON_S(String s_JSON_doc, String[] arr_terms)throws Exception {
			StringBuffer s_result = new StringBuffer("");
			
			try {
				if(arr_terms.length > 0) {
					String s_top_term = arr_terms[0];
					if(arr_terms.length == 1) {
						/*
						 * last layer, get value directly
						 */
						s_result.append(this.funcGetValue_JSONNODE_ONE_LAYER(s_JSON_doc, s_top_term) + ",");
					}else {
						ArrayList<Object> arr_objs = this.funcGetObjects(s_JSON_doc, s_top_term);
						String[] arr_sub_terms = Arrays.copyOfRange(arr_terms, 1, arr_terms.length);
						if(arr_objs.size() > 0) {
							for(int i=0; i<arr_objs.size(); i++) {
								Object obj_JSON = arr_objs.get(i);
								s_result.append(this.funcGetValue_JSON_S(obj_JSON.toString(), arr_sub_terms)+ ",");
							}
						}else {
							/*
							 * When there isn't any object returned, 
							 * it means the keyword doesn't exist in the JSON doc  
							 * or has empty data
							 */
						}
						
					}
					
				}else {
					/*
					 * If the term array is empty, do nothing. 
					 */
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				s_result = new StringBuffer("null");
			}
			
			String s_final_result = oUtilities.TrimLastComma(s_result.toString(),",").trim();
			return (s_final_result.length() >0)?s_final_result:"null"; 
		}
		
		/*
		 * Get node value from JSON: one layer
		 */
		
		private String funcGetValue_JSONNODE_ONE_LAYER(String s_JSON_doc, String s_term_name) {
			StringBuffer s_result = new StringBuffer("");
			
			JSONObject obj_JSON = new JSONObject(s_JSON_doc);
			try{
				if(this.funcIsNodeExist(obj_JSON, s_term_name)){
					Object obj_temp = obj_JSON.get(s_term_name);
					/*
					 * If it is an JSON Array, parse it as an array
					 * If not, parse it as a straight object value
					 */
					if(obj_temp instanceof JSONArray) {
						JSONArray arr_JSON_obj = (JSONArray)obj_temp;
						if(arr_JSON_obj.length() > 0) {
							for(int i=0; i<arr_JSON_obj.length(); i++) {
								Object obj_target_node = arr_JSON_obj.get(i);
								s_result.append(obj_target_node.toString().
										replace(";","##").replace(",", ";").replace(":", "#_#").trim() + ",");
							}
						}else{
							
						}
					}else{
						/*
						 * If it is not an array, since it is only one layer, just fetch value.
						 */
						s_result.append(this.funcGetValueFromJSONNODE(obj_JSON, s_term_name));
					}
					
				}else {
					
				}
			}catch(Exception e) {
				e.printStackTrace();
				
			}
			String s_final_result = oUtilities.TrimLastComma(s_result.toString(),",").trim();
			return (s_final_result.length() >0)?s_final_result:"null"; 
		}
		
		public ArrayList<String>  funcGetKeysFromJSONObject(String s_JSON_doc)throws Exception {
			
			ArrayList<String> arr_Keys = new ArrayList<String>();
			
			try {
				if(this.funcIsJSON(s_JSON_doc)) {
					JSONObject obj_JSON = new JSONObject(s_JSON_doc);
					for(String s_Key: obj_JSON.keySet()) {
						arr_Keys.add(s_Key);
					}
				}else {
					//Do nothing
				}
				
			}catch(Exception e) {
				oUtilities.funcLogInfo(Log, "Something went wrong when it tried to fetch JSON keys", true);
				oUtilities.funcLogInfo(Log, e.toString(), true);
			}
			return arr_Keys;
		}
		
		/*
		 * Get node value from JSON
		 */
		private String funcGetValueFromJSONNODE(JSONObject obj_input, String s_term_name){
			String s_result = "";
			
			try{
				if(this.funcIsNodeExist(obj_input, s_term_name)){
					
					int int_type = this.funcGetJSONKeyValueType(obj_input,s_term_name);

					switch(int_type){
						case(1):{
							s_result = obj_input.getString(s_term_name);
							break;
						}
						case(2):{
							s_result = String.valueOf(obj_input.getInt(s_term_name));
							break;
						}
						case(3):{
							s_result = String.valueOf(obj_input.getDouble(s_term_name));
							break;
						}
						case(4):{
							s_result = String.valueOf(obj_input.getBoolean(s_term_name));
							break;
						}
						case(5):{
							s_result = String.valueOf(obj_input.getLong(s_term_name));
							break;
						}
						case(6):{
							s_result = obj_input.getJSONObject(s_term_name).toString();
							break;
						}
						default:{
							s_result = "null";
							break;
						}
					}
				}else{
					s_result = "null";
				}
				
			}catch(JSONException je){
				System.out.println(je.toString());
			}
			s_result = s_result.replace(";","##").replace(",", ";").replace(":", "#_#").trim();
			
			if(s_result.length() <= 0){
				s_result = "null";
			}
			return s_result.trim();
		}

		private ArrayList<Object> funcGetObjects(String s_JSON_doc, String s_term){
			ArrayList<Object> arr_obj_results = new ArrayList<Object>();
			
			JSONObject obj_JSON = new JSONObject(s_JSON_doc);
			try {
				if(this.funcIsNodeExist(obj_JSON, s_term)){
					Object obj_temp = obj_JSON.get(s_term);
					/*
					 * If the object is a JOSNArray
					 */
					if(obj_temp instanceof JSONArray) {
						JSONArray arr_JSON_obj = (JSONArray)obj_temp;
						if(arr_JSON_obj.length() > 0){
							for(int i=0; i<arr_JSON_obj.length();i++) {
								Object obj_2nd_layer = arr_JSON_obj.get(i);
								arr_obj_results.add(obj_2nd_layer);
							}
						}
					}
					/*
					 * If the object is not a JSONArray
					 */
					else{
						arr_obj_results.add(obj_temp);
					}
					
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return arr_obj_results;
		}
		
		/*
		 * Get the key value type: String = 1; Integer = 2; Double = 3;
		 */
		private int funcGetJSONKeyValueType(JSONObject obj_input, String s_key_name){
			int int_result = 0;
			Object oKeyNode = obj_input.get(s_key_name);
			if(oKeyNode instanceof String){
				int_result = 1;
			}else if(oKeyNode instanceof Integer){
				int_result = 2;
			}else if(oKeyNode instanceof Double){
				int_result = 3;
			}else if(oKeyNode instanceof Boolean){
				int_result = 4;
			}else if(oKeyNode instanceof Long){
				int_result = 5;
			}else if(oKeyNode instanceof JSONObject){
				int_result = 6;
			}
			return int_result;
		}
		
		private boolean funcIsNodeExist(JSONObject obj_input, String s_term_name){
			boolean bool_result = true;
			if(!obj_input.has(s_term_name)){
				bool_result = false;
			}
			return bool_result;
		}
		
		private boolean funcIsJSON(String s_JSON_doc) {
			boolean bool_IsJSON = false;
			
			try {
				JSONObject obj_JSON = new JSONObject(s_JSON_doc);
				if(obj_JSON instanceof JSONObject) {
					bool_IsJSON = true;
				}
				
			}catch(Exception e) {
				
			}
			return bool_IsJSON;
			
			
		}

}
