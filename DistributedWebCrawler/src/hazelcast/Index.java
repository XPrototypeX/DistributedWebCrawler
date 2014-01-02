package hazelcast;

import helper.CrawlerRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import serializer.Url;
import Impl.UrlHandler;

import com.hazelcast.core.HazelcastInstance;

/**
 * Class that managed the urls. Means it checks if the given url was already added
 * or returns a new Url which needs to be parsed. The class also saves every
 * parsed URL. The key is the main url name For instance: The key for:
 * http://google.de/example/child will be http://google.de/
 * 
 * It doesnt save the site from which the given URL was parsed. However that
 * should be easy to implement since that kind of information knows a CrawlerRecord
 * 
 * 
 * @author root
 * 
 */
public class Index implements UrlHandler {

	public static final String QUEUE_NAME = "urlQueue";
	public static final String MAP_NAME = "savedUrls";
	public static final String SET_NAME = "checkedUrls";

	private final BlockingQueue<Url> urlQueue;
	private final Map<String, HashSet<Url>> savedUrls;
	private final Set<String> checkedUrls;

	public Index(HazelcastInstance hazelInstance) {
		urlQueue = hazelInstance.getQueue(QUEUE_NAME);
		savedUrls = hazelInstance.getMap(MAP_NAME);
		checkedUrls = hazelInstance.getSet(SET_NAME);
	}

	public boolean addNewUrl(Url url) {
		if (!checkedUrls.contains(url)) {
			return urlQueue.offer(url);
		}

		return false;
	}

	public Url nextUrl() {
		return nextUrl(10, TimeUnit.MILLISECONDS);
	}

	public Url nextUrl(int timeout, TimeUnit unit) {
		try {
			return urlQueue.poll(timeout, unit);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public boolean addResult(CrawlerRecord record) {
		List<Url> urls = record.getUrls();
		checkedUrls.add(record.getLink().getUrl());

		String topLevelUrl;
		HashSet<Url> urlSafe;

		for (Url currUrl : urls) {

			if (!checkedUrls.contains(currUrl.getUrl())) {
				topLevelUrl = getTopLevelUrl(currUrl.getUrl());

				if ((urlSafe = savedUrls.get(topLevelUrl)) != null) {
					urlSafe.add(currUrl);
				} else {
					urlSafe = new HashSet<>();
					urlSafe.add(currUrl);
					savedUrls.put(topLevelUrl, urlSafe);
				}

				urlQueue.offer(currUrl);
			}

		}

		return true;
	}

	/**
	 * Gets the top level url like "www.google.de" in "www.google.de/example"
	 * Also works with urls like http//test.example.de/anotherExample/
	 * 
	 * @param url
	 * @return the top level url or null doesnt check the validness of the url
	 *         itself
	 */
	public static String getTopLevelUrl(String url) {

		int startPoint, endStr;

		startPoint = url.indexOf('.');
		endStr = url.indexOf('/', startPoint);

		// is top level already
		if (endStr == -1) {
			endStr = url.length();
		}
		// each valid url needs at least one "." (site.toplevel.domain)
		if (startPoint == -1) {
			return null;
		}

		return url.substring(0, endStr);
	}
}
