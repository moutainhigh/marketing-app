package com.zhongmei.bty.basemodule.customer.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @Date：2015-8-28 上午11:59:08
 * @Description: 会员等级
 * @Version: 1.0
 * @see com.zhongmei.bty.entity.bean.order.OrderMemo 替换原始类
 */
@DatabaseTable(tableName = "takeaway_memo")
public class TakeawayMemo extends BaseInfoOld {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public interface $ extends BaseInfoOld.$ {

        /**
         * name
         */
        String memoContent = "memoContent";

        String memo = "memo";

    }

    public static final String RECORD_STATUS_DIRTY = "1";// local data but not
    // upload
    public static final String RECORD_STATUS_UNDIRTY = "0";// local data but
    // uploaded
    public static final String RECORD_STATUS_UNDELETE = "0";// effective record
    public static final String RECORD_STATUS_DELETE = "-1";// invalid record

    @DatabaseField(columnName = "memoContent")
    private String memoContent;
    @DatabaseField
    private String memo;

    public String getMemoContent() {
        return memoContent;
    }

    public void setMemoContent(String memoContent) {
        this.memoContent = memoContent;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
