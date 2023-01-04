package com.bsi.utils;
import com.bsi.md.agent.datasource.AgDatasourceContainer;
import com.bsi.md.agent.datasource.AgKafkaTemplate;
import lombok.extern.slf4j.Slf4j;
/**
 * Kafka工具类
 */
@Slf4j
public class KafkaUtils {
//    public static KafkaConsumer<String, String> getDefaultKafkaConsumer() {
//        Properties properties = new Properties();
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "139.9.27.37:9092");
//        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "fish");
//        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
//        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//        return new KafkaConsumer<>(properties);
//    }

    /**
     * 根据主题查询数据
     * @param dataSourceId 数据源id
     * @param topic 主题
     */
    public static Object poll(String dataSourceId,String taskId,String topic){
        AgKafkaTemplate template = AgDatasourceContainer.getKafkaDataSource(dataSourceId);
        return template.poll(dataSourceId+"-"+taskId,topic);
    }

    /**
     * 根据主题查询数据
     * @param dataSourceId 数据源id
     * @param topic 主题
     * @param timeOut 超时时间
     */
    public static Object poll(String dataSourceId,String taskId,String topic,long timeOut){
        AgKafkaTemplate template = AgDatasourceContainer.getKafkaDataSource(dataSourceId);
        return template.poll(dataSourceId+"-"+taskId,topic,timeOut);
    }

    /**
     * 手动提交offset
     * @param dataSourceId
     * @param taskId
     * @return
     */
    public static boolean commit(String dataSourceId,String taskId){
        AgKafkaTemplate template = AgDatasourceContainer.getKafkaDataSource(dataSourceId);
        return template.commit(dataSourceId+"-"+taskId);
    }
//    public static void main(String[] args) {
//        try {
//            KafkaConsumer<String, String> consumer = getDefaultKafkaConsumer();
//            consumer.subscribe(Arrays.asList("test","prod"));
//            while (Boolean.TRUE) {
//                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
//                for (ConsumerRecord<String, String> record : records) {
//                    log.info(">>>>>>>>Consumer offset:{}, key:{},value:{}", record.offset(),record.key(), record.value());
//                }
//            }
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }
}
