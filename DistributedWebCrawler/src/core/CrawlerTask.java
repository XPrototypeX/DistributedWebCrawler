package core;

import helper.CrawlerRecord;

import java.io.Serializable;
import java.util.concurrent.Callable;

import serializer.Url;
import Impl.Filter;
import Impl.Parser;

/**
 * Describes a Crawler Task Crawler Task takes a parser and filter as parameter.
 * Theoretical both can be changed at runtime. For each URL a new Task will be
 * create
 * 
 * the localInstanceName is there for debugging purpose. It displays the cluster
 * from which the URL is crawled
 * 
 * @author root
 * 
 */

public class CrawlerTask implements Callable<CrawlerRecord>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String localInstanceName;
	private final Parser parser;
	private final Filter urlFilter;
	private final Url url;

	public CrawlerTask(Parser parser, Filter filter, Url url,
			String localInstanceName) {

		this.parser = parser;
		this.url = url;
		this.urlFilter = filter;
		this.localInstanceName = localInstanceName;

	}

	/**
	 * 
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      returns a parsed crawler Record or null when the sites was somehow
	 *      invalid
	 */
	@Override
	public CrawlerRecord call() throws Exception {

		CrawlerRecord parsedResult = parser.parseUrl(url, urlFilter,
				localInstanceName);

		return parsedResult;
	}

	public Parser getParser() {
		return parser;
	}

}
