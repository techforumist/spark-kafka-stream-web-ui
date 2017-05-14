package org.techforumist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.techforumist.domain.File;
import org.techforumist.domain.StreamRecord;

import com.fasterxml.jackson.annotation.JsonCreator;

import kafka.serializer.StringDecoder;

/**
 * @author Sarath Muraleedharan
 *
 */
public class StreamProducer {
	public static KafkaProducer<String, String> producer = null;
	public static Properties props;
	public static JavaSparkContext sc;

	public static ObjectMapper objectMapper = new ObjectMapper();
	public static RestTemplate restTemplate = new RestTemplate();
	public static final String apiUrl = "http://localhost:8585/api/fileProcessingStarted";

	@JsonCreator
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("hadoop.home.dir", "D:/apple/hadoop/");
		SparkConf conf = new SparkConf().setAppName("Stream Producer Task").setMaster("local[*]");
		sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000));

		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");
		Set<String> topics = Collections.singleton("file_upload_complete");

		JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

		directKafkaStream.foreachRDD(rdd -> {
			if (producer == null) {
				props = new Properties();
				props.put("bootstrap.servers", "localhost:9092");
				props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				producer = new KafkaProducer<>(props);
			}
			rdd.foreach(record -> {
				try {

					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + record._2());

					File file = create(record._2());
					ResponseEntity<String> response = restTemplate.getForEntity(apiUrl + "/?fileId=" + file.getId(),
							String.class);
					sendMessage(sc, file);
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
		});

		ssc.start();
		ssc.awaitTermination();

	}

	@JsonCreator
	public static File create(String jsonString) {
		File pc = null;
		try {
			pc = objectMapper.readValue(jsonString, File.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pc;
	}

	public static void sendMessage(JavaSparkContext sc, File file) {
		System.out.println("################################################  fileName : " + file);
		JavaRDD<String> textFile = sc.textFile(file.getFilePath());
		textFile.collect().forEach(s -> {
			ProducerRecord<String, String> lineRecord;
			try {
				lineRecord = new ProducerRecord<>("file_stream_input",
						objectMapper.writeValueAsString(new StreamRecord(file.getId(), s)));
				producer.send(lineRecord);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		try {
			ProducerRecord<String, String> lineRecord;
			lineRecord = new ProducerRecord<>("file_stream_input",
					objectMapper.writeValueAsString(new StreamRecord(file.getId(), ">>END<<")));
			producer.send(lineRecord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (producer != null) {
			producer.close();
			producer = null;
		}

	}

}
