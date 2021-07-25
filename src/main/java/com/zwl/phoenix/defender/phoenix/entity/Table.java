package com.zwl.phoenix.defender.phoenix.entity;

import java.util.List;

public class Table extends TableBaseInfo{
	
	List<Column> columns;
	Primarykey primaryKey;
	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public Primarykey getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(Primarykey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
}
