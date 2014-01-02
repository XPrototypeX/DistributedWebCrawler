package core;

import hazelcast.Util;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import crawler.SimpleFilter;
import crawler.SimpleParser;

public class Server {

	public static void main(String[] args) {
		Config config = new Config();

		Util.initSerilizer(config.getSerializationConfig());

		HazelcastInstance hazelInstance = Hazelcast
				.newHazelcastInstance(config);

		ClusterManager clusterManager = new ClusterManager(hazelInstance,
				new SimpleFilter(hazelInstance), new SimpleParser());

	}

}
