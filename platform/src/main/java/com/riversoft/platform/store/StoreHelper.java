package com.riversoft.platform.store;

import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.platform.store.db.OtherDatabasePools;
import com.riversoft.platform.store.db.OtherDatabasePools.DbHelper;
import com.riversoft.platform.store.redis.RedisPools;
import com.riversoft.platform.store.redis.RedisPools.RedisHelper;

/**
 * 数据存储函数库
 * 
 * @author Chris
 */
@ScriptSupport("store")
public class StoreHelper {

	/**
	 * 默认redis
	 * 
	 * @return
	 */
	public RedisHelper redis() {
		return redis(null);
	}

	/**
	 * 外部redis
	 * 
	 * @param key
	 * @return
	 */
	public RedisHelper redis(String key) {
		return RedisPools.getInstance().getRedis(key);
	}

	/**
	 * 外部数据库函数库
	 * 
	 * @param key
	 * @return
	 */
	public DbHelper db(String key) {
		return OtherDatabasePools.getInstance().getHelper(key);
	}

}
