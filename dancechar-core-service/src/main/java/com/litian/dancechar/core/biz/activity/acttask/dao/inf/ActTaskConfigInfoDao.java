package com.litian.dancechar.core.biz.activity.acttask.dao.inf;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.litian.dancechar.core.biz.activity.acttask.dao.entity.ActTaskConfigInfoDO;
import com.litian.dancechar.core.biz.activity.acttask.dto.ActTaskConfigInfoReqDTO;
import com.litian.dancechar.core.biz.activity.acttask.dto.ActTaskConfigInfoRespDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 活动任务配置信息Dao
 *
 * @author tojson
 * @date 2022/7/9 6:30
 */
@Mapper
public interface ActTaskConfigInfoDao extends BaseMapper<ActTaskConfigInfoDO> {

    List<ActTaskConfigInfoRespDTO> findList(ActTaskConfigInfoReqDTO req);
}