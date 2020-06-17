package ycya.xngc.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;


public class TaskDeamon implements Runnable{

	

	private final Logger logger = Logger.getLogger(getClass());
	private Map<String,Thread> threadMap = new ConcurrentHashMap<String,Thread>();
	private boolean bRun = true;
	@Override
	public void run() {
		logger.info("Deamon线程开始运行...");
		long start = System.currentTimeMillis();
		while(bRun){
			try {
				for(Map.Entry<String,Thread> entry:threadMap.entrySet()){
					Thread t = entry.getValue();
					if(!t.isAlive()){
						String className = entry.getKey();
						logger.error(String.format("线程%s意外终止", className));
						
						Runnable runnable = (Runnable)Class.forName(className).newInstance();
						Thread rc = new Thread(runnable,runnable.getClass().getName());
    					rc.setUncaughtExceptionHandler(new TheadTaskExceptionHandler());
    		            rc.start();
    		            
    		            threadMap.put(className, rc);
    		            logger.error(String.format("线程%s重新启动完成", className));
					}
				}
				if(System.currentTimeMillis()-start>120_000){//2分钟输出一次日志
					logger.info("Deamon线程运行中");
					start = System.currentTimeMillis();
				}
				
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(), e);
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("Deamon线程结束");
	}

	public void setData(String className,Thread thread){
		threadMap.put(className, thread);
	}
	
	public Thread getAndRemoveData(String className){
		Thread t = threadMap.get(className);
		threadMap.remove(className);
		return t;
	}
	
	public void stop(){
		this.bRun = false;
	}

}
