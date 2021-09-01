package com.zwl.phoenix.defender.phoenix;

import java.sql.Connection;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.exception.RestoreExecutorException;

public class PhoenixRestoreExecutorThread extends Thread implements Runnable{

	static Logger logger=LoggerFactory.getLogger(PhoenixRestoreExecutorThread.class);
	
	private static final int BATCH_COMMIT_SIZE=2000;
	private int currentSize=0;
	public static final String STOP_SIGN="STOP";
	private boolean started=false;
	
	
	private ArrayBlockingQueue<String> queue;
	public PhoenixRestoreExecutorThread(ArrayBlockingQueue<String> queue) {
		this.queue=queue;
		String name=UUID.randomUUID().toString();
		super.setName(name);
	}
	
	
	@Override
	public void run() {
		
		started=true;
		
		Connection conn=null;
		Statement stat=null;
		
		try {
			
			conn=PhoenixClient.getClient().getConnection();
			stat=conn.createStatement();
			conn.setAutoCommit(false);
		
			while(true) {
				
				String sql=queue.take();
				
				if(STOP_SIGN.equals(sql)) {
					break;
				}
				currentSize++;
				
				stat.executeUpdate(sql);
				
				if(currentSize%BATCH_COMMIT_SIZE==0) {
					conn.commit();
				}
			}
			
			conn.commit();
			
			logger.info(String.format("%s is stoped!", getName()));
			
		}catch (Exception e) {
			logger.error("",e);
			throw new RestoreExecutorException(e.getMessage());
		}finally {
			started=false;
			PhoenixClient.close(stat);
			PhoenixClient.close(conn);
		}
	}


	public boolean isStarted() {
		return started;
	}


	public void setStarted(boolean started) {
		this.started = started;
	}

}
	
	
	
