package com.zwl.phoenix.defender.phoenix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.PhoenixBasicRestoreConfig;
import com.zwl.phoenix.defender.exception.RestoreExecutorException;
import com.zwl.phoenix.defender.utils.StringUtil;

/**
 * restore executor
 * @author zhangweilai
 *
 */
public class RestoreExecutor {
	static Logger logger=LoggerFactory.getLogger(RestoreExecutor.class);
	private static final Integer BATCH_EXECUTE_SIZE=2000;
	private static final String CODE_COMMENT_CHAR="#";
	
	public static Long restore(File file) {
		BufferedReader br=null;
		Connection conn=null;
		Statement stat=null;
		Long size=0l;
		try {
			
			conn=PhoenixClient.getClient().getConnection();
			stat=conn.createStatement();
			conn.setAutoCommit(false);
			
			br=new BufferedReader(new FileReader(file));
			String schema=br.readLine();
			
			schema = getRealSchemaInfo(schema);
			//The first line is schema info, like 'UPSERT INTO SECHME.TABLE ';
			String sql=br.readLine();
			
			while(sql!=null) {
				if("".equals(sql.trim())) {
					continue;
				}
				
				if(sql.startsWith(CODE_COMMENT_CHAR)) {
					continue;
				}
				
				/**
				 * 
				 */
				sql = String.format("%s VALUES(%s)", schema, sql);
				
				size++;
				
				stat.executeUpdate(sql);
				
				if(size%BATCH_EXECUTE_SIZE==0) {
					conn.commit();
					logger.info("Processing : {}", size);
				}
				
				sql=br.readLine();
			}
			
			if(size%BATCH_EXECUTE_SIZE!=0) {
				logger.info("Processing : {}", size);
				conn.commit();
			}
			
			
		}catch (Exception e) {
			logger.error("",e);
			throw new RestoreExecutorException(e.getMessage());
		}finally {
			IOUtils.closeQuietly(br);
			PhoenixClient.close(stat);
			PhoenixClient.close(conn);
		}
			
		return size;
	}
	
	private static String getRealSchemaInfo(String oriSchema) {
		if(StringUtil.isEmpty(PhoenixBasicRestoreConfig.targetTableName)) {
			return oriSchema;
		}
		
		String flag = "UPSERT INTO ";
		int first = oriSchema.indexOf(flag);
		int last = oriSchema.indexOf("(",first+flag.length()+1);
		
		String tailString = oriSchema.substring(last);
		
		return String.format("%s%s%s", flag, PhoenixBasicRestoreConfig.targetTableName, tailString);
	}
	
}
