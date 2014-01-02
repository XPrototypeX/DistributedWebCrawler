package helper;

import java.io.Serializable;
import java.util.List;

import serializer.Url;

/**
 * The returning Record of a CrawlerTask mainUrl from which the given urls of
 * the site were parsed the clusterInfo is a simple String containing
 * informations about the cluster instance.
 * 
 * @author Moritz
 * 
 */

public final class CrawlerRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Url mainUrl;
	private final String clusterInfo;
	private final List<Url> urls;

	public CrawlerRecord(Url mainUrl, String clusterInfo, List<Url> urls) {
		this.mainUrl = mainUrl;
		this.clusterInfo = clusterInfo;
		this.urls = urls;
	}

	public String getClusterInfo() {
		return clusterInfo;
	}

	public Url getLink() {
		return mainUrl;
	}

	public List<Url> getUrls() {
		return urls;
	}

}
