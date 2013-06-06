package printercontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jssc.SerialPortException;

public class SerialTester {
	public static void main(String[] args) throws InterruptedException, IOException{
		Printcore prc = new Printcore("COM3", 250000);
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		while(true){
		    String s = bufferRead.readLine();
		    if(s.contains("q")){
		    	break;
		    }
		    if(s.contains("c")){
		    	prc.connect();
		    }
		    if(s.contains("d")){
		    	prc.disconnect();
		    }
		    if(s.contains("p")){
		    	prc.print("D:\\3d_print\\projekty\\paraslicer\\gcodes\\cw.gcode", 230.);
		    }
		    if(s.contains("a")){
		    	prc.abort();
		    }
		    if(s.contains("n")){
		    	prc.pause();
		    }
		    if(s.contains("m")){
		    	prc.unpause();
		    }
		    
		}
	}
}
