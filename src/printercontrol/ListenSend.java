package printercontrol;

import jssc.SerialPort;
import jssc.SerialPortException;

public class ListenSend extends Thread{
	SerialPort sp;
	Printcore pc;
	String buffer;
	boolean loud = true;
	boolean clear = true;
	
	public ListenSend(SerialPort sp,Printcore pc) throws InterruptedException{
		this.sp = sp;
		this.pc = pc;
		buffer = new String();
		start();
	}
	
	private boolean sendNext(){
		String toSend;
		toSend = pc.modifyQuickBuffer(null);
		if(toSend != null){
			if(loud)System.out.println("quickSENT:"+toSend);
			try {
				sp.writeString(toSend);
			} catch (SerialPortException e) {}
			return true;
		}
		if(pc.printing && !pc.paused){
			toSend = pc.modifyPrintBuffer(null);
			if(toSend != null){
				pc.printGcodesCount += 1;
				if(loud)System.out.println("printSENT:"+toSend);
				try {
					sp.writeString(toSend);
				} catch (SerialPortException e) {}
				return true;
			}
		}
		if(pc.printing && pc.printBuffer.size() == 0){
			pc.printing = false;
			pc.print_finished = true;
			pc.timePrintFinished = System.currentTimeMillis();
		}
		return false;
	}
	private boolean receiveNext(){
		String line = getLine();
		if(line != null){
			if(loud)System.out.println("RECV:"+line);
			//if temperature reading in line set temperature in pc
			if(line.contains("T:")){
				double temp = parseTempAck(line);
				pc.setTemp(temp);
				System.out.println(temp+"/"+pc.targetTemp);
			}
			if(line.contains("ok")){
				return true;
			}
		}
		return false;
	}
	
	public void run(){
		while(true){
			if(!pc.connected)break;
			while(!sendNext() && pc.connected)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			while(!receiveNext() && pc.connected)
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
		}
	}
	
	//-------------
	private String getLine(){
		String read;
		try {
			read = sp.readString();
		} catch (SerialPortException e) {
			return null;
		}
		if(read != null)buffer = buffer.concat(read);
		int indx = buffer.indexOf("\n");
		if(indx<0)return null;
		read = buffer.substring(0,indx+1);
		buffer = buffer.substring(indx+1);
		return read;
	}
	private double parseTempAck(String tempAck){
		//ok T:19.7 /0.0 B:21.4 /0.0 @:0
		int index = tempAck.indexOf("T:");
		if(index < 0) return 0.;
		tempAck = tempAck.substring(index+2);
		index = tempAck.indexOf(" ");
		tempAck = tempAck.substring(0,index);
		return Double.parseDouble(tempAck);
	}
}
