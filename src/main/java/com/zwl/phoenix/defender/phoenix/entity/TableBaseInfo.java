package com.zwl.phoenix.defender.phoenix.entity;

public class TableBaseInfo {
	private String schemaName;
	private String tableName;
	private Integer saltBuckets;
	private Long updateCacheFrequency;
	private String primaryKeyName;
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Integer getSaltBuckets() {
		return saltBuckets;
	}
	public void setSaltBuckets(Integer saltBuckets) {
		this.saltBuckets = saltBuckets;
	}
	
	public Long getUpdateCacheFrequency() {
		return updateCacheFrequency;
	}
	public void setUpdateCacheFrequency(Long updateCacheFrequency) {
		this.updateCacheFrequency = updateCacheFrequency;
	}
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}
	
	
}
