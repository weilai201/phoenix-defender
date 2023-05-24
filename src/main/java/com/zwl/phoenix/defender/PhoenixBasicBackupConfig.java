package com.zwl.phoenix.defender;

public class PhoenixBasicBackupConfig {
	public static String zkUrl;
	public static String schema;
	//public static String table;
	public static String tables;//批量备份表。多个表之间使用逗号隔开
	public static String backupDir;
	public static Boolean overwrite;
	
}
