package ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

class KafkaDispatcher implements Closeable {

	private KafkaProducer<String, String> producer;

	public KafkaDispatcher() {
		this.producer = new KafkaProducer<>(properties());
	}

	private static Properties properties() {
		var properties = new Properties();

		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.23.142.116:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		return properties;
	}

	void send(String topic, String key, String value) throws InterruptedException, ExecutionException {
		var record = new ProducerRecord<>(topic, key, value);

		Callback callback = (data, ex) -> {
			if (ex != null) {
				ex.printStackTrace();
				return;
			}
			System.out.println("sucesso enviando " + data.topic() + ":::partition " + data.partition() + "/offset "
					+ data.offset() + "/timestamp " + data.timestamp());
		};

		this.producer.send(record, callback).get();
		
	}

	@Override
	public void close() {
		this.producer.close();
		
	}
}
