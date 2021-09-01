package com.zwl.phoenix.test;

import com.zwl.phoenix.defender.PhoenixBasicRestoreKit;
import com.zwl.phoenix.defender.PhoenixMultiThreadRestoreKit;

public class ResotreTest {

	public static void main(String[] args) {
		//test1(); 
		test2();
	}
		
	public static void test1() {
		String[] args=new String[] {"--files=/Users/zhangweilai/TEMP/QM_CK_RESULT/QM_CK_RESULT.sql","--zkUrl=node3,node5,node6:2181"};
		PhoenixBasicRestoreKit.main(args);
	}
	
	public static void test2() {
		String[] args=new String[] {"--files=/Users/zhangweilai/TEMP/QM_CK_RESULT/QM_CK_RESULT.sql","--zkUrl=node3,node5,node6:2181"};
		PhoenixMultiThreadRestoreKit.main(args);
	}
	
	
}
