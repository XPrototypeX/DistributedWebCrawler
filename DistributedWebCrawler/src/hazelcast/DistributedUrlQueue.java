package hazelcast;

import java.util.Queue;

import serializer.Url;

import com.hazelcast.core.HazelcastInstance;

public class DistributedUrlQueue {

	private final HazelcastInstance hazelInstance;

	private final Queue<Url> urls;

	public DistributedUrlQueue(HazelcastInstance hazelInstance) {
		this.hazelInstance = hazelInstance;
		this.urls = hazelInstance.getQueue("links");
	}

	public boolean add(Url arg0) {
		return urls.add(arg0);
	}

	public boolean isEmpty() {
		return urls.isEmpty();
	}

	public Url nextLink() {
		return urls.poll();
	}

	public Url remove() {
		return urls.remove();
	}

	public int size() {
		return urls.size();
	}

	public HazelcastInstance getHazelInstance() {
		return hazelInstance;
	}
}
