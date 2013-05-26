package printercontrol;

public class ThreadedWait implements Runnable
{	
	boolean waitTemperature;
	int targetTemp;
	int actualTemp;
	boolean targetTempAchieved;
	MainPrinterControl mpc;
	ThreadedWait(MainPrinterControl maprco)
	{
		mpc = maprco;
	}
	
	
	public void pause()
	{
		
	}
	public void cancel()
	{
		
	}
	
	public void waitForTemperature(int temp)
	{
		targetTemp = temp;
		waitTemperature = true;
		targetTempAchieved = false;

	}
	
	@Override
	public void run() 
	{
		if (waitTemperature)
		{
			actualTemp = mpc.mc.getActualTemperature();
			while(actualTemp != targetTemp)
			{
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			actualTemp = mpc.mc.getActualTemperature();

			}
			
		mpc.fs.prepareFile(mpc.getFileToPrint());
		mpc.fs.run();
		
		
		return;
			
		}
		
		return;
	}

	
}
