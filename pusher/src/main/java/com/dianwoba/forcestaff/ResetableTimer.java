package com.dianwoba.forcestaff;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 可以进行重置的简易定时器
 * 
 * @author Administrator
 *
 */
public class ResetableTimer {

	private static Logger logger = LoggerFactory.getLogger(ResetableTimer.class);

	private Thread boss;
	private Runnable task;
	private int period;
	private ExecutorService threadPool;
	private long lastTime;

	private volatile boolean running;

	public ResetableTimer(int periodMillisecond) {
		this(periodMillisecond, null);
	}

	public ResetableTimer(int periodMillisecond, Runnable task) {
		this.period = periodMillisecond;
		this.delay(0 - this.period);
		this.setTask(task);
		this.threadPool = Executors.newSingleThreadExecutor();
	}

	public void delay() {
		delay(0);
	}

	public void delay(int delayMillisecond) {
		this.lastTime = System.currentTimeMillis() + delayMillisecond;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}

	public synchronized ResetableTimer start() {
		if (this.boss != null) {
			return this;
		}
		this.running = true;
		this.boss = new Thread(new Runnable() {
			public void run() {
				while (running) {
					long split = System.currentTimeMillis() - lastTime;
					if ((split >= period) && (task != null)) {
						try {
							threadPool.execute(task);
						} catch (Exception e) {
							logger.warn("Timer task error.", e);
						}
						delay();
					}
					try {
						Thread.sleep(split >= period ? period : period - split);
					} catch (InterruptedException e) {
						logger.warn("Thread sleep Interrupted.");
					}
				}
			}
		});
		this.boss.start();
		return this;
	}

	public void stop() throws InterruptedException {
		this.running = false;
		this.boss.join();
		this.boss = null;
		this.threadPool.shutdown();
	}
}
