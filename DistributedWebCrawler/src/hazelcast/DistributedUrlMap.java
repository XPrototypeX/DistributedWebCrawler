package hazelcast;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import serializer.Url;

import com.hazelcast.core.HazelcastInstance;

public class DistributedUrlMap {

	private final HazelcastInstance hazelInstance;

	// Key is the top level sites like "www.google.de" value a list a child
	// sites
	private final Map<String, Set<Url>> topLevelUrls;

	public DistributedUrlMap(HazelcastInstance hazelInstance) {
		this.hazelInstance = hazelInstance;
		this.topLevelUrls = hazelInstance.getMap("topLevelUrls");
	}

	public void clear() {
		topLevelUrls.clear();
	}

	public Set<Entry<String, Set<Url>>> entrySet() {
		return topLevelUrls.entrySet();
	}

	public Set<Url> get(Object key) {
		return topLevelUrls.get(key);
	}

	public Set<Url> put(String key, Set<Url> value) {
		return topLevelUrls.put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Set<Url>> m) {
		topLevelUrls.putAll(m);
	}

	public Set<Url> remove(Object key) {
		return topLevelUrls.remove(key);
	}

	public int size() {
		return topLevelUrls.size();
	}

	public HazelcastInstance getHazelInstance() {
		return hazelInstance;
	}
}
