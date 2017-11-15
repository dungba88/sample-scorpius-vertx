package org.joo.scorpius.test.vertx;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

public class TestClient {

	public static void main(String[] args) {
		int maxThreads = 20;
		int noConnections = 10000;
		CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
		httpClient.start();
		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		HttpPost request = new HttpPost("http://localhost:8080/msg?name=greet_java");
		String xml = "{\"name\": \"Anh Dung\"}";
        HttpEntity entity = new ByteArrayEntity(xml.getBytes());
        request.setEntity(entity);
        
        CountDownLatch latch = new CountDownLatch(1);
        
        Handler handler = new Handler(latch, noConnections);
        
		long start = System.currentTimeMillis();
		
		for(int i=0; i<noConnections; i++) {
			executor.submit(() -> {
				httpClient.execute(request, handler);
			});
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long elapsed = System.currentTimeMillis() - start;
		long pace = noConnections * 1000 / elapsed;
		
		System.out.println("Elapsed: " + elapsed + "ms");
		System.out.println("Pace: " + pace + " connections/sec");
		System.out.println("Success: " + handler.getCounter());
		System.out.println("Failed: " + handler.getFailed());

		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}
}

class Handler implements FutureCallback<HttpResponse> {

	private AtomicInteger counter = new AtomicInteger(0);
	
	private AtomicInteger failed = new AtomicInteger(0);

	private int noConnections = 0;

	private CountDownLatch latch;

	public Handler(CountDownLatch latch, int noConnections) {
		this.latch = latch;
		this.noConnections = noConnections;
	}

	@Override
	public void completed(HttpResponse result) {
		if (counter.incrementAndGet() + failed.get() == noConnections)
			latch.countDown();
	}

	@Override
	public void failed(Exception ex) {
		if (counter.get() + failed.incrementAndGet() == noConnections)
			latch.countDown();
	}

	@Override
	public void cancelled() {
		if (counter.get() + failed.incrementAndGet() == noConnections)
			latch.countDown();
	}
	
	public int getCounter() {
		return counter.get();
	}
	
	public int getFailed() {
		return failed.get();
	}
}