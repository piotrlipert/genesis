package printercontrol;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jssc.SerialPort;
import jssc.SerialPortException;

public class Printcore{
	boolean loud = true; //print shit to console
	//connection fields
	int baudRate;
	String comPort;
	SerialPort sp;
	ListenSend ls;
	TempMonitor tm;
	//printer state fields
	boolean connected = false;
	boolean printing = false;
	boolean paused = false;
	boolean print_finished = false;
	//temperature related fields
	double targetTemp = 0.;
	double lastMeasuredTemp = 0.;
	//command buffers
	ArrayList<String> printBuffer;
	ArrayList<String> quickBuffer;
	//timing
	int printGcodesCount;
	int printGcodesSent;
	long timePrintStarted = 0;
	long timePrintFinished = 0;
	
	
	public Printcore(String cPort, int bRate){
		baudRate = bRate;
		comPort = cPort;
		printBuffer = new ArrayList<String>();
		quickBuffer = new ArrayList<String>();
	}
	
	public boolean connect(){
		if(baudRate <=0 || !comPort.contains("COM")){
			if(loud)System.out.println("Err.printcore:<"+comPort+":"+baudRate+"> are wrong connection parameters");
			return false;
		}
		sp = new SerialPort(comPort);
		try {
			boolean a = sp.openPort();
			boolean b = sp.setParams(baudRate, 1, SerialPort.DATABITS_8, SerialPort.PARITY_NONE);
		    Thread.sleep(1000);
		    boolean c = sp.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		    //System.out.println(a+""+b+""+c);
		    connected = true;
		    Thread.sleep(500);
		    ls = new ListenSend(sp, this);
		    sp.writeString("\n");
		    homeAllAxis();
		    //modifyQuickBuffer("M140 S90\n");
		    System.out.println("connected");
		} catch (SerialPortException | InterruptedException e) {
			if(loud)System.out.println("exception");
			disconnect();
			return false;
		}
	    return true;
	}
	public void disconnect(){
		if(printing) abort();
		if(connected)
			try {
				sp.closePort();
			} catch (SerialPortException e) {}
		connected = false;
		ls = null;
		if(loud)System.out.println("disconnected");
	}
	
	//--------SYNCHRONIZED METHODS -------------------
	
	public synchronized String modifyPrintBuffer(String add){
		if(!connected)return null;
		if(add == null){
			try {
				return printBuffer.remove(0);
			}
			catch(IndexOutOfBoundsException e){
				return null;
			}
		}
		else{
			printBuffer.add(add);
			return null;
		}
	}
	public synchronized String modifyQuickBuffer(String add){
		if(!connected)return null;
		if(add == null){
			try {
				return quickBuffer.remove(0);
			}
			catch(IndexOutOfBoundsException e){
				return null;
			}
		}
		else{
			quickBuffer.add(add);
			return null;
		}
	}

	
	//-------------PRINTING CONTROL---------------
	
	public void print(String fileName, double waitTemp) throws IOException{
		printGcodesCount = 0;
		printGcodesSent = 0;
		List<String> lines=Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
		for(String line:lines){
		  String ll = prepareLine(line);
		  if(ll!= null){
			  modifyPrintBuffer(ll);
			  printGcodesCount += 1;
		  }
		}
		paused = false;
		print_finished = false;
		timePrintStarted = System.currentTimeMillis();
		if(waitTemp>0){
			setTargetTemp(waitTemp);
			new TempMonitor(this);
			printing = false;
		}
		else{
			printing = true;
		}
	}
	public void pause(){
		paused = true;
	}
	
	public void unpause(){
		paused = false;
	}
	
	public void abort(){
		printing = false;
		setTargetTemp(0.);
		while(modifyPrintBuffer(null)!=null){} //flush print buffer
		modifyQuickBuffer("G1 X0 Y0 F2000\n");
	}
	//----PRINTER BASIC CONTROL
	public void homeAxis(boolean x, boolean y, boolean z){
		String axis = (x ? "X " : "") + (y ? "Y " : "") + (z ? "Z " : "");
		if(!printing)modifyQuickBuffer("G28 "+axis+"\n");
	}
	public void homeAllAxis(){
		if(!printing)modifyQuickBuffer("G28 \n");
	}
	public void moveTo(double x, double y){
		moveTo(x,y,-1.);
	}
	public void moveTo(double x, double y, double z){
		String X = x!=-1 ? " X"+round(x,2) : "";
		String Y = y!=-1 ? " Y"+round(y,2) : "";
		String Z = z!=-1 ? " Z"+round(z,2) : "";
		if(!printing)modifyQuickBuffer("G1"+X+Y+Z+" F2000. \n");
	}


	//--------TEMPERATURE HANDLING---------
	
	public void setTemp(double t) {//should be used by Listener instance only
		lastMeasuredTemp = t;
	}
	public double getTemp(){
		return lastMeasuredTemp;
	}
	public void setTargetTemp(double t){
		targetTemp = t;
		String tt = Integer.toString((int)t);
		modifyQuickBuffer("M104 S"+tt+"\n");
	}
	public double getTargetTemp(){
		return targetTemp;
	}
	public void reqTempMeasure(){
		modifyQuickBuffer("M105\n");
	}
	
	///######-------HELPERS----------------
	public double computePrintTime(){//returns printing time in seconds
		double x=0.,y=0.,z=0.,f=50.,lastX=0.,lastY=0.,lastZ=0.,lastF = 0.;
		double totalDuration = 0.;
		for(String gCode: printBuffer){
			if(gCode.contains("G1")){
				if(gCode.contains("X")) x = getGcodeArg(gCode, "X");
				if(gCode.contains("Y")) y = getGcodeArg(gCode, "Y");
				if(gCode.contains("Z")) z = getGcodeArg(gCode, "Z");
				if(gCode.contains("F")) f = getGcodeArg(gCode, "F")/60.;
				double moveDuration = 0.;
				double dist = Math.sqrt((x-lastX)*(x-lastX) + (y-lastY)*(y-lastY) + (z-lastZ)*(z-lastZ));
				if(f!=0.)moveDuration = dist/f;
				totalDuration += moveDuration;
				lastX = x;
				lastY = y;
				lastZ = z;
				lastF = f;
			}
		}
		return totalDuration;
	}
	private double getGcodeArg(String gCode, String what){
		int index = gCode.indexOf(what);
		if(index < 0) return 0.;
		String substr = gCode.substring(index+1);
		index = Math.min(substr.indexOf(" "), substr.indexOf("\n"));
		if(index == -1) index = Math.max(substr.indexOf(" "), substr.indexOf("\n"));
		substr = substr.substring(0,index);
		return Double.parseDouble(substr);
		
	}
	private String prepareLine(String line){
		//this prepares the line to be send to printer:
		//cuts the string to the first ';' char <- comment indicator
		int index = line.indexOf(";");
		if(index>=0) line = line.substring(0,index);
		//cut the white spaces at the beginning and at the end of the line
		line = line.trim();
		if(line.length()<=1 || line == null)return null;
		//append new line to the end
		line = line.concat("\n");
		return line;
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
