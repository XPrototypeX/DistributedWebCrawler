package Impl;

import java.util.concurrent.TimeUnit;

import serializer.Url;

public interface UrlHandler {

	public boolean addNewUrl(Url url);
	
	public Url nextUrl();
	
	public Url nextUrl(int timeout, TimeUnit unit);
	
}
