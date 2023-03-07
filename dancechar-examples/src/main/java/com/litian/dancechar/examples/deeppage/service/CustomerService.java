package com.litian.dancechar.examples.deeppage.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litian.dancechar.examples.deeppage.cache.CustomerCache;
import com.litian.dancechar.examples.deeppage.dao.entity.CustomerDO;
import com.litian.dancechar.examples.deeppage.dao.inf.CustomerDao;
import com.litian.dancechar.examples.deeppage.dto.CustomerDTO;
import com.litian.dancechar.examples.deeppage.dto.CustomerReqDTO;
import com.litian.dancechar.examples.deeppage.dto.CustomerRespDTO;
import com.litian.dancechar.framework.common.util.DCBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 客户服务
 *
 * @author tojson
 * @date 2022/7/9 6:30
 */
@Service
@Slf4j
public class CustomerService extends ServiceImpl<CustomerDao, CustomerDO> {
    @Resource
    private CustomerDao customerDao;
    @Resource
    private CustomerCache customerCache;

    /**
     * 默认的分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 500;
    /**
     * 页号
     */
    public static final int DEFAULT_PAGE_NO = 1;

    public CustomerRespDTO findListWithDeepPage(CustomerReqDTO customer) {
        List<CustomerDO> customerList = customerDao.findListWithDeepPage(customer.getMaxId(), customer.getNo(),
                                        customer.getPageSize());
        if(CollUtil.isNotEmpty(customerList)){
            CustomerRespDTO customerRespDTO = new CustomerRespDTO();
            customerRespDTO.setPageNo(customer.getPageNo());
            customerRespDTO.setPageSize(customer.getPageSize());
            customerRespDTO.setMaxId(customerList.get(customerList.size()-1).getId());
            customerRespDTO.setCustomerDTOList(DCBeanUtil.copyList(customerList, CustomerDTO.class));
            return customerRespDTO;
        }
        return null;
    }

    public void handleCustomerDeepPageCache(){
        try{
            List<CustomerDO> customerList = customerDao.findListWithDeepPage(0L, null, DEFAULT_PAGE_SIZE);
            if(CollUtil.isEmpty(customerList)){
                return;
            }
            while(customerList.size() >= DEFAULT_PAGE_SIZE){
                customerCache.batchAddCache(customerList);
                long start = System.currentTimeMillis();
                customerList = customerDao.findListWithDeepPage(
                                customerList.get(customerList.size()-1).getId(), null, DEFAULT_PAGE_SIZE);
                log.info("当前耗时:{}ms", System.currentTimeMillis()-start);
            }
            if(CollUtil.isNotEmpty(customerList)){
                customerCache.batchAddCache(customerList);
            }
        } catch (Exception e){
            log.error("处理客户深度分页异常！errMsg:{}", e.getMessage(), e);
        }
    }
}