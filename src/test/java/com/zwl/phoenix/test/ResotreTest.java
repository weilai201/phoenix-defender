package com.zwl.phoenix.test;

import com.zwl.phoenix.defender.PhoenixBasicRestoreKit;

public class ResotreTest {

	public static void main(String[] args) {
		test1(); 
	}
		
	public static void test1() {
		String[] args=new String[] {"--files=/Users/zhangweilai/TEMP/ZWL/","--zkUrl=node3,node5,node6:2181"};
		PhoenixBasicRestoreKit.main(args);
	}
}
