package com.litian.dancechar.examples.deeppage.job;

import com.litian.dancechar.examples.deeppage.service.CustomerService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@EnableScheduling
public class CustomerCacheHandleJob {

    @Resource
    private CustomerService customerService;

    // @Scheduled(cron = "0 0/10 * * * ?")
    public void handleCustomerDeepPageCache(){
        customerService.handleCustomerDeepPageCache();
    }
}
