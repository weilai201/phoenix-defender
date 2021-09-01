package com.zwl.phoenix.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileTest {

	public static void main(String[] args) throws IOException {
		long b1=System.currentTimeMillis();
		BufferedReader br=new BufferedReader(new FileReader("/Users/zhangweilai/TEMP/QM_CK_RESULT/QM_CK_RESULT.sql"));
		
		try {
			String sTemp=br.readLine();
			while(sTemp!=null) {
				sTemp=br.readLine();
			}
		}finally {
			br.close();
		}
		
		long b2=System.currentTimeMillis();
		
		System.out.println("耗时："+(b2-b1));
	}

}
