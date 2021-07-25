# Phoenix Defender

Phoenix defender is a simple tools to help you backup and restore your data who use apache phoenix in they’s software architecture . It backup data to SQL grammar, such as `UPSERT INTO T(ID,NAME) VALUES(1, 'LUCY');`

# How to use

First, download `Phoenix defender`, or you can compile it youself.

secondly,  open `{phoenix install dir}\bin`, you may find a file which named `backup.sh.template`. Ok, rename it as `backup.sh`.

Then, update parameters in the `backup.sh` file.

Below are the paramters: 

````
# The zkUrl which phoenix use to connect to hbase
ZKURL="node3,node5,node6:2181";

# The schema of phoenix you want to backup
SCHEMA="ZWL";

# The table of phoenix you want to backup. If it's null, then backup all table in the SCHEMA.
TABLE="T1";

# The folder which your want to store your backup's files. 
BACKUP_DIR="/Users/zhangweilai/TEMP/"`date +%Y%m%d`

````

# TODO

| FUNCTION | STATUS |
|  ----  | ----  |
|Backup Data of table | OK |
|Backup Structure of table | TODO |
|Backup View of schema | TODO |
|Backup Index of table | TODO |
|Backup Sequence | TODO |
|Backup FUNCTION| TODO |
|Restore Data of table | TODO |
|Restore Structure of table| TODO|
|Restore View of schema | TODO |
|Restore Index of table | TODO |
|Restore Sequence | TODO |
|Restore FUNCTION| TODO |
|Use Spark to backup and restore data| TODO |


