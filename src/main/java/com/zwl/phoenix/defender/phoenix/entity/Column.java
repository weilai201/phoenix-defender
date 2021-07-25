package com.zwl.phoenix.defender.phoenix.entity;

public class Column {
	private String columnName;
	private String dataType;
	private Boolean nullAble;
	private Integer orderby;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Boolean getNullAble() {
		return nullAble;
	}
	public void setNullAble(Boolean nullAble) {
		this.nullAble = nullAble;
	}
	public Integer getOrderby() {
		return orderby;
	}
	public void setOrderby(Integer orderby) {
		this.orderby = orderby;
	}

}
