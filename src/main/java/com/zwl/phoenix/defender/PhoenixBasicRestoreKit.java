package com.zwl.phoenix.defender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.exception.PhoenixRestoreException;
import com.zwl.phoenix.defender.phoenix.PhoenixClient;
import com.zwl.phoenix.defender.phoenix.RestoreExecutor;

/**
 * It is simple tool for restore data of hbase.
 * It use phoenix to restore data.
 * 
 * Include:
 *   Data of table
 *   
 * Not inculde:
 *   Structure of table
 *   Index of table
 *   view
 *   function
 * 
 * 
 * @author Future.Zhang
 * 
 */
public class PhoenixBasicRestoreKit {
	
	static Logger logger=LoggerFactory.getLogger(PhoenixBasicRestoreKit.class);
	
	public static void main(String[] args) {
		logger.info("Start phoenix defender to restore data of phoenix ...");
		
		initParamters(args);
		initPhonenixClient();
		
		restore();
		
	}
	
	private static void initParamters(String[] args) {
		logger.info("Init input paramers ...");
		CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("", "zkUrl", true, "The url of zookeeper which is used to connect to phoenix" );
        options.addOption("f", "files", true, "The file or directory where the backup's files you want to restore.If it is a file, then restore data of a table. Otherwise restore all data in this directory" );
        options.addOption("h", "help", false, "Help" );
        
        CommandLine commandLine;
		try {
			commandLine = parser.parse(options,args);
			
			if(commandLine.hasOption("h")) {
				usage(options);
				System.exit(0);
			}
			
			if (commandLine.hasOption("zkUrl")){
				PhoenixBasicRestoreConfig.zkUrl=commandLine.getOptionValue("zkUrl");
	        }else {
	        	logger.error("Please input parameter : --zkUrl");
	        	System.exit(1);
	        }
			
			if (commandLine.hasOption("f")){
				PhoenixBasicRestoreConfig.files=commandLine.getOptionValue("f");
	        }else {
	        	logger.error("Please input parameter : --files");
	        	System.exit(1);
	        }
			
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("Fail to parse parameters",e);
			usage(options);
			System.exit(1);
		}
        
	}
	
	private static void usage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "java com.zwl.phoenix.defender.PhoenixBasicRestoreKit --zkUrl=localhost:2181 --files=/data/backup", options );
	}
	
	private static void initPhonenixClient() {
		logger.info("Init phoenix client ...");
		PhoenixClient client=PhoenixClient.getClient();
		client.init(PhoenixBasicRestoreConfig.zkUrl);
	}
	
	private static void restore() {
		File files=new File(PhoenixBasicRestoreConfig.files);
		if (!files.exists()) {
			throw new PhoenixRestoreException(String.format("The file or directory [%s] is not exists!", PhoenixBasicRestoreConfig.files));
		}
		
		List<File> list=new ArrayList<>();
		//restore a table
		if(files.isFile()) {
			list.add(files);
		}else {
			list.addAll(FileUtils.listFiles(files, new String[] {"SQL","sql"}, false));
		}
		
		if(list.size()==0) {
			logger.warn("There are nothing to restore!");
			System.exit(0);
		}
		
		long sumSize=0;
		for(File file: list) {
			String tableName=getTableNameFromFile(file);
			logger.info("Begin to restore table [{}] data ...",tableName);
			long beginTime=System.currentTimeMillis();
			Long size=RestoreExecutor.restore(file);
			sumSize=sumSize+size;
			long endTime=System.currentTimeMillis();
			logger.info(String.format("Sucessfully restore table [%s] data! Data size: %s, Cost: %sms",tableName,size,(endTime-beginTime)));
		}
		
		logger.info(String.format("successfully restore %s table and restore %s record!", list.size(),sumSize));
	}
	
	private static String getTableNameFromFile(File file) {
		String fileName=file.getName();
		int index=fileName.lastIndexOf(".");
		if(index>0) {
			fileName=fileName.substring(0,index);
		}
		
		return fileName;
	}
}
