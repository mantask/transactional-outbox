# Transactional Outbox

This repo impmenents a transactional outbox in Spring (Java).

To run the example, first start Docker compose:

```
$ docker-compose up -d
```

Next, register Kafka Connector for Debezium:

```
$ cd docker/debezium && ./debezium.sh
```

Finally, build and run Spring Boot application:

```
$ ./gradlew bootRun 
```

Notice the log entries for (1) saving an event to outbox and (2) later receiving it via Kafka.