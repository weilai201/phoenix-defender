package com.zwl.phoenix.defender;

import java.io.File;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zwl.phoenix.defender.phoenix.BackupExecutor;
import com.zwl.phoenix.defender.phoenix.PhoenixClient;
import com.zwl.phoenix.defender.phoenix.entity.Schema;
import com.zwl.phoenix.defender.phoenix.entity.Table;
import com.zwl.phoenix.defender.utils.StringUtil;

/**
 * It is simple tool for backup data of hbase.
 * It use phoenix to backup data.
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
public class PhoenixBasicBackupKit {
	
	static Logger logger=LoggerFactory.getLogger(PhoenixBasicBackupKit.class);

	public static void main(String[] args) {
		logger.info("Start phoenix defender to backup data of phoenix ...");
		initParamters(args);
		initPhonenixClient();
		
		backup();
		
	}
	
	private static void initParamters(String[] args) {
		logger.info("Init input paramers ...");
		CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("", "zkUrl", true, "The url of zookeeper which is used to connect to phoenix" );
        options.addOption("s", "schema", true, "The Schema you want to backup");
        options.addOption("t", "tables", true, "The Tables you want to backup. Multiple tables are separated by commas." );
        options.addOption("o", "output", true, "The path for store backup's files" );
        options.addOption("O", "overwrite", false, "If the backup files is exists, overwrite it" );
        options.addOption("h", "help", false, "Help" );
        
        CommandLine commandLine;
		try {
			commandLine = parser.parse(options,args);
			
			if(commandLine.hasOption("h")) {
				usage(options);
				System.exit(0);
			}
			
			if (commandLine.hasOption("zkUrl")){
				PhoenixBasicBackupConfig.zkUrl=commandLine.getOptionValue("zkUrl");
	        }else {
	        	logger.error("Please input parameter : --zkUrl");
	        	System.exit(1);
	        }
			
			if (commandLine.hasOption("s")){
				PhoenixBasicBackupConfig.schema=commandLine.getOptionValue("s");
	        }else {
	        	logger.error("Please input parameter : --schema");
	        	System.exit(1);
	        }
			
			if (commandLine.hasOption("t")){
				PhoenixBasicBackupConfig.tables=commandLine.getOptionValue("t");
	        }
			
			if (commandLine.hasOption("o")){
				PhoenixBasicBackupConfig.backupDir=commandLine.getOptionValue("o");
	        }else {
	        	logger.error("Please input parameter : --output");
	        	System.exit(1);
	        }
			
			PhoenixBasicBackupConfig.overwrite=false;
			if(commandLine.hasOption("overwrite")) {
				PhoenixBasicBackupConfig.overwrite=true;
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
		formatter.printHelp( "java com.zwl.phoenix.defender.PhoenixBasicBackupKit --zkUrl=localhost:2181 --schema=S1 [--tables=T1,T2] --output=/data/backup/ --overwrite", options );
	}
	
	private static void initPhonenixClient() {
		logger.info("Init phoenix client ...");
		PhoenixClient client=PhoenixClient.getClient();
		client.init(PhoenixBasicBackupConfig.zkUrl);
	}
	
	private static void backup() {
		String schemaName=PhoenixBasicBackupConfig.schema;
		String outputDir=PhoenixBasicBackupConfig.backupDir;
		String tableNames=PhoenixBasicBackupConfig.tables;
		
		String backupDir=null;
		if(outputDir.endsWith(File.separator)) {
			backupDir=String.format("%s%s", outputDir,schemaName);
		}else {
			backupDir=String.format("%s%s%s", outputDir,File.separator,schemaName);
		}
		
		File dir=new File(backupDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		//backup schema
		if(StringUtil.isEmpty(PhoenixBasicBackupConfig.tables)) {
			backupAllTablesOfSchema(schemaName, backupDir);
		}else {// backup a table
			badckupTables(schemaName,tableNames,backupDir);
		}
		
	}
	
	private static void backupAllTablesOfSchema(String schemaName, String backupDir) {
		PhoenixClient client=PhoenixClient.getClient();
		Schema schema=client.getSchema(schemaName);
		if(schema==null) {
			logger.error("The schema [{}] is not exists! ",schemaName);
			System.exit(0);
		}
		
		List<Table> tables=schema.getTables();
		if(tables==null || tables.size()==0) {
			logger.error("There is not table for backup! ");
			System.exit(0);
		}
		
		long beginTime=System.currentTimeMillis();
		logger.info("Begin to backup shchema [{}] data ...",schemaName);
		long size=0;
		for(Table table: tables) {
			size=size+backupTable(table,backupDir);
		}
		long endTime=System.currentTimeMillis();
		logger.info(String.format("Sucessfully backup shchema [%s] data! Data size: %s, Cost: %sms",schemaName,size, (endTime-beginTime)));
	}
	
	private static void badckupTables(String schemaName, String tableNames, String backupDir) {
		
		PhoenixClient client=PhoenixClient.getClient();
		
		String[] arrTables = tableNames.split(",");
		
		for(String talbeName:arrTables) {
			Table table=client.getTable(schemaName, talbeName);
			if(table==null) {
				logger.error("The table [{}] is not exists!",talbeName);
				//System.exit(0);
				continue;
			}
			
			backupTable(table,backupDir);
		}
	}
	
	private static long backupTable(Table table, String backupDir) {
		logger.info("Begin to backup table [{}] data ...",table.getTableName());
		long beginTime=System.currentTimeMillis();
		long size=BackupExecutor.backup(table, backupDir);
		long endTime=System.currentTimeMillis();
		logger.info(String.format("Sucessfully backup table [%s] data! Data size: %s, Cost: %sms",table.getTableName(),size,(endTime-beginTime)));
		
		return size;
	}
	
}
