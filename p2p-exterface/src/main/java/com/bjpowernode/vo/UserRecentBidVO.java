package com.bjpowernode.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 20:54
 */
@Getter
@Setter
@ToString
public class UserRecentBidVO  implements Serializable {
    private String productName;
    private Double bidMoney;
    private String bidTime;
    private String status;
}
