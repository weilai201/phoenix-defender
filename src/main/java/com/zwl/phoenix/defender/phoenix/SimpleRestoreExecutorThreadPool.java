package com.zwl.phoenix.defender.phoenix;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleRestoreExecutorThreadPool {
	
	static Logger logger=LoggerFactory.getLogger(SimpleRestoreExecutorThreadPool.class);

	private int poolSize;
	private Map<String,PhoenixRestoreExecutorThread> executors;
	private ArrayBlockingQueue<String> queue;
	
	public SimpleRestoreExecutorThreadPool(int poolSize,ArrayBlockingQueue<String> queue) {
		
		if(poolSize<=0) {
			poolSize=1;
		}
		
		this.queue=queue;
		this.poolSize=poolSize;
		
		executors=new HashMap<String, PhoenixRestoreExecutorThread>();
		for(int i=0;i<poolSize;i++) {
			PhoenixRestoreExecutorThread executor=new PhoenixRestoreExecutorThread(queue);
			
			String executorName=String.format("RESTORE-THREAD-%s", i);
			executor.setName(executorName);
			executors.put(executorName, executor);
		}
	}
	
	public void start() {
		for(Entry<String, PhoenixRestoreExecutorThread> entry:executors.entrySet()) {
			PhoenixRestoreExecutorThread executor=entry.getValue();
			executor.start();
			logger.info(String.format("%s started!", entry.getKey()));
			
		}
	}
	

	public void stop() {
		int size=queue.size();
		
		try {
			while(size>0) {
				size=queue.size();
				Thread.currentThread().sleep(2000);
				logger.info(String.format("Queue size is %s, Waiting executor been stoped...",size));
			}
			
			//send sign to queue to tell the executor stop!
			for(int i=0;i<poolSize;i++) {
				queue.put(PhoenixRestoreExecutorThread.STOP_SIGN);
			}
			
		
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
	
}
