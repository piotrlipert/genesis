package printercontrol;
import jssc.SerialPortList;
import java.util.List;

public class SerialControl
{		
    public SerialControl(){	    	
	}
    
    public void listPorts(){
    	 String[] portNames = SerialPortList.getPortNames();
         for(int i = 0; i < portNames.length; i++){
             System.out.println(portNames[i]);
         }
    }
    public static Printcore autoConnect(){
    	String[] portNames = SerialPortList.getPortNames();
    	Printcore pc;
    	for(int i = portNames.length -1; i>=0; i--){
    			pc = new Printcore(portNames[i], 250000);
    			if(pc.connect()){
    				System.out.println("succes at "+portNames[i]);
    				return pc;
    			}
    			System.out.println("failure at "+portNames[i]);
    	}
    	return null;
    }
	    
}