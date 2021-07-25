package com.zwl.phoenix.test;

import com.zwl.phoenix.defender.PhoenixBasicBackupKit;

public class BackupTest {

	public static void test1() {
		String[] args=new String[] {"--schema=QM_CK_RESULT","--output=/Users/zhangweilai/TEMP","--zkUrl=node3,node5,node6:2181","--overwrite"};
		PhoenixBasicBackupKit.main(args);
	}
	
	public static void main(String[] args) {
		test1();
	}
}
