package com.litian.dancechar.examples.deeppage.controller;

import cn.hutool.core.util.ObjectUtil;
import com.litian.dancechar.examples.deeppage.cache.CustomerCache;
import com.litian.dancechar.examples.deeppage.dto.CustomerReqDTO;
import com.litian.dancechar.examples.deeppage.dto.CustomerRespDTO;
import com.litian.dancechar.examples.deeppage.service.CustomerService;
import com.litian.dancechar.framework.cache.redis.util.RedisHelper;
import com.litian.dancechar.framework.common.base.RespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 客户相关业务处理
 *
 * @author tojson
 * @date 2022/7/9 06:26
 */
@Api(tags = "客户相关api")
@RestController
@Slf4j
@RequestMapping(value = "/customer/")
public class CustomerController {
    @Resource
    private CustomerService customerService;
    @Resource
    private RedisHelper redisHelper;

    @ApiOperation(value = "c端-查询客户分页列表", notes = "c端-查询分页客户列表")
    @PostMapping("listPage")
    public RespResult<CustomerRespDTO> listPage(@RequestBody CustomerReqDTO customerReqDTO){
        Long maxId = customerReqDTO.getMaxId();
        if(customerReqDTO.getPageNo() == null || customerReqDTO.getPageNo() == 1){
            maxId = 0L;
        }
        maxId =(maxId == null ? 0 : maxId);
        customerReqDTO.setMaxId(maxId);
        customerReqDTO.setPageNo(customerReqDTO.getPageNo() == null ? CustomerService.DEFAULT_PAGE_NO :
                customerReqDTO.getPageNo());
        customerReqDTO.setPageSize(customerReqDTO.getPageSize() == null ?
                CustomerService.DEFAULT_PAGE_SIZE: customerReqDTO.getPageSize());
        return RespResult.success(customerService.findListWithDeepPage(customerReqDTO));
    }

    @ApiOperation(value = "刷新客户信息到redis缓存", notes = "刷新客户信息到redis缓存")
    @PostMapping("refreshCustomerCache")
    public RespResult<Void> refreshCustomerCache(){
        customerService.handleCustomerDeepPageCache();
        return RespResult.success();
    }

    @ApiOperation(value = "根据工号获取客户信息", notes = "根据工号获取客户信息")
    @GetMapping("getByNo")
    public RespResult<String> getByNo(@RequestParam("no")Long no){
        if(ObjectUtil.isNull(no)){
            return RespResult.error("工号不能为空");
        }
        return RespResult.success(redisHelper.get(CustomerCache.CUSTOMER_INFO_REDIS_KEY+no));
    }
}