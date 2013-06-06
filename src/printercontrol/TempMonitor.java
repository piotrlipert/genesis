package printercontrol;

//TempMonitor is a thread that monitors the temperature of the hot-end
//when the temperature reaches its target the thread sets <printing> flag and dies

public class TempMonitor extends Thread{
	
	Printcore pc;
	public TempMonitor(Printcore pc){
		this.pc = pc;
		start();
	}
	public void run(){
		while(true){
			if(!pc.connected)break;
			pc.reqTempMeasure();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			if(pc.getTargetTemp() == 0.)break;//targettemp == 0 means printing was aborted
			if(pc.lastMeasuredTemp > pc.targetTemp -5){
				pc.printing = true;
				break;
			}
		}
	}
}
