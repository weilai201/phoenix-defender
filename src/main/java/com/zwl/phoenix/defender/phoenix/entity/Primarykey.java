package com.zwl.phoenix.defender.phoenix.entity;

import java.util.List;

public class Primarykey {
	private String primaryKeyName;
	List<Column> columns;
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
}
