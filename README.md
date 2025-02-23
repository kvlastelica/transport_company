# PA Debezium POC

Aim of this POC is to setup local enviroment with:
    debezium connector
    zookeeper
    kafka
    kafka mirrormaker
    oracle
    logMiner
Changes in the OracleXE Debezium.CUSTOMERS table should be push to Kafka topic.

Files in this repository are based on the files and info provided in:
https://github.com/debezium/debezium-examples.git
https://github.com/debezium/oracle-vagrant-box
https://github.com/royalihasan/dockerized-setup-kafka-connect-oracle-debezium-stack/tree/master
https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-kafka-mirror-maker-tutorial


This image shows comparison between Log and Query based synchronization approach:
![image info](./images/debezium_vs_sp.png)


Arhitecture of this POC solution (log based) is shown below:
![image info](./images/debezium_ea_pa_.png)

## Azure

Go to Azure portal and create:
1) Events Hub Namespase :  padebezium
2) Event Hub :  padebezium/server1.debezium.customers
3) copy event hub SharedAccessKey  to mirror-eventhub.config

In Azure padebezium namespace under Shared access policies find **Connection string–primary key**
and paste it in **kafka-mirror/mirror-eventhub.config**



## Using Oracle

This assumes Oracle is running on localhost
(or is reachable there, e.g. by means of running it within a VM or Docker container with appropriate port configurations).

```shell
# Start the topology as defined in https://debezium.io/documentation/reference/stable/tutorial.html
export DEBEZIUM_VERSION=2.5
docker compose -f docker-compose-oracle_xeinc.yaml up --build

# Enable Oracle LogMiner
cat ./oracle/setup-logminer-11.sh | docker exec -i --user=oracle oracle-xe bash


# Insert test data
cat ./oracle/inventory11xe.sql | docker exec -i oracle-xe sqlplus debezium/dbzuser@//localhost:1521/XE

# Start Kafka MirrorMaker to send messages to Azure Events Hub
docker exec -i kafka bin/kafka-mirror-maker.sh --consumer.config config/source-mirror.config --num.streams 1 --producer.config config/mirror-eventhub.config --whitelist="server1.DEBEZIUM.CUSTOMERS"
```


The Oracle connector can be used to interact with Oracle either using the Oracle LogMiner API or the Oracle XStreams API.

### LogMiner

The connector by default will use Oracle LogMiner.
Adjust the host name of the database server in the `register-oracle-logminer.json` as per your environment.
Then register the Debezium Oracle connector:

```shell
# Start Oracle connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-oracle-logminer.json

# Create a test change record
echo "INSERT INTO customers VALUES (1007, 'John', 'Doe', 'john.doe@example.com');" | docker exec -i oracle-xe sqlplus debezium/dbzuser@//localhost:1521/XE

# Modify other records in the database via Oracle client
docker exec -i oracle-xe sqlplus debezium/dbz@//localhost:1521/XE

# Shut down the cluster
docker-compose -f docker-compose-oracle.yaml down
```

To check the Kafka topics and the messages, go to:
http://localhost:8080


### Production enviroment

There are few possible upgrades when considering moving to production:
1) replace Kafka MirrorMaker 1 with MirrorMaker 2 separate instance
2) LB for Kafka/Zookeper
3) replace message format from JSON to AVRO


 