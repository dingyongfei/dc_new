package com.litian.dancechar.examples.deeppage.dto;

import com.litian.dancechar.framework.common.base.BasePage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 客户请求对象
 *
 * @author tojson
 * @date 2022/7/9 06:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerReqDTO extends BasePage implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工号-查询条件
     */
    private String no;

    /**
     * 最后记录的Id
     */
    private Long maxId;

    /**
     * 页号
     */
    private Integer pageNo;

    /**
     * 每页显示大小
     */
    private Integer pageSize;

}