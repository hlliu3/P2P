package com.bjpowernode.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * MsgVO class
 *
 * @author
 * @date
 */
@Setter
@Getter
@ToString
public class MsgVO implements Serializable {
    private String code;
    private String msg;
}
