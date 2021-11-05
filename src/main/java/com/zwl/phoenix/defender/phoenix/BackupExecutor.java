package com.zwl.phoenix.defender.phoenix;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.PhoenixBasicBackupConfig;
import com.zwl.phoenix.defender.exception.BackupExecutorException;
import com.zwl.phoenix.defender.phoenix.entity.Column;
import com.zwl.phoenix.defender.phoenix.entity.Table;

/**
 * backup executor
 * @author Future.Zhang
 *
 */
public class BackupExecutor {
	
	static Logger logger=LoggerFactory.getLogger(BackupExecutor.class);
	private static final Integer BATCH_OUTPUT_SIZE=10000;// write to file every 10000 record
	private static final String FIELD_QUOTE="\"";
	private static final String FIELD_SINGLE_QUOTE="'";
	private static final String CHARSET="utf8";

	public static Long backup(Table table, String backupDir) {
		File file=new File(String.format("%s%s%s.sql", backupDir,File.separator, table.getTableName()));
		
		if(PhoenixBasicBackupConfig.overwrite) {
			if(file.exists()) {
				logger.debug(String.format("The backup file [%s] is exists, then override it!",file.getAbsolutePath()));
				
				file.delete();
			}
		}else {
			if(file.exists()) {
				throw new BackupExecutorException(String.format("The backup file [%s] is exists!",file.getAbsolutePath()));
			}
		}
		
		
		String sql=buildSQL(table);
		
		PhoenixClient client=PhoenixClient.getClient();
		Connection conn=null;
		PreparedStatement stats=null;
		ResultSet rs=null;
		
		Long size=0l;
		try {
			conn=client.getConnection();
			stats=conn.prepareStatement(sql);
			rs=stats.executeQuery();
			
			String upsertColumns=buildColumns(table);
			
			StringBuffer buffer=new StringBuffer();
			while(rs.next()) {
				String values=buildValues(table, rs);
				buffer.append("UPSERT INTO ")
				.append(FIELD_QUOTE).append(table.getSchemaName()).append(FIELD_QUOTE)
				.append(".")
				.append(FIELD_QUOTE).append(table.getTableName()).append(FIELD_QUOTE)
				.append("(")
				.append(upsertColumns).append(") VALUES(").append(values).append(");\r\n");
				
				size++;
				
				if(size%BATCH_OUTPUT_SIZE==0) {
					//write to file
					logger.info("Processing : {}", size);
					FileUtils.write(file, buffer.toString(), CHARSET, true);
					buffer.delete(0, buffer.length());
				}
				
				
			}
			
			if(buffer.length()>0) {
				logger.info("Processing : {}", size);
				FileUtils.write(file, buffer.toString(), CHARSET, true);
				buffer.delete(0, buffer.length());
			}
			
		}catch(Exception e) {
			logger.error("",e);
			throw new BackupExecutorException(e);
		}finally {
			PhoenixClient.close(rs);
			PhoenixClient.close(stats);
			PhoenixClient.close(conn);
		}
		
		return size;
	}
	
	private static String buildSQL(Table table) {
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT ")
		   .append(buildColumns(table))
		   .append(" FROM ")
		   .append(FIELD_QUOTE).append(table.getSchemaName()).append(FIELD_QUOTE)
		   .append(".")
		   .append(FIELD_QUOTE).append(table.getTableName()).append(FIELD_QUOTE);
		
		return sql.toString();
	}
	
	private static String buildColumns(Table table) {
		
		List<Column> columns=table.getColumns();
		StringBuffer sql=new StringBuffer();
		for(int i=0;i<columns.size();i++) {
			sql.append(FIELD_QUOTE).append(columns.get(i).getColumnName()).append(FIELD_QUOTE);
			if(i<columns.size()-1) {
				sql.append(",");
			}
		}
		
		return sql.toString();
		
	}
	
	private static String buildValues(Table table , ResultSet rs) throws SQLException {
		StringBuffer values=new StringBuffer();
		int i=0;
		for(Column c:table.getColumns()) {
			values.append(getActualValue(rs, c.getColumnName()));
			if(i<table.getColumns().size()-1) {
				values.append(",");
			}
				
			i++;
		}
		return values.toString();
	}
	
	private static String getActualValue(ResultSet rs, String columnName) throws SQLException {
		Object o=rs.getObject(columnName);
		if(o==null) {
			return null;
		}
		
		if(o instanceof String) {
			//处理字符串中的回车、换行、单引号
			String temp=o.toString();
			temp=temp.replaceAll("\r", "\\\\r");
			temp=temp.replaceAll("\n", "\\\\n");
			temp=temp.replaceAll("'", "''");
			
			return String.format("%s%s%s", FIELD_SINGLE_QUOTE,temp,FIELD_SINGLE_QUOTE);
		}else if(o instanceof Long) {
			return o.toString();
		}else if(o instanceof Double){
			return o.toString();
		}else if(o instanceof Float){
			return o.toString();
		}else if(o instanceof Short){
			return o.toString();
		}else if(o instanceof Integer){
			return o.toString();
		}else if(o instanceof Byte) {
			throw new BackupExecutorException("Unsupport data type [Byte]!");
		}else if(o instanceof Boolean) {
			return o.toString();
		}else if(o instanceof Timestamp) {
			Timestamp t=(Timestamp)o;
			return t.getTime()+"";
		}else if(o instanceof Time) {
			Time t=(Time)o;
			return t.getTime()+"";
		}else if (o instanceof Date) {
			Date t=(Date)o;
			return t.getTime()+"";
		}else if(o instanceof BigDecimal) {
			return o.toString();
		}else if(o instanceof byte[]) {
			throw new BackupExecutorException("Unsupport data type [Byte[]]!");
		}else if(o instanceof Array) {
			throw new BackupExecutorException("Unsupport data type [Array]!");
		}
		
		return null;
	}
	
}
