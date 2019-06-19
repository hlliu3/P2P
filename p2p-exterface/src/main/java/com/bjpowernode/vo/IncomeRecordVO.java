package com.bjpowernode.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 21:12
 */
@Setter
@Getter
@ToString
public class IncomeRecordVO implements Serializable {
    private String projectName;
    private String incomeTime;
    private Double incomeMoney;
    private Double bidMoney;
    private String status;
}
