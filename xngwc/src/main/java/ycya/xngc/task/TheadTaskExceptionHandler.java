package ycya.xngc.task;

import org.apache.log4j.Logger;


public class TheadTaskExceptionHandler implements Thread.UncaughtExceptionHandler{
	private final Logger log = Logger.getLogger(TheadTaskExceptionHandler.class);
	  
    public TheadTaskExceptionHandler() {}

	public void uncaughtException(Thread t, Throwable e) { this.log.error(String.format("捕获到线程异常(%d,%s),status:%s,msg,%s", new Object[] {
	  Long.valueOf(t.getId()), e.getClass().getName(), t.getState(), e.getMessage() }), e);
	}
}
