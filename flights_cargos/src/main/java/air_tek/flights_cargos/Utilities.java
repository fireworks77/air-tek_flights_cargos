package air_tek.flights_cargos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Utilities {
	
	
	public static String getProperty(String s_Property_Name)throws Exception{
		Properties property = new Properties();
		InputStream inp_Properties = null;
		String result = "";
		
		try{
			inp_Properties = new FileInputStream("config.properties");
			property.load(inp_Properties);
			result = property.getProperty(s_Property_Name);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(inp_Properties != null){
				try{
					inp_Properties.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return result;
		
	}
	
	public String TrimLastComma(String s_input, String s_character){
		if(s_input.length() > 0){
			if(s_input.substring(s_input.length()-1,s_input.length()).equalsIgnoreCase(s_character)){
				s_input = s_input.substring(0,s_input.length()-1);
			}
		}
		return s_input;
	}
	
	public String funcReadDataFromFiles(String s_FilePath)throws Exception{
		StringBuffer s_Results = new StringBuffer("");
		BufferedReader br_File = new BufferedReader(new FileReader(s_FilePath));
		try {
		    String s_Line = br_File.readLine();
		    while (s_Line != null) {
		    	s_Results.append(s_Line);
		    	s_Line = br_File.readLine();
		    }
		} finally {
			br_File.close();
		}
		return s_Results.toString();
	}

	public void funcLogInfo(Logger Log, String s_Message, boolean bool_Print) {
		Log.info(s_Message);
		if(bool_Print) {
			System.out.println(s_Message);
		}
	}
	
	public Boolean funcGenerateOutputFile(String s_FileName, String s_Message) throws Exception{
		
		String currentUsersHomeDir = System.getProperty("user.dir");
		String strOutputFileName = "";

		strOutputFileName = currentUsersHomeDir + File.separator + s_FileName;
		
		try{
		    FileWriter oFile = new FileWriter(strOutputFileName);
		    oFile.append(s_Message);				
		    oFile.flush();
		    oFile.close();
		}
		catch(IOException e){
		     e.printStackTrace();
		     return false;
		} 
		return true;
	}
}
