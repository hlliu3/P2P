package com.bjpowernode.vo;

import com.bjpowernode.model.LoanInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/14  17:02
 */
@Data
public class PageVO<T> implements Serializable {
    private Integer totalPage;
    private Integer totalCount;
    private Integer currentPage;
    private List<T> loanInfoList;
}
