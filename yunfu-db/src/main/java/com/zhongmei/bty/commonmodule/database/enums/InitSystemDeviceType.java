package com.zhongmei.bty.commonmodule.database.enums;

import com.zhongmei.yunfu.util.ValueEnum;

/**
 *

 *
 */
public enum InitSystemDeviceType implements ValueEnum<Integer> {

    /**
     * pad
     */
    PAD(1),

    /**
     * ipad
     */
    IPAD(2),

    /**
     * 未知的值
     *
     * @deprecated 为了避免转为enum出错而设置，不应直接使用
     */
    @Deprecated
    __UNKNOWN__;

    private final Helper<Integer> helper;

    private InitSystemDeviceType(Integer value) {
        helper = Helper.valueHelper(value);
    }

    private InitSystemDeviceType() {
        helper = Helper.unknownHelper();
    }

    @Override
    public Integer value() {
        return helper.value();
    }

    @Override
    public boolean equalsValue(Integer value) {
        return helper.equalsValue(this, value);
    }

    @Override
    public boolean isUnknownEnum() {
        return helper.isUnknownEnum();
    }

    @Override
    public void setUnknownValue(Integer value) {
        helper.setUnknownValue(value);
    }

    @Override
    public String toString() {
        return "" + value();
    }

}
