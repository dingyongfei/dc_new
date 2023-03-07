package com.litian.dancechar.examples.deeppage.dao.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.litian.dancechar.framework.common.mybatis.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 客户DO
 *
 * @author tojson
 * @date 2022/7/9 06:18
 */
@Data
@TableName("t_customer")
@EqualsAndHashCode(callSuper = false)
@ExcelIgnoreUnannotated
public class CustomerDO extends BaseDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 工号
     */
    private String no;

    /**
     * 姓名
     */
    private String name;
}