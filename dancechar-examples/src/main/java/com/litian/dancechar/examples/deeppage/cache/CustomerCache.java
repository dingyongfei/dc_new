package com.litian.dancechar.examples.deeppage.cache;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.litian.dancechar.examples.deeppage.dao.entity.CustomerDO;
import com.litian.dancechar.framework.cache.redis.util.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CustomerCache {
    @Resource
    private RedisHelper redisHelper;

    public  static  final String CUSTOMER_INFO_REDIS_KEY = "customer:i:";

    public void batchAddCache(List<CustomerDO> customerList){
        // 每100条批量写入一次，所有的customerList写入完成以后休眠一下，防止对redis cpu造成太大的冲击
        List<List<CustomerDO>> tempCustomerList = Lists.partition(customerList, 100);
        tempCustomerList.forEach(vo->{
            Map<String, Object> dataMap = Maps.newHashMap();
            vo.forEach(k->dataMap.put(CUSTOMER_INFO_REDIS_KEY + k.getNo(), k));
            redisHelper.executeAsyncPipeLinedSetMap(dataMap, 24*3600L, TimeUnit.SECONDS);
        });
        try{
            TimeUnit.MILLISECONDS.sleep(500);
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }
}
