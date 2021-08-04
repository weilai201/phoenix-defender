package com.zwl.phoenix.defender.phoenix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.zwl.phoenix.defender.exception.PhoenixClientException;
import com.zwl.phoenix.defender.phoenix.entity.Column;
import com.zwl.phoenix.defender.phoenix.entity.Primarykey;
import com.zwl.phoenix.defender.phoenix.entity.Schema;
import com.zwl.phoenix.defender.phoenix.entity.Table;
import com.zwl.phoenix.defender.phoenix.entity.TableBaseInfo;


/**
 * phoenix 客户端
 * @author Future.Zhang
 *
 */
public class PhoenixClient {
	
	static Logger logger=LoggerFactory.getLogger(PhoenixClient.class);
	
	private String zkUrl;
	
	private static PhoenixClient client;
	
	public static PhoenixClient getClient() {
		if(client==null)
			client=new PhoenixClient();
		
		return client;
	}
	
	public void init(String zkUrl) {
		this.zkUrl=zkUrl;
		if(client==null) {
			client=this;
		}
		
	}
	
	public Connection getConnection() {
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
            String jdbcUrl=String.format("jdbc:phoenix:%s", zkUrl);
            return DriverManager.getConnection(jdbcUrl);
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new PhoenixClientException(e);
        }
    }
	
	
	public static void close(Statement pstat) {
		try {
			if(pstat!=null)
				pstat.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void close(Connection conn) {
		try {
			if(conn!=null)
				conn.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void close(ResultSet rs) {
		try {
			if(rs!=null)
				rs.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Schema getSchema(String schemaName) {
		Schema schema=new Schema();
		List<Table> tables=new ArrayList<>();
		
		List<String> tableNames=getTables(schemaName);
		tableNames.forEach(tableName->{
			tables.add(getTable(schemaName, tableName));
		});
		
		schema.setTables(tables);
		
		return schema;
	}
	
	private List<String> getTables(String schemaName){
		String sql="SELECT table_name FROM SYSTEM.CATALOG WHERE table_schem=?  AND table_type='u'";
		
		List<String> result=new ArrayList<>();
		Connection conn=null;
		PreparedStatement stats=null;
		ResultSet rs=null;
		
		try {
			conn=this.getConnection();
			stats=conn.prepareStatement(sql);
			
			stats.setString(1, schemaName);
			
			rs=stats.executeQuery();
			
			while(rs.next()) {
				result.add(rs.getString("TABLE_NAME"));
			}
			
			return result;
		}catch(Exception e) {
			logger.error("",e);
			throw new PhoenixClientException(e.getMessage());
		}finally {
			PhoenixClient.close(rs);
			PhoenixClient.close(stats);
			PhoenixClient.close(conn);
		}
		
	}
	
	public Table getTable(String schemaName,String tableName) {
		
		TableBaseInfo baseInfo=getBaseInfoOfTable(schemaName, tableName);
		Table table=new Table();
		table.setPrimaryKeyName(baseInfo.getPrimaryKeyName());
		table.setSaltBuckets(baseInfo.getSaltBuckets());
		table.setSchemaName(schemaName);
		table.setTableName(tableName);
		table.setUpdateCacheFrequency(baseInfo.getUpdateCacheFrequency());
		
		table.setColumns(getColumns(schemaName, tableName));
		Primarykey primarykey=new Primarykey();
		primarykey.setPrimaryKeyName(baseInfo.getPrimaryKeyName());
		primarykey.setColumns(getPrimaryKeyColumns(schemaName, tableName));
		
		table.setPrimaryKey(primarykey);
		
		return table;
	}
	
	private TableBaseInfo getBaseInfoOfTable(String schemaName,String tableName) {
		String sql="SELECT pk_name,salt_buckets,update_cache_frequency FROM SYSTEM.CATALOG WHERE table_schem=? AND table_name=? AND table_type='u'";
		
		Connection conn=null;
		PreparedStatement stats=null;
		ResultSet rs=null;
		
		try {
			conn=this.getConnection();
			stats=conn.prepareStatement(sql);
			
			stats.setString(1, schemaName);
			stats.setString(2, tableName);
			
			rs=stats.executeQuery();
			
			boolean isfirst=rs.next();
			
			if(!isfirst) {
				throw new PhoenixClientException(String.format("Can not found table [%s:%s]",schemaName, tableName));
			}
			
			TableBaseInfo baseInfo=new TableBaseInfo();
			baseInfo.setPrimaryKeyName(rs.getString("PK_NAME"));
			baseInfo.setSaltBuckets(getIntegerFromResultset(rs,"SALT_BUCKETS"));
			baseInfo.setSchemaName(schemaName);
			baseInfo.setTableName(tableName);
			baseInfo.setUpdateCacheFrequency(getLongFromResultset(rs, "UPDATE_CACHE_FREQUENCY"));
			
			return baseInfo;
		}catch(Exception e) {
			logger.error("",e);
			throw new PhoenixClientException(e.getMessage());
		}finally {
			PhoenixClient.close(rs);
			PhoenixClient.close(stats);
			PhoenixClient.close(conn);
		}
		
	}
	
	private Integer getIntegerFromResultset(ResultSet rs, String columnName) throws SQLException {
		String sTemp=rs.getString(columnName);
		if(sTemp==null || "".equals(sTemp)) {
			return null;
		}
		
		return Integer.parseInt(sTemp);
	}
	
	private Long getLongFromResultset(ResultSet rs, String columnName) throws SQLException {
		String sTemp=rs.getString(columnName);
		if(sTemp==null || "".equals(sTemp)) {
			return null;
		}
		
		return Long.valueOf(sTemp);
	}
	
	private List<Column> getColumns(String schemaName,String tableName){
		String sql="SELECT column_name, data_type, nullable,ORDINAL_POSITION  FROM SYSTEM.CATALOG WHERE table_schem=? AND table_name=? AND column_name IS NOT NULL ORDER BY ordinal_position";
		return getColumnsBySQLTemplate(sql, Lists.newArrayList(schemaName,tableName));
	}
	
	private List<Column> getPrimaryKeyColumns(String schemaName,String tableName){
		String sql="SELECT column_name, data_type, nullable,ordinal_position FROM SYSTEM.CATALOG WHERE table_schem=? AND table_name=? AND key_seq IS NOT NULL ORDER BY key_seq";
		
		return getColumnsBySQLTemplate(sql, Lists.newArrayList(schemaName,tableName));
	}
	
	private List<Column> getColumnsBySQLTemplate(String sql,List<Object> parameters){
		List<Column> result=new ArrayList<>();
		
		Connection conn=null;
		PreparedStatement stats=null;
		ResultSet rs=null;
		
		try {
			conn=this.getConnection();
			stats=conn.prepareStatement(sql);
			
			for(int i=1;i<=parameters.size();i++) {
				stats.setObject(i, parameters.get(i-1));
			}
			
			rs=stats.executeQuery();
			
			while(rs.next()) {
				Column column=new Column();
				column.setColumnName(rs.getString("COLUMN_NAME"));
				column.setDataType(rs.getString("DATA_TYPE"));
				column.setNullAble(rs.getInt("NULLABLE")==0?false:true);
				column.setOrderby(rs.getInt("ORDINAL_POSITION"));
				
				result.add(column);
			}
			
			return result;
		}catch(Exception e) {
			logger.error("",e);
			throw new PhoenixClientException(e.getMessage());
		}finally {
			PhoenixClient.close(rs);
			PhoenixClient.close(stats);
			PhoenixClient.close(conn);
		}
	}
	
}
