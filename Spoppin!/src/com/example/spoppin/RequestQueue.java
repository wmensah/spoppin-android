package com.example.spoppin;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.example.spoppin.RequestsAndResponses.Request;

public class RequestQueue {
	private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static Queue<Request> queue;
	
	private static void EnsureInit(){
		if (queue == null)
			queue = new LinkedList<Request>();
	}
	
	public static void Enqueue(Request e){
		EnsureInit();
		queue.add(e);
	}
	
	public static void ProcessQueue(){
		final Runnable requestor = new Runnable(){
			public void run(){
				//TODO Send requests here
//				if (!queue.isEmpty()){
//					Request request = queue.poll();
//					if (request != null){
//						if (request.IsValid())
//							request.Send();							
//					}
//				}
			}
		};
		final ScheduledFuture<?> requestHandle = scheduler.scheduleAtFixedRate(requestor, 5, 2, SECONDS);
		scheduler.schedule(new Runnable(){
			public void run(){
				requestHandle.cancel(true);
			}
		}, 60*60, SECONDS);
	}
}
