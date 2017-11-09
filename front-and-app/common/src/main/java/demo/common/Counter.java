package demo.common;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * 计数器及停止状态 <br>
 * Copyright <a href="http://www.erayt.com/" target="_blank">erayt.com</a>(c)<br>
 * 
 * @author Yonee
 * @version Mar 28, 2012
 */
 public final class Counter {
	 
	 private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ServerBootstrap.class);

	/**
	 * 停止状态
	 */
	private final AtomicBoolean stop = new AtomicBoolean(false);
	
	
	/**
	 * 计数器
	 */
	private final AtomicLong uriCount = new AtomicLong(0);
	
	/**
	 * 开始计数时间
	 */
	private  long startTime = System.currentTimeMillis();

	/**
	 * 开始计数时间
	 */
	private final AtomicLong currentUriTps = new AtomicLong(0);

	/***
	 * 
	 * 统计吞吐量范围
	 * 
	 */
	public static final int FLOW_NUM = 5;
	
	/***
	 * 
	 * 统计吞吐量范围
	 * 
	 */
	public  int  nowFlowNum = 0;

	/**
	 * 上一个时间的计数总数
	 */
	private final long [] lastUriTotalCountArr = new long[FLOW_NUM];

	/**
	 * 五个间隔时间段count汇总
	 */
	private long lastUriTotalCount = 0l;
	
	
	private static final byte[] LOCK = new byte[0];
	
	private int sampleInterval;
	

	public AtomicBoolean getStop() {
		return stop;
	}

	public AtomicLong getUriCount() {
		return uriCount;
	}

	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public AtomicLong getCurrentUriTps() {
		return currentUriTps;
	}

	public void setNowFlowNum(int nowFlowNum) {
		this.nowFlowNum = nowFlowNum;
	}

	public void setLastUriTotalCount(long lastUriTotalCount) {
		this.lastUriTotalCount = lastUriTotalCount;
	}
	public void setSampleInterval(int sampleInterval) {
		this.sampleInterval = sampleInterval;
	}
	/**
	 * 
	 * 计算tps共五个区域,
	 * 本方法重置当前计数count,并将上一个时间段的tps赋值
	 * 
	 */
	public  void restLastCount() {
		synchronized (LOCK) {
			LOGGER.debug("开始重置当前计数");
			double off = System.currentTimeMillis() - startTime;
			if(off <sampleInterval){
				return;
			}
			startTime=System.currentTimeMillis();
			if(nowFlowNum < FLOW_NUM){
				lastUriTotalCountArr[nowFlowNum]= uriCount.get();
				lastUriTotalCount = lastUriTotalCount +lastUriTotalCountArr[nowFlowNum];
				nowFlowNum++;
			} else {
				for (int i = 0; i < nowFlowNum-1; i++) {
					lastUriTotalCountArr[i]=lastUriTotalCountArr[i+1];
				}
				lastUriTotalCountArr[nowFlowNum-1]=uriCount.get();
				lastUriTotalCount = lastUriTotalCount -lastUriTotalCountArr[0]+lastUriTotalCountArr[nowFlowNum-1];
			}
			uriCount.set(0);
			LOGGER.debug("重置计数完成,lastUriTotalCount=[{}],nowFlowNum=[{}],startTime=[{}],lastUriTotalCountArr=[{}]", lastUriTotalCount,nowFlowNum,startTime,lastUriTotalCountArr);
		}
	}
	/**
	 * 计算系统当前tps情况
	 * @param off
	 * @return
	 */
	public long calNowUriTps(long off){
		uriCount.incrementAndGet();
		long count = uriCount.get()+lastUriTotalCount;
		long time = off+nowFlowNum*sampleInterval;
		long calNowUriTps= count*1000/time;
	//	LOGGER.debug("当前tps情况,totalcount=[{}],time=[{}],nowFlowNum=[{}],calNowUriTps=[{}],off=[{}]", count,time,nowFlowNum,calNowUriTps,off);
		return calNowUriTps;
	}

}