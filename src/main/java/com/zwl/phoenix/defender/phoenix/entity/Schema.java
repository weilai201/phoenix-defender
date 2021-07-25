package com.zwl.phoenix.defender.phoenix.entity;

import java.util.List;

public class Schema {
	private String schemaName;
	List<Table> tables;
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public List<Table> getTables() {
		return tables;
	}
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

}
