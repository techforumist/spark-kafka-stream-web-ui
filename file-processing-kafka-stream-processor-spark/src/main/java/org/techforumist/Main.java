package org.techforumist;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.techforumist.domain.StreamRecord;

import com.fasterxml.jackson.annotation.JsonCreator;

import kafka.serializer.StringDecoder;

public class Main {
	public static ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws InterruptedException {

		System.setProperty("hadoop.home.dir", "D:/apple/hadoop/");

		SparkConf conf = new SparkConf().setAppName("Stream Processor Task").setMaster("local[*]");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(2000));

		Map<String, String> kafkaParams = new HashMap<>();
		kafkaParams.put("metadata.broker.list", "localhost:9092");
		Set<String> topics = Collections.singleton("file_stream_input");

		JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc, String.class,
				String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

		directKafkaStream.foreachRDD(rdd -> {
			rdd.foreach(record -> {
				String csvLine = create(record._2()).getData();
				String values[] = csvLine.split(",");

				// System.out.println(values);
				for (int i = 0; i < values.length; i++) {
					System.out.print(values[i] + "\t");
				}
				System.out.println();
			});
		});

		ssc.start();
		ssc.awaitTermination();
	}

	@JsonCreator
	public static StreamRecord create(String jsonString) {
		StreamRecord pc = null;
		try {
			pc = objectMapper.readValue(jsonString, StreamRecord.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pc;
	}
}
