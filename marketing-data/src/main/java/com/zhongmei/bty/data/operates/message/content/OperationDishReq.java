package com.zhongmei.bty.data.operates.message.content;

import com.zhongmei.yunfu.db.entity.dish.TradeItemOperation;

import java.util.List;

/**
 * @Date： 17/8/21
 * @Description:
 * @Version: 1.0
 */
public class OperationDishReq {

    private Long tradeId;
    private List<TradeItemOperation> tradeItemOperations;


    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public List<TradeItemOperation> getTradeItemOperations() {
        return tradeItemOperations;
    }

    public void setTradeItemOperations(List<TradeItemOperation> tradeItemOperations) {
        this.tradeItemOperations = tradeItemOperations;
    }
}
