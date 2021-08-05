# Phoenix Defender

Phoenix defender is a simple tools to help you backup and restore your data who use apache phoenix in they’s software architecture . It backup data to SQL grammar, such as `UPSERT INTO T(ID,NAME) VALUES(1, 'LUCY');`

# Usage

## Backup Data

First, download `Phoenix defender`, or you can compile it youself.

secondly,  open `{phoenix install dir}/bin`, you may find a file which named `backup.sh.template` and rename it as `backup.sh`.

Then, update parameters in the `backup.sh` file.

Below are the paramters: 

````
# The zkUrl which phoenix defender use to connect to hbase
ZKURL="node3,node5,node6:2181";

# The schema of phoenix you want to backup
SCHEMA="S1";

# The table of phoenix you want to backup. If it's null, then backup all table in the SCHEMA.
TABLE="T1";

# The folder which your want to store your backup's files. 
BACKUP_DIR="/TEMP/"`date +%Y%m%d`


````

Or you can compile by youself. Checkout code and run `mvn clean package -DskipTests` .

Finally, run `backup.sh` to backup your data of phoenix.

## Restore Data

First, download `Phoenix defender`, also you can compile it youself.

secondly,  open `{phoenix install dir}/bin`, you may find a file which named `restore.sh.template` and rename it as `restore.sh`.

Then, update parameters in the `restore.sh` file.

Below are the paramters: 

````
# The zkUrl which phoenix defender use to connect to hbase
ZKURL="node3,node5,node6:2181";

# The schema of phoenix you want to backup
SCHEMA="S1";

````

Or you can compile by youself. Checkout code and run `mvn clean package -DskipTests` .

Finally, run `restore.sh` to restore your data of phoenix.

# TODO

| FUNCTION | STATUS |
|  ----  | ----  |
|Backup Data of table | OK |
|Backup Structure of table | TODO |
|Backup View of schema | TODO |
|Backup Index of table | TODO |
|Backup Sequence | TODO |
|Backup FUNCTION| TODO |
|Restore Data of table | OK |
|Restore Structure of table| TODO|
|Restore View of schema | TODO |
|Restore Index of table | TODO |
|Restore Sequence | TODO |
|Restore FUNCTION| TODO |
|Use Spark to backup and restore data| TODO |


