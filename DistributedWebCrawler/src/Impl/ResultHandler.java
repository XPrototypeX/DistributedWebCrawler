package Impl;

import java.util.concurrent.Future;

public interface ResultHandler<T> {
	
	public boolean handleResult(Future<T> future);
	
	
}
