package com.enjoy.dh;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Assign and seek are generally used to replay the data.
 */
public class ConsumerAssignAndSeekDemo {

    public static void main(String[] args)  {

        final Logger logger = LoggerFactory.getLogger(ProducerCallbackDemo.class);

        String bootstrapServers = "127.0.0.1:9092";
        String topic = "first_topic";


        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
        consumer.assign(Arrays.asList(partitionToReadFrom));
        consumer.seek(partitionToReadFrom, 15L);

        boolean exitFlag = false;
        int limit = 5;
        int recordProcessedTillNow = 0;
        while(!exitFlag) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<String, String> record: records) {
                logger.info("Key: " + record.key() + ", Value: " + record.value());
                logger.info("Partition: " + record.partition() + ", Offset: " + record.offset() + ", Timestamp: " + record.timestamp());
                recordProcessedTillNow++;
                if(recordProcessedTillNow == limit) {
                    exitFlag = true;
                    break;
                }
            }
        }
        consumer.close();

    }

}
