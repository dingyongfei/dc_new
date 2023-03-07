package com.litian.dancechar.examples.deeppage.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 客户返回對象
 *
 * @author tojson
 * @date 2022/7/9 06:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 最大记录的Id
     */
    private long maxId;

    /**
     *
     */
    private List<CustomerDTO> customerDTOList;

    /**
     * 页号
     */
    private Integer pageNo;

    /**
     * 每页显示大小
     */
    private Integer pageSize;
}