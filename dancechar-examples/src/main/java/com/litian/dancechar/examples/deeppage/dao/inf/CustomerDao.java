package com.litian.dancechar.examples.deeppage.dao.inf;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litian.dancechar.examples.deeppage.dao.entity.CustomerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 客户Dao
 *
 * @author tojson
 * @date 2022/7/9 6:30
 */
@Mapper
public interface CustomerDao extends BaseMapper<CustomerDO> {

    List<CustomerDO> findListWithDeepPage(@Param("id")long id,
                                          @Param("no")String no,
                                          @Param("pageSize") Integer pageSize);
}