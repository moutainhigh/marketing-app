package com.zhongmei.bty.basemodule.trade.message;

import com.zhongmei.bty.basemodule.orderdish.entity.TradeItemExtraDinner;
import com.zhongmei.bty.basemodule.trade.bean.DinnertableState;

import java.util.List;

/**
 * 封装正餐换桌接口的请求数据
 */
public class TransferDinnertableReq {

    private Long updatorId;
    private String updatorName;
    private Long tradeTableId;
    private Long tradeTableUpdateTime;
    private Long fromTableUpdateTime;
    private Long toTableId;
    private Long toTableUpdateTime;
    private List<DinnertableState> tables;
    private List<TradeItemExtraDinner> tradeItemExtraDinners;

    public Long getUpdatorId() {
        return updatorId;
    }

    public void setUpdatorId(Long updatorId) {
        this.updatorId = updatorId;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public Long getTradeTableId() {
        return tradeTableId;
    }

    public void setTradeTableId(Long tradeTableId) {
        this.tradeTableId = tradeTableId;
    }

    public Long getTradeTableUpdateTime() {
        return tradeTableUpdateTime;
    }

    public void setTradeTableUpdateTime(Long tradeTableUpdateTime) {
        this.tradeTableUpdateTime = tradeTableUpdateTime;
    }

    public Long getFromTableUpdateTime() {
        return fromTableUpdateTime;
    }

    public void setFromTableUpdateTime(Long fromTableUpdateTime) {
        this.fromTableUpdateTime = fromTableUpdateTime;
    }

    public Long getToTableId() {
        return toTableId;
    }

    public void setToTableId(Long toTableId) {
        this.toTableId = toTableId;
    }

    public Long getToTableUpdateTime() {
        return toTableUpdateTime;
    }

    public void setToTableUpdateTime(Long toTableUpdateTime) {
        this.toTableUpdateTime = toTableUpdateTime;
    }

    public List<DinnertableState> getTables() {
        return tables;
    }

    public void setTables(List<DinnertableState> tables) {
        this.tables = tables;
    }

    public List<TradeItemExtraDinner> getTradeItemExtraDinners() {
        return tradeItemExtraDinners;
    }

    public void setTradeItemExtraDinners(List<TradeItemExtraDinner> tradeItemExtraDinners) {
        this.tradeItemExtraDinners = tradeItemExtraDinners;
    }
}
