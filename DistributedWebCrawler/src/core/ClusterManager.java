package core;

import hazelcast.Index;
import helper.CrawlerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import serializer.Url;
import Impl.Filter;
import Impl.Parser;
import Impl.ResultHandler;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IExecutorService;

/**
 * Class for managing threads on a cluster. Firstly it checks the number of
 * processors available one the machine then creates exactly as much task as
 * processors. When all threads are finished the result will be added to the
 * index The class manages itself. Means it creates a Thread to managing the
 * cluster. It will run until the executorservice is terminated
 * 
 * It is possible to stop the manager for the instance
 * 
 * Idea: Hazelcast has an own ExecutorService. It is worth a try to use that
 * executor instead of the java standart api one. Problem: how to creates as
 * much threads as cores available
 * 
 * 
 * @author Moritz
 * 
 */

public class ClusterManager implements ResultHandler<CrawlerRecord>, Runnable {

	// Static constants and their declaration
	public static final int N_THREADS;

	/**
	 * Numbers that may be interesting to know those are strings which get
	 * global atmoic Longs for example the client can use those numbers to get
	 * all currently working threads or clusters.
	 */
	public static final String NAME_ATMOIC_N_CLUSTERS;
	public static final String NAME_ATMOIC_N_TASKS;

	static {
		N_THREADS = Runtime.getRuntime().availableProcessors();
		NAME_ATMOIC_N_CLUSTERS = "numberClusters";
		NAME_ATMOIC_N_TASKS = "numberTasks";
	}

	public final String instanceName;

	private final ExecutorService executionService;
	private final Filter urlFilter;
	private final Parser urlParser;
	private final Index indexer;
	private final IAtomicLong numberTask;
	private final IAtomicLong numberClusters;

	public ClusterManager(final HazelcastInstance hazelcast, Filter urlFilter,
			Parser urlParser) {
		this.urlFilter = urlFilter;
		this.urlParser = urlParser;
		this.instanceName = hazelcast.getCluster().getLocalMember().toString();
		this.executionService = Executors.newScheduledThreadPool(N_THREADS,
				new CustomThreadFactory());
		/**
		 * TODO test hazelcasts executor service, it has some interesting stuff
		 * executionService = hazelcast.getExecutorService("bla");
		 */
		this.indexer = new Index(hazelcast);
		this.numberClusters = hazelcast.getAtomicLong(NAME_ATMOIC_N_CLUSTERS);
		this.numberTask = hazelcast.getAtomicLong(NAME_ATMOIC_N_TASKS);

		// increment numbers of clusters
		this.numberClusters.incrementAndGet();

		indexer.addNewUrl(new Url(
				"http://de.wikipedia.org/wiki/Sirimavo_Bandaranaike"));

		Thread th = new Thread(this, "ClusterManager " + hazelcast.getName());
		th.start();

	}

	private Future<CrawlerRecord> executeTask(Url currUrl) {
		Future<CrawlerRecord> task = executionService.submit(new CrawlerTask(
				urlParser, urlFilter, currUrl, instanceName));

		return task;
	}

	private List<Future<CrawlerRecord>> createTask() {

		Url currUrl;
		List<Future<CrawlerRecord>> executableTasks = new ArrayList<>(N_THREADS);
		// creates as much threads as processor cores availeble
		for (int i = 0; i < N_THREADS; i++) {
			currUrl = indexer.nextUrl();
			if (currUrl != null) {
				executableTasks.add(executeTask(currUrl));
				// increment number of tasks
				numberTask.incrementAndGet();
			}
		}

		return executableTasks;
	}

	@Override
	public boolean handleResult(Future<CrawlerRecord> future) {
		CrawlerRecord result = getFutureSafely(future);
		if (result != null) {

			// Result is handled task is finished decrement tasks
			numberTask.decrementAndGet();
			System.out.println("Added");
			return indexer.addResult(result);
		}
		return false;
	}

	private void handleResult(List<Future<CrawlerRecord>> tasks) {
		for (Future<CrawlerRecord> future : tasks) {
			handleResult(future);
		}
	}

	/**
	 * Returns a future with the possibility to handle any exception which may
	 * occure while runtime
	 * 
	 * @param future
	 * @return
	 */
	private static <T> T getFutureSafely(Future<T> future) {
		try {
			return future.get(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		return null;

	}

	public synchronized boolean shutdownExecutor(long timeout, TimeUnit unit) {
		try {
			return executionService.awaitTermination(timeout, unit);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		return false;
	}

	public synchronized void shutdownExecutor(boolean instandly) {
		if (instandly) {
			executionService.shutdownNow();
		} else {
			executionService.shutdown();
		}
	}

	@Override
	public void run() {
		while (!executionService.isTerminated()) {
			List<Future<CrawlerRecord>> tasks = createTask();

			handleResult(tasks);
		}

	}

	/**
	 * Simple CustomThreadFactory When creating a thread it will have the
	 * highest Priority available
	 * 
	 * @author root
	 * 
	 */
	class CustomThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			Thread th = new Thread(r);
			th.setPriority(Thread.MAX_PRIORITY);
			return th;
		}

	}

}
