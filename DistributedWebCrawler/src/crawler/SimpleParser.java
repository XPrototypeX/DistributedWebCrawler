package crawler;

import helper.CrawlerRecord;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import serializer.Url;
import Impl.Filter;
import Impl.Parser;

public class SimpleParser implements Parser {

	private List<Url> parsedUrls;

	public SimpleParser() {
		parsedUrls = new ArrayList<>();
	}

	/**
	 * Simple parser. Parsed with the Framework : HTMLParser
	 * 
	 * Throws exceptions from time to time the returning result will be Null then
	 * However a big TODO : there could be faster ways to archive this. THis is
	 * just a simple implementation. 
	 * 
	 */

	@Override
	public CrawlerRecord parseUrl(Url link, Filter filter,
			String hazelCastMember) throws Exception {
		try {
			final org.htmlparser.Parser htmlParser = new org.htmlparser.Parser(
					link.getUrl());

			final NodeList tagNodeList = htmlParser
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));

			for (int j = 0; j < tagNodeList.size(); j++) {
				final LinkTag loopLink = (LinkTag) tagNodeList.elementAt(j);
				final String linkStr = loopLink.getLink();

				if (!linkStr.isEmpty()) {
					if (filter.isValidUrl(link.getUrl())) {
						parsedUrls.add(new Url(linkStr));
					}

				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
			return null;
		}

		return new CrawlerRecord(link, hazelCastMember, parsedUrls);
	}
}
