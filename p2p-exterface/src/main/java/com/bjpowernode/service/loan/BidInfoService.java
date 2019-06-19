package com.bjpowernode.service.loan;

import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.vo.PageVO;
import com.bjpowernode.vo.UserRecentBidVO;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  16:45
 */
public interface BidInfoService {
    /**
     * 查询投资总额
     * @return
     */
    Double queryAllBidMoney();
    /**
     * 查询投资信息根据用户
     * @return
     */
    List<Map<String, Object>> queryBidInfoByUid();
    /**
     * 查询最近投资信息根据产品
     * @return
     */
    List<BidInfo> queryRecentlyBidInfoByLoanId( Map<String,Object> paramMap);
    /**
     * 查询最近投资信息根据用户
     * @return
     */
    List<UserRecentBidVO> queryRecentlyBidInfoByUid(Map<String, Object> paramMapRecentBid);
    /**
     * 查询最近投资信息根据分页和用户
     * @return
     */
    PageVO<UserRecentBidVO> queryBidInfoByUidAndPage(Map<String, Object> paramMap);
    /**
     * 查询最近充值信息根据分页和用户
     * @return
     */
    PageVO<RechargeRecord> queryRechargeRecordByUidAndPage(Map<String, Object> paramMap);
}
