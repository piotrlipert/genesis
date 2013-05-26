package printercontrol;


// Base printer control class. Use this class everywhere to send
// commands to the printer.
public class MainPrinterControl 
{
	
	MechanicControl mc;
	FileSender fs;
	PrinterSettings ps;
	ThreadedWait tw;
	// To do in the future
	boolean wifiConnection = false;
	
	String fileNameToPrint;
	
	public void initPrinterControl()
	{
	// This class serves as a hub for all functionalities.
	fs = new FileSender(this);
	mc = new MechanicControl(this);
	ps = new PrinterSettings(this);
	tw = new ThreadedWait(this);
		
	String port = scanPortsForPrinters();
	connectToPrinter(port);
	
		
		
	}

	private void connectToPrinter(String port) {
		if (wifiConnection){}
		else
		{
		}
		
	}

	private String scanPortsForPrinters() 
	{
		if (wifiConnection)
		{
			return null;
		}
		else
		{
			return null;	
		}
		
	}
	
	
	public void sendBufferedCommand(String command)
	{
		// Analogical as in printrun, remember to wait for response.
		// Examples are here https://github.com/reprap/host
		if (wifiConnection){}
		else
		{
		}
		
		
	}
	
	public void sendCommand(String command)
	{
		// Remember to pause FileSender thread for this
		// if there is anything printed at the moment
		if (wifiConnection){}
		else
		{
		}
		
		
	}
	
	
	
	public void printFile()
	{
		mc.homeAllAxis();
		mc.setTemperature(ps.getMaterialTemp());
		// Yo daug i heard you like threads so i run a waiting thread
		// to run a printing thread next.
		
		tw.waitForTemperature(mc.getTargetTemperature());
		tw.run();
		// Printing is started from inside ThreadedWait.
		
		
		
		
	}

	public String getFileToPrint() {
		return fileNameToPrint;
	}
	
}
