package demo.common;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;


/**
 * 流量控制, 等级越高，优先级越高
 * 
 * @author Yonee
 * 
 */
public class ThroughputChecker {
	
	 private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(ServerBootstrap.class);

    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();
    
    private static final ThroughputChecker CHECK = new ThroughputChecker();
	// uri对应的tps级别
	private Map<String, Integer> uriTpsLevelMap = new HashMap<String, Integer>();
	// 对应uri当前的tps信息
	private Map<String, Counter> uriTpsCountMap = new HashMap<String, Counter>();
   
	// 采样时间
	private int sampleInterval = 5000;// 5S
	//全局URI
	public static final String ALL_TPS_LIMIT_URL="urls";
	/**
	 * 不受限
	 */
	public static final int CHECK_NORMAL = 0;
	/**
	 * TPS受限中
	 */
	public static final int CHECK_LIMITED = 1;
	/**
	 * 不检查
	 */
	public static final int CHECK_UNCHECK = -1;
	
	private ThroughputChecker() {
	}

	public static ThroughputChecker getThroughputChecker() {
		return CHECK;
	}

	public void load (){
		uriTpsLevelMap.put(ALL_TPS_LIMIT_URL, CONFIG.getIntByKey(ConfigEntry.FRONT_END_FLOWCONTROL_TPS_LIMIT));
		Counter counter = new Counter();
		counter.setSampleInterval(sampleInterval);
		uriTpsCountMap.put(ALL_TPS_LIMIT_URL, counter);
	}
	

	public boolean check(String uri){
		Counter counter= uriTpsCountMap.get(uri);
		if(counter == null){
			return false;
		}
		long currentUriTps = 0l;
		long off = System.currentTimeMillis() - counter.getStartTime();
		if (off > sampleInterval) {
			counter.restLastCount();
			currentUriTps = counter.calNowUriTps(0);
		} else {
			currentUriTps = counter.calNowUriTps(off);
		}
		if (currentUriTps < 0) {
			return true;
		}
		if (currentUriTps == 0) {
			return true;
		}
		long levelLimit = uriTpsLevelMap.get(ALL_TPS_LIMIT_URL);
		if(currentUriTps>levelLimit){
			counter.getUriCount().decrementAndGet();
			LOGGER.error("tps[{}] excess limit[{}], service[{}] permit ,uricount [{}]",currentUriTps,levelLimit, uri,counter.getUriCount());
			return false;
		}else {
			LOGGER.info("tonggggg--------------------------------------------------------[{}]",currentUriTps);
		}
		return true;
	}
	
	
}
