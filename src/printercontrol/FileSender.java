package printercontrol;

import java.util.ArrayList;

public class FileSender implements Runnable{
	// File sender thread. Takes gcodes and takes them to the printer.
	
	
	// Remember to reinitialize all variables somewhere after the print. 
	public ArrayList<String> commandBuffer = new ArrayList<String>();
	public int commandCount = 0;
	public int commandsSent = 0;
	MainPrinterControl mpc;
	
	FileSender(MainPrinterControl maprco)
	{
		mpc = maprco;
		
	}
	
	
	public void prepareFile(String filename)
	{
		
		commandBuffer = loadFileAsBuffer(filename);
		commandCount = commandBuffer.size();
		
	}
	
	private ArrayList<String> loadFileAsBuffer(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() 
	{
		
		// Send buffered commands here.
	}
	
	
	public void pause()
	{
		
		
	}
	
	
	public void unpause()
	{
		
		
	}

	public synchronized int getProgress()
	{
		
		// Progress should be (commandsSent/commandCount) * 100
		return 0;
		
		
	}
	
	
	
}
