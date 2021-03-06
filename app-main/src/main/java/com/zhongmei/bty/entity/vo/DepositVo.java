package com.zhongmei.bty.entity.vo;

import com.zhongmei.bty.basemodule.trade.bean.DepositInfo;
import com.zhongmei.bty.basemodule.trade.entity.TradeDeposit;

import java.io.Serializable;

/**
 * @Date： 2016/7/19
 * @Description:押金操作对象
 * @Version: 1.0
 */
public class DepositVo implements Serializable {

    private DepositInfo mDepositInfo;

    private TradeDeposit mTradeDeposit;

    public DepositInfo getmDepositInfo() {
        return mDepositInfo;
    }

    public void setmDepositInfo(DepositInfo mDepositInfo) {
        this.mDepositInfo = mDepositInfo;
    }

    public TradeDeposit getmTradeDeposit() {
        return mTradeDeposit;
    }

    public void setmTradeDeposit(TradeDeposit mTradeDeposit) {
        this.mTradeDeposit = mTradeDeposit;
    }
}
