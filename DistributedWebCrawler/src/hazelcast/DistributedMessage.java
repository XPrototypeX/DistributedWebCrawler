package hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

public class DistributedMessage<E> {

	public final String name; 
	private final ITopic<E> message; 
	
	
	public DistributedMessage(HazelcastInstance hazelInstance, String name) {
		this.name = name; 
		message = hazelInstance.getTopic(name);
	}

	public void setMessageListener(MessageListener<E> listener){
		message.addMessageListener(listener);
	}

	public ITopic<E> getMessage() {
		return message;
	}
}
