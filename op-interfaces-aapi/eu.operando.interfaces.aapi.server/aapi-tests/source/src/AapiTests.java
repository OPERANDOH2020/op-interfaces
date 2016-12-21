

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.org.apache.bcel.internal.util.ClassPath;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexandris
 */
public class AapiTests {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Properties prop = new Properties();
	InputStream input = null;
	OutputStream output = null;
        
        File jarPath=new File(AapiTests.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath=jarPath.getParentFile().getAbsolutePath();
        System.out.println(" propertiesPath-"+propertiesPath);
        String tgt = "",st = "", validationXML = "";
        try {
        input = new FileInputStream(propertiesPath+"/config.properties");

        // load a properties file
        prop.load(input);
        /******Test #1 Generate TGT from user given in property file**********/
        System.out.println("******Test #1 Generate TGT from user given in property file**********");
        String url = prop.getProperty("as.url")+"/aapi/tickets";

        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");

        String data =  "{\n" +
                        "  \"password\": \""+prop.getProperty("as.username")+"\",\n" +
                        "  \"username\": \""+prop.getProperty("as.password")+"\"\n" +
                        "}";
        System.out.println("POST "+url);
        System.out.println("Parameters: "+data);

        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(data);
        out.close();
        tgt = getString(conn.getInputStream());
        System.out.println(tgt);
        System.out.println("****Test #1: Status Passed");
        } catch (Exception ex) {
		ex.printStackTrace();
                System.out.println("****Test #1: Status Failed");

	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
                
	}
        
        try {
        input = new FileInputStream(propertiesPath+"/config.properties");

        // load a properties file
        prop.load(input);
        /******Test #1 Generate TGT from user given in property file**********/
        System.out.println("\n******Test #2 Generate ST provided the TGT generated before for serviceId from property file**********");
        String url = prop.getProperty("as.url")+"/aapi/tickets/"+tgt;

        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");

        String data = prop.getProperty("as.serviceId");
        System.out.println("POST "+url);
        System.out.println("Parameters: "+data);

        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(data);
        out.close();
        st = getString(conn.getInputStream());
        System.out.println(st);
        System.out.println("****Test #2: Status Passed");
        } catch (Exception ex) {
		ex.printStackTrace();
                System.out.println("****Test #2: Status Failed");

	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
                
	}

	try {
        input = new FileInputStream(propertiesPath+"/config.properties");

        // load a properties file
        prop.load(input);
        /******Test #1 Generate TGT from user given in property file**********/
        System.out.println("\n******Test #3 Validate ST provided the ST generated before for serviceId from property file**********");
        String data = prop.getProperty("as.serviceId");
        
        String url = prop.getProperty("as.url")+"/aapi/tickets/"+st+"/validate?serviceId="+data;

        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setDoOutput(true);

        conn.setRequestMethod("GET");
        System.out.println("GET "+url);
        validationXML = getString(conn.getInputStream());
        System.out.println(validationXML);
        System.out.println("****Test #3: Status Passed");
        } catch (Exception ex) {
		ex.printStackTrace();
                System.out.println("****Test #3: Status Failed");

	} finally {
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
                
	}
  }
    
    public static String getString(InputStream in){
        String fres = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            fres =  result.toString();
        } catch (IOException ex) {
            Logger.getLogger(AapiTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fres;
    }
    
    
}
