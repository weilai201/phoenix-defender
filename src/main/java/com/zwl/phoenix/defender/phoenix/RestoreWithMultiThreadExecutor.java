package com.zwl.phoenix.defender.phoenix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.exception.RestoreExecutorException;

/**
 * restore executor
 * @author zhangweilai
 *
 */
public class RestoreWithMultiThreadExecutor {
	static Logger logger=LoggerFactory.getLogger(RestoreWithMultiThreadExecutor.class);
	private static final Integer BATCH_EXECUTE_SIZE=5000;
	private static final String CODE_COMMENT_CHAR="#";
	private static final int DEFAULT_THREAD_SIZE=20;
	private static final int DEFAULT_QUEUE_SIZE=200000;
	
	private static ArrayBlockingQueue<String> queue=new ArrayBlockingQueue<String>(DEFAULT_QUEUE_SIZE);
	
	
	public static Long restore(File file) {
		BufferedReader br=null;
		Long size=0l;
		try {
			
			SimpleRestoreExecutorThreadPool pool=new SimpleRestoreExecutorThreadPool(DEFAULT_THREAD_SIZE,queue);
			pool.start();
			
			br=new BufferedReader(new FileReader(file));
			String sql=br.readLine();//read is much faster then write
			while(sql!=null) {
				if("".equals(sql.trim())) {
					continue;
				}
				
				if(sql.startsWith(CODE_COMMENT_CHAR)) {
					continue;
				}
				
				if(sql.endsWith(";")) {
					sql=sql.substring(0,sql.length()-1);
				}
				
				queue.put(sql);
				
				size++;

				if(size%BATCH_EXECUTE_SIZE==0) {
					logger.info("Processing : {}", size);
				}
				
				sql=br.readLine();
			}
			
			//last, stop executors!
			pool.stop();
			
			
		}catch (Exception e) {
			logger.error("",e);
			throw new RestoreExecutorException(e.getMessage());
		}finally{
			IOUtils.closeQuietly(br);
		}
			
			
		
			
		return size;
	}
	
}