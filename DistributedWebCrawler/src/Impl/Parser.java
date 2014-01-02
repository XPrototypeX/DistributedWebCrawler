package Impl;

import serializer.Url;
import helper.CrawlerRecord;

public interface Parser {

	
	public CrawlerRecord parseUrl(Url link, Filter filter, String hazelCastMember) throws Exception;
	
}
