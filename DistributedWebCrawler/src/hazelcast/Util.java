package hazelcast;

import serializer.Url;
import serializer.UrlSerializer;

import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.nio.serialization.StreamSerializer;

public class Util {

	private Util() {

	}

	/**
	 * Add here all Serializer
	 * TODO 
	 * doesnt work yet no idea why
	 * 
	 * @param serializationConfig
	 */
	public static void initSerilizer(SerializationConfig serializationConfig) {
		addSerializer(serializationConfig, Url.class, new UrlSerializer());
	}

	public static <T> void addSerializer(
			SerializationConfig serializationConfig,
			Class<? extends T> typeClass, StreamSerializer<T> streamSerializer) {
		SerializerConfig serializerConfig = new SerializerConfig();
		serializerConfig.setTypeClass(typeClass);
		serializerConfig.setImplementation(streamSerializer);
		serializationConfig.addSerializerConfig(serializerConfig);
	}

}
