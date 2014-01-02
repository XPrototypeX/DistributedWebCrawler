package crawler;

import hazelcast.DistributedMessage;
import Impl.Filter;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * A simple filter with the ability to change the filter keyword (filterString) at ANY time
 * 
 * @author root
 *
 */

public class SimpleFilter implements MessageListener<String>, Filter {

	public  final static String FILTER_NAME= "simpleFilter";
	private String filterString = "";
	private DistributedMessage<String> message;
	
	
	
	public SimpleFilter(HazelcastInstance hazelInstance) {
		message = new DistributedMessage<String>(hazelInstance, FILTER_NAME);
		message.setMessageListener(this);
	}
	
	
	@Override
	public boolean isValidUrl(String url) {
		if (filterString.isEmpty()) {
			return true;
		}

		if (url.indexOf(filterString) != -1) {
			return true;
		}

		return false;
	}

	/**
	 * if the message changes the message object will inform the filter String
	 * Not sure if the synchronized is really needed however the filter shouldnt change much
	 */
	@Override
	public void onMessage(Message<String> message) {
		synchronized (filterString) {
			filterString = message.getMessageObject();
		}
	}

}