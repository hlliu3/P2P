package com.bjpowernode.service.loan;

import com.bjpowernode.Constants;
import com.bjpowernode.mapper.loan.BidInfoMapper;
import com.bjpowernode.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.vo.PageVO;
import com.bjpowernode.vo.UserRecentBidVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  16:46
 */
@Service
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    /**
     * 平台总投资金额
     * @return
     */
    @Override
    public Double queryAllBidMoney() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        BoundValueOperations<Object, Object> boundValueOperations = redisTemplate.boundValueOps(Constants.ALL_BID_MONEY);
        Double allBidMoney = (Double) boundValueOperations.get();
        if(null == allBidMoney){//redis没有缓存，从数据库中查询

            synchronized (this){//解决redis缓存穿透
                allBidMoney = bidInfoMapper.selectAllBidMoney();
                boundValueOperations.set(allBidMoney, 15, TimeUnit.MINUTES);
            }
        }
        return allBidMoney;
    }

    @Override
    public List<Map<String, Object>> queryBidInfoByUid() {

        List<Map<String, Object>> mapList = new ArrayList<>();
        BoundZSetOperations<Object, Object> objectObjectBoundZSetOperations = redisTemplate.boundZSetOps(Constants.BID_TOP);
        Long size = objectObjectBoundZSetOperations.size();
        
        //Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.BID_TOP, 0, 5);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = objectObjectBoundZSetOperations.reverseRangeWithScores(0, 5);
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = typedTuples.iterator();
        while (iterator.hasNext()){
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String value = (String) next.getValue();
            Double score = next.getScore();
            Map<String,Object> resMap = new HashMap<>();
            resMap.put(value, score);
            mapList.add(resMap);
        }
        return mapList;
    }

    @Override
    public PageVO<RechargeRecord> queryRechargeRecordByUidAndPage(Map<String, Object> paramMap) {
        Integer rechargeRecordCount = rechargeRecordMapper.selectAllRechargeRecordCountByUid(paramMap);
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRecentlyRechargeByUid(paramMap);

        Integer pageSize = (Integer) paramMap.get(Constants.PAGE_SIZE);
        Integer currentPage = (Integer) paramMap.get("RealPageCurrent");
        Integer totalPage = rechargeRecordCount%pageSize==0?rechargeRecordCount/pageSize:rechargeRecordCount/pageSize+1;
        PageVO<RechargeRecord> pageVO = new PageVO<>();
        pageVO.setTotalCount(rechargeRecordCount);
        pageVO.setLoanInfoList(rechargeRecordList);
        pageVO.setTotalPage(totalPage);
        pageVO.setCurrentPage(currentPage);

        return pageVO;
    }

    /**
     * 分页查询所有的投资记录
     * @param paramMap
     * @return
     */
    @Override
    public PageVO<UserRecentBidVO> queryBidInfoByUidAndPage(Map<String, Object> paramMap) {
        Integer bidCount = bidInfoMapper.selectAllBidCountByUid(paramMap);
        List<UserRecentBidVO> userRecentBidVOList = bidInfoMapper.selectRecentlyBidInfoByUserId(paramMap);

        Integer pageSize = (Integer) paramMap.get(Constants.PAGE_SIZE);
        Integer currentPage = (Integer) paramMap.get("RealPageCurrent");
        Integer totalPage = bidCount%pageSize==0?bidCount/pageSize:bidCount/pageSize+1;
        PageVO<UserRecentBidVO> pageVO = new PageVO<>();
        pageVO.setTotalCount(bidCount);
        pageVO.setLoanInfoList(userRecentBidVOList);
        pageVO.setTotalPage(totalPage);
        pageVO.setCurrentPage(currentPage);

        return pageVO;
    }

    @Override
    public List<UserRecentBidVO> queryRecentlyBidInfoByUid(Map<String, Object> paramMapRecentBid) {

        return bidInfoMapper.selectRecentlyBidInfoByUserId(paramMapRecentBid);
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoByLoanId( Map<String,Object> paramMap) {
        List<BidInfo> bidInfoList = bidInfoMapper.selectRecentlyBidInfoByLoanId(paramMap);
        return bidInfoList;
    }
}
