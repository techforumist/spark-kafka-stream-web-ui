package org.techforumist.ui.kafka.producer;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @author Sarath Muraleedharan
 *
 */
public class Sender {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void send(String topic, String message) {
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
		try {
			future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// future.addCallback(new ListenableFutureCallback<SendResult<String,
		// String>>() {
		//
		// @Override
		// public void onSuccess(SendResult<String, String> result) {
		// System.out.println("Message Sent : " + message + " " +
		// result.getRecordMetadata().offset());
		// }
		//
		// @Override
		// public void onFailure(Throwable ex) {
		// ex.printStackTrace();
		// System.out.println("Error While Sending message : " + message);
		// }
		// });
	}
}
