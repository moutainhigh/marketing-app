package com.zhongmei.bty.basemodule.orderdish.cache;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.zhongmei.bty.basemodule.log.RLog;
import com.zhongmei.bty.basemodule.orderdish.entity.DishBrandProperty;
import com.zhongmei.bty.basemodule.orderdish.entity.DishCarte;
import com.zhongmei.bty.basemodule.orderdish.entity.DishCarteDetail;
import com.zhongmei.bty.basemodule.orderdish.entity.DishCarteNorms;
import com.zhongmei.yunfu.db.entity.dish.DishCyclePeriod;
import com.zhongmei.yunfu.db.entity.dish.DishProperty;
import com.zhongmei.yunfu.db.entity.dish.DishPropertyType;
import com.zhongmei.yunfu.db.entity.dish.DishSetmeal;
import com.zhongmei.yunfu.db.entity.dish.DishSetmealGroup;
import com.zhongmei.yunfu.db.entity.dish.DishShop;
import com.zhongmei.bty.basemodule.orderdish.entity.DishUnitDictionary;
import com.zhongmei.yunfu.db.enums.ChildDishType;
import com.zhongmei.yunfu.db.entity.dish.DishBrandType;
import com.zhongmei.yunfu.context.util.DateTimeUtils;
import com.zhongmei.yunfu.context.util.ThreadUtils;
import com.zhongmei.yunfu.orm.DBHelperManager;
import com.zhongmei.yunfu.orm.DatabaseHelper;
import com.zhongmei.yunfu.db.BasicEntityBase;
import com.zhongmei.yunfu.db.enums.Bool;
import com.zhongmei.yunfu.db.enums.DishType;
import com.zhongmei.yunfu.db.enums.PropertyKind;
import com.zhongmei.yunfu.db.enums.SaleType;
import com.zhongmei.yunfu.db.enums.StatusFlag;
import com.zhongmei.bty.commonmodule.util.MemUtil;
import com.zhongmei.yunfu.context.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @version: 1.0
 * @date 2015年7月13日
 */
public class DishCache {

    private static final String TAG = DishCache.class.getSimpleName();
    //临时菜编码 tes：BQL002
    public static final String TEMPDISHCODE = "kry_linshicai";
    //商品数据关键的URI
    private static final Uri S_URI_DISH_SHOP = DBHelperManager.getUri(DishShop.class);
    private static final Uri S_URI_DISH_BRAND_TYPE = DBHelperManager.getUri(DishBrandType.class);

    private static class LazySingletonHolder {
        private static final DishCache INSTANCE = new DishCache();
    }

    public static boolean isInit() {
        return LazySingletonHolder.INSTANCE.inited;
    }

    /**
     * 刷新所有缓存数据
     */
    public synchronized static void refresh() {
        try {
            LazySingletonHolder.INSTANCE._refresh();
        } catch (Exception e) {
            Log.e(TAG, "refresh error!", e);
        }
        LazySingletonHolder.INSTANCE.checkData();
    }

    /**
     * 返回属性分类缓存数据
     */
    public static DataHolder<DishPropertyType> getPropertyTypeHolder() {
        return LazySingletonHolder.INSTANCE.propertyTypeHolder;
    }

    /**
     * 返回属性缓存数据
     */
    public static PropertyHolder getPropertyHolder() {
        return LazySingletonHolder.INSTANCE.propertyHolder;
    }

    /**
     * 返回菜品分类缓存数据
     */
    public static DataHolder<DishBrandType> getDishTypeHolder() {
        return LazySingletonHolder.INSTANCE.dishTypeHolder;
    }

    /**
     * 返回菜品缓存数据
     */
    public static DishHolder getDishHolder() {
        return LazySingletonHolder.INSTANCE.dishHolder;
    }

    /**
     * 返回加料缓存数据
     */
    public static ExtraHolder getExtraHolder() {
        return LazySingletonHolder.INSTANCE.extraHolder;
    }

    /**
     * 返回菜品属性缓存数据
     */
    public static DishPropertyHolder getDishPropertyHolder() {
        return LazySingletonHolder.INSTANCE.dishPropertyHolder;
    }

    /**
     * 返回套餐明细缓存数据
     */
    public static DishSetmealHolder getSetmealHolder() {
        return LazySingletonHolder.INSTANCE.setmealHolder;
    }

    /**
     * 返回套餐分组缓存数据
     */
    public static DataHolder<DishSetmealGroup> getSetmealGroupHolder() {
        return LazySingletonHolder.INSTANCE.setmealGroupHolder;
    }

    /**
     * 返回菜品加料缓存数据
     */
    public static DishExtraHolder getDishExtraHolder() {
        return LazySingletonHolder.INSTANCE.dishExtraHolder;
    }

    /**
     * 返回单位缓存数据
     */
    public static DataHolder<DishUnitDictionary> getUnitHolder() {
        return LazySingletonHolder.INSTANCE.unitHolder;
    }

    /**
     * 返回模版缓存
     */
    public static DishCarteHolder getDishCarteHolder() {
        return LazySingletonHolder.INSTANCE.dishCarteHolder;
    }

    /**
     * 返回模版菜单
     */
    public static DishCarteDetailHolder getDishCarteDetailHolder() {
        return LazySingletonHolder.INSTANCE.dishCarteDetailHolder;
    }

    /**
     * 返回模版人数
     */
    public static DishCarteNormsHolder getDishCarteNormsHolder() {
        return LazySingletonHolder.INSTANCE.dishCarteNormsHolder;
    }

    /**
     * 返回临时菜dishshop
     */
    public static DishShop getTempDishShop() {
        return LazySingletonHolder.INSTANCE.tempDishShop;
    }

    private final DishCyclePeriodHolder dishCyclePeriodHolder;
    private final DishHolder dishHolder;
    private final DishTypeHolder dishTypeHolder;
    private final ExtraHolder extraHolder;
    private final BasicHolder<DishPropertyType> propertyTypeHolder;
    private final PropertyHolder propertyHolder;
    private final DishPropertyHolder dishPropertyHolder;
    private final DishSetmealHolder setmealHolder;
    private final BasicHolder<DishSetmealGroup> setmealGroupHolder;
    private final DishExtraHolder dishExtraHolder;
    private final BasicHolder<DishUnitDictionary> unitHolder;
    private final DishCarteHolder dishCarteHolder;
    private final DishCarteDetailHolder dishCarteDetailHolder;
    private final DishCarteNormsHolder dishCarteNormsHolder;
    //临时菜的dishShop
    private static DishShop tempDishShop;

    private boolean inited = false;

    private DishCache() {
        dishCyclePeriodHolder = new DishCyclePeriodHolder();
        dishHolder = new DishHolder(dishCyclePeriodHolder);
        dishTypeHolder = new DishTypeHolder(dishHolder);
        extraHolder = new ExtraHolder(dishHolder);
        propertyTypeHolder = new BasicHolder<DishPropertyType>(DishPropertyType.class) {
            @Override
            protected List<DishPropertyType> query(DatabaseHelper helper, Dao<DishPropertyType, Long> dao) throws Exception {
                QueryBuilder<DishPropertyType, Long> qb = dao.queryBuilder();
                qb.selectColumns(DishPropertyType.$.id,
                        DishPropertyType.$.statusFlag,
                        DishPropertyType.$.serverCreateTime,
                        DishPropertyType.$.serverUpdateTime,
                        DishPropertyType.$.brandIdenty,
                        DishPropertyType.$.name,
                        DishPropertyType.$.propertyKind,
                        DishPropertyType.$.sort);
                qb.where().eq(DishPropertyType.$.enabledFlag, Bool.YES);
                return qb.orderBy(DishPropertyType.$.sort, true)
                        .orderBy(DishPropertyType.$.id, true)
                        .query();
            }
        };
        propertyHolder = new PropertyHolder();
        dishPropertyHolder = new DishPropertyHolder();
        setmealHolder = new DishSetmealHolder();
        setmealGroupHolder = new BasicHolder<DishSetmealGroup>(DishSetmealGroup.class) {
            @Override
            protected List<DishSetmealGroup> query(DatabaseHelper helper, Dao<DishSetmealGroup, Long> dao) throws Exception {
                return dao.queryBuilder()
                        .selectColumns(DishSetmealGroup.$.id,
                                DishSetmealGroup.$.statusFlag,
                                DishSetmealGroup.$.serverCreateTime,
                                DishSetmealGroup.$.serverUpdateTime,
                                DishSetmealGroup.$.brandIdenty,
                                DishSetmealGroup.$.name,
                                DishSetmealGroup.$.orderMax,
                                DishSetmealGroup.$.orderMin,
                                DishSetmealGroup.$.setmealDishId)
                        .orderBy(DishSetmealGroup.$.sort, true)
                        .orderBy(DishSetmealGroup.$.id, true)
                        .query();
            }
        };
        dishExtraHolder = new DishExtraHolder(setmealHolder);
        unitHolder = new BasicHolder<DishUnitDictionary>(DishUnitDictionary.class) {
            @Override
            protected List<DishUnitDictionary> query(DatabaseHelper helper, Dao<DishUnitDictionary, Long> dao) throws Exception {
                return dao.queryBuilder()
                        .selectColumns(DishUnitDictionary.$.id,
                                DishUnitDictionary.$.statusFlag,
                                DishUnitDictionary.$.serverCreateTime,
                                DishUnitDictionary.$.serverUpdateTime,
                                DishUnitDictionary.$.brandIdenty,
                                DishUnitDictionary.$.name)
                        .orderBy(DishUnitDictionary.$.id, true)
                        .query();
            }
        };

        dishCarteHolder = new DishCarteHolder();
        dishCarteDetailHolder = new DishCarteDetailHolder(dishHolder, dishCarteHolder);
        dishCarteNormsHolder = new DishCarteNormsHolder();
    }

    /**
     * 刷新所有缓存数据
     */
    synchronized void _refresh() throws Exception {
        Log.i(TAG, "refresh...");
        long startTime = System.currentTimeMillis();
        long memUsed = MemUtil.getUsedHeapSizeWithMB();
        DatabaseHelper helper = DBHelperManager.getHelper();
        try {
            _refresh(helper, dishCyclePeriodHolder);
            _refresh(helper, propertyTypeHolder);
            _refresh(helper, propertyHolder);
            _refresh(helper, dishHolder);
            _refresh(helper, dishTypeHolder);
            _refresh(helper, dishPropertyHolder);
            _refresh(helper, setmealHolder);
            _refresh(helper, setmealGroupHolder);
            _refresh(helper, unitHolder);
            _refresh(helper, dishCarteHolder);
            _refresh(helper, dishCarteDetailHolder);
            _refresh(helper, dishCarteNormsHolder);
            initDishLog();
            inited = true;
        } finally {
            DBHelperManager.releaseHelper(helper);
        }
        System.gc();
        Log.i(TAG, "refresh,查询耗时:" + (System.currentTimeMillis() - startTime) + "ms,新增内存:" + (MemUtil.getUsedHeapSizeWithMB() - memUsed) + "MB,总占用内存:"
                + MemUtil.getUsedHeapSizeWithMB() + "MB");
    }

    // 初始化缓存的时候,记录下商品的信息
    private void initDishLog() {
        RLog.i(RLog.DISH_KEY_TAG, "初始化操作, dishHolder 商品size : " + Utils.size(dishHolder.getAll()));
        RLog.i(RLog.DISH_KEY_TAG, "初始化操作, dishTypeHolder 商品中类size : " + Utils.size(dishTypeHolder.getAll()));
    }

    private void _refresh(DatabaseHelper helper, BasicHolder<?> holder) throws Exception {
        holder.unregistenObserver();
        holder.refresh(helper);
        holder.registenObserver();
    }

    /**
     * 数据缓存对象
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static interface DataHolder<T extends BasicEntityBase> {

        /**
         * 刷新缓存数据
         */
        void refresh() throws Exception;

        /**
         * 获取所有对象(包含循环菜品，for商品设置)
         */
        Collection<T> getSetAll();

        /**
         * 返回包含的对象个数
         */
        int getCount();

        /**
         * 获取所有对象
         */
        Collection<T> getAll();

        /**
         * 根据ID获取对象
         */
        T get(Long id);

        /**
         * 返回符合给定过滤规则的对象列表
         */
        List<T> filter(DataFilter<T> filter);

        /**
         * 返回符合后台给定过滤规则的对象列表(包含循环菜品，for设置中的商品设置)
         */
        List<T> filterSet(DataFilter<T> filter);
    }

    /**
     * 数据过滤器
     *
     * @version: 1.0
     * @date 2015年7月13日
     */
    public static interface DataFilter<T extends BasicEntityBase> {

        /**
         * 返回true表示此entity是符合条件的
         */
        boolean accept(T entity);

    }

    /**
     * @version: 1.0
     * @date 2015年7月14日
     */
    private static abstract class AbstractHolder<T extends BasicEntityBase> implements DataHolder<T> {
        @Override
        public synchronized void refresh() throws Exception {
            DatabaseHelper helper = DBHelperManager.getHelper();
            try {
                refresh(helper);
            } finally {
                DBHelperManager.releaseHelper(helper);
            }
        }

        @Override
        public int getCount() {
            return getDatas() == null ? 0 : getDatas().size();
        }

        @Override
        public Collection<T> getAll() {
            return getDatas() == null ? new ArrayList<T>() : getDatas().values();
        }

        @Override
        public Collection<T> getSetAll() {
            return getDatas() == null ? new ArrayList<T>() : getDatas().values();
        }

        @Override
        public T get(Long id) {
            return getDatas() == null ? null : getDatas().get(id);
        }

        @Override
        public List<T> filter(DataFilter<T> filter) {
            List<T> resultList = new ArrayList<T>();
            if (filter == null) {
                resultList.addAll(getAll());
            } else {
                for (T entity : getAll()) {
                    if (filter.accept(entity)) {
                        resultList.add(entity);
                    }
                }
            }
            return resultList;
        }

        @Override
        public List<T> filterSet(DataFilter<T> filter) {
            List<T> resultList = new ArrayList<T>();
            if (filter == null) {
                resultList.addAll(getSetAll());
            } else {
                for (T entity : getSetAll()) {
                    if (filter.accept(entity)) {
                        resultList.add(entity);
                    }
                }
            }
            return resultList;
        }

        protected abstract Map<Long, T> getDatas();

        protected abstract void refresh(DatabaseHelper helper) throws Exception;

    }

    /**
     * @version: 1.0
     * @date 2015年7月13日
     */
    private static abstract class BasicHolder<T extends BasicEntityBase> extends AbstractHolder<T> {

        private final Class<T> classType;
        private final Uri uri;
        private DatabaseHelper.DataChangeObserver observer;
        private Map<Long, T> datas;

        //private final HandlerThread mRefreshThread;
        BasicHolder(Class<T> classType) {
            this.classType = classType;
            uri = DBHelperManager.getUri(classType);
            datas = new LinkedHashMap<Long, T>();
            //modify  20170908 替换provider 监听
            /*mRefreshThread=new HandlerThread(getClass().getSimpleName());
            mRefreshThread.setDaemon(true);
			mRefreshThread.start();
			mRefreshThread.getLooper().toString();*/
        }

        void registenObserver() {
            if (observer == null) {
                //modify  20170908 替换provider 监听
                /*observer = new DataObserver(this,new Handler(mRefreshThread.getLooper()));*/
                observer = new DataObserver(this, uri);
            }
            //BaseApplication.getInstance().getContentResolver().registerContentObserver(uri, true, observer);
            DatabaseHelper.Registry.register(observer);
        }

        void unregistenObserver() {
            if (observer != null) {
                //modify  20170908 替换provider 监听
                //BaseApplication.getInstance().getContentResolver().unregisterContentObserver(observer);
                DatabaseHelper.Registry.unregister(observer);
                observer = null;
            }
        }

        @Override
        protected Map<Long, T> getDatas() {
            return datas;
        }

        @Override
        protected synchronized void refresh(DatabaseHelper helper) throws Exception {
            try {
                Log.i(TAG, classType.getSimpleName() + " refresh...");
                Dao<T, Long> dao = helper.getDao(classType);
                List<T> list = query(helper, dao);
                renewDatas(cache(list));
                Log.i(TAG, classType.getSimpleName() + ".count=" + getCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        protected Map<Long, T> cache(List<T> list) {
            Map<Long, T> map = new LinkedHashMap<Long, T>();
            for (T entity : list) {
                cacheEntity(map, entity);
            }
            return map;
        }

        protected void cacheEntity(Map<Long, T> map, T entity) {
            map.put(entity.getId(), entity);
        }

        protected void renewDatas(Map<Long, T> theNew) {
            datas = theNew;
        }

        protected abstract List<T> query(DatabaseHelper helper, Dao<T, Long> dao) throws Exception;
    }

    /**
     * 菜单循环周期表
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static class DishCyclePeriodHolder extends BasicHolder<DishCyclePeriod> {
        private Map<Long, List<DishCyclePeriod>> mDishCycleMap = new LinkedHashMap<Long, List<DishCyclePeriod>>();//add 20180328 优化循环菜单

        public DishCyclePeriodHolder() {
            super(DishCyclePeriod.class);
        }

        @Override
        protected List<DishCyclePeriod> query(DatabaseHelper helper, Dao<DishCyclePeriod, Long> dao) throws Exception {
            QueryBuilder<DishCyclePeriod, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishCyclePeriod.$.id,
                    DishCyclePeriod.$.statusFlag,
                    DishCyclePeriod.$.serverCreateTime,
                    DishCyclePeriod.$.serverUpdateTime,
                    DishCyclePeriod.$.brandIdenty,
                    DishCyclePeriod.$.dishId,
                    DishCyclePeriod.$.dayNumber,
                    DishCyclePeriod.$.validityStart,
                    DishCyclePeriod.$.validityEnd,
                    DishCyclePeriod.$.periodStart,
                    DishCyclePeriod.$.periodEnd);
            qb.where().eq(DishCyclePeriod.$.statusFlag, StatusFlag.VALID);
            return qb.orderBy(DishCyclePeriod.$.serverUpdateTime, false).query();
        }

        /**
         * 判断指定菜品是否有效
         */
        public boolean isValidDish(Long brandDishId) {
            //根据指定brandDishId获取循环菜品数据
            //modify begin  20180328 优化循环菜单
			/*List<DishCyclePeriod> dishCyclePeriods = new ArrayList<DishCyclePeriod>();
			Collection<DishCyclePeriod> allDishCyclePeriods = getAll();
			for (DishCyclePeriod dishCyclePeriod : allDishCyclePeriods) {
				if (dishCyclePeriod.getDishId().equals(brandDishId)) {
					dishCyclePeriods.add(dishCyclePeriod);
				}
			}*/
            List<DishCyclePeriod> dishCyclePeriods = mDishCycleMap.get(brandDishId);
            //modify end  20180328 优化循环菜单
            //循环菜品数据为0，表示这个菜品没有进入循环菜品设置，那么这个菜一直有效
            if (dishCyclePeriods == null || dishCyclePeriods.isEmpty()) {
                return true;
            } else {
                //遍历指定菜品查询出分时段数据，判断菜品是否有效
                for (DishCyclePeriod dishCyclePeriod : dishCyclePeriods) {
                    if (isValid(dishCyclePeriod)) {
                        return true;
                    }
                }

                return false;
            }
        }

        //add 20180328  start 优化循环菜单
        private void updateDishCyclePeriodMap(List<DishCyclePeriod> list) {
            mDishCycleMap.clear();
            for (DishCyclePeriod dcPeriod : list) {
                if (dcPeriod.getDishId() != null) {
                    List<DishCyclePeriod> cacheList = mDishCycleMap.get(dcPeriod.getDishId());
                    if (cacheList == null) {
                        cacheList = new ArrayList<DishCyclePeriod>(2);
                        cacheList.add(dcPeriod);
                        mDishCycleMap.put(dcPeriod.getDishId(), cacheList);
                    } else {
                        cacheList.add(dcPeriod);
                    }
                }
            }
        }

        @Override
        protected Map<Long, DishCyclePeriod> cache(List<DishCyclePeriod> list) {
            this.updateDishCyclePeriodMap(list);
            return super.cache(list);
        }
        //add 20180328  end 优化循环菜单

        /**
         * 判断当前时间指定菜品是否有效
         */
        private boolean isValid(DishCyclePeriod dishCyclePeriod) {
            long currentTimeMillis = System.currentTimeMillis();
            //判断菜品有效的日期序号是否与今天日期序号相等
            int currentDayNumber = DateTimeUtils.getDayNumber(currentTimeMillis);
            if (!dishCyclePeriod.getDayNumber().equalsValue(currentDayNumber)) {
                return false;
            }

            //判断菜品是否在有效期日期以内，例如 YYYY-MM-DD
            String periodStart = dishCyclePeriod.getPeriodStart();
            String periodEnd = dishCyclePeriod.getPeriodEnd();
            Date onlyDate = DateTimeUtils.onlyDate(new Date());//当天日期
            if (!TextUtils.isEmpty(periodStart) && !TextUtils.isEmpty(periodEnd)) {
                Date periodStartDate = new Date(DateTimeUtils.formatDate(periodStart));
                Date periodEndDate = new Date(DateTimeUtils.formatDate(periodEnd));
                if (periodStartDate.compareTo(onlyDate) > 0 || periodEndDate.compareTo(onlyDate) < 0) {
                    return false;
                }
            } else if (!TextUtils.isEmpty(periodStart)) {
                Date periodStartDate = new Date(DateTimeUtils.formatDate(periodStart));
                if (periodStartDate.compareTo(onlyDate) > 0) {
                    return false;
                }
            } else if (!TextUtils.isEmpty(periodEnd)) {
                Date periodEndDate = new Date(DateTimeUtils.formatDate(periodEnd));
                if (periodEndDate.compareTo(onlyDate) < 0) {
                    return false;
                }
            }

            //判断菜品是否在有效时间段以内,例如hh:mm:ss
            long validityStartTime = DateTimeUtils.getTime(dishCyclePeriod.getValidityStart());
            if (validityStartTime > currentTimeMillis) {
                return false;
            }

            //判断菜品是否在有效时间段以内,例如hh:mm:ss
            long validityEndTime = DateTimeUtils.getTime(dishCyclePeriod.getValidityEnd());
            if (validityEndTime < currentTimeMillis) {
                return false;
            }

            return true;
        }
    }

    public static class DishCarteDetailHolder extends BasicHolder<DishCarteDetail> {
        private final List<OnDataChangeListener> listeners;
        private Map<Long, List<DishCarteDetail>> carteDishNorms;//根据菜单类型缓存套餐模版
        //private Map<String,List<DishShop>> dishShopByCarteId;
        private Map<String, List<DishShop>> dishShopByCarteUuid;
        private Map<String, Map<Long, List<DishShop>>> dishShopByDishType;//根据分类缓存菜品
        private Map<String, Map<Long, Map<String, DishShop>>> dishShopMapByDishType;//根据分类缓存菜品
        private DishHolder mDishHolder;
        private DishCarteHolder mDishCarteHolder;

        public void removeDataChangeListener(OnDataChangeListener listener) {
            listeners.remove(listener);
        }

        DishCarteDetailHolder(DishHolder dishHolder, DishCarteHolder dishCarteHolder) {
            super(DishCarteDetail.class);
            listeners = new CopyOnWriteArrayList<OnDataChangeListener>();
            carteDishNorms = new HashMap<Long, List<DishCarteDetail>>();
            dishShopByCarteUuid = new HashMap<String, List<DishShop>>();
            dishShopByDishType = new HashMap<String, Map<Long, List<DishShop>>>();
            dishShopMapByDishType = new HashMap<String, Map<Long, Map<String, DishShop>>>();
            this.mDishHolder = dishHolder;
            this.mDishCarteHolder = dishCarteHolder;
        }

        /**
         * 根据指定类型的所有菜品信息
         */
        public List<DishShop> getDishShopsByType(String carteUuid, Long dishTypeId) {
            Map<Long, List<DishShop>> listMap = dishShopByDishType.get(carteUuid);
            if (listMap != null) {
                return listMap.get(dishTypeId);
            } else {
                return new ArrayList<>();
            }
        }

        public Map<String, DishShop> getDishShopMapByType(String carteUuid, Long dishTypeId) {
            Map<Long, Map<String, DishShop>> mapDishCarte = dishShopMapByDishType.get(carteUuid);

            if (mapDishCarte != null) {
                return mapDishCarte.get(dishTypeId);
            }

            return null;
        }

        /**
         * 根据UUID来获取菜单
         */
        public List<DishShop> getDishShopsByCarte(String dishCarteUuid) {
            return dishShopByCarteUuid.get(dishCarteUuid);
        }


        @Override
        protected Map<Long, DishCarteDetail> cache(List<DishCarteDetail> list) {
            carteDishNorms.clear();
            dishShopByCarteUuid.clear();
            dishShopByDishType.clear();
            dishShopMapByDishType.clear();

            for (DishCarteDetail dishCarteDetail : list) {
                if (!carteDishNorms.containsKey(dishCarteDetail.getCarteId())) {
                    carteDishNorms.put(dishCarteDetail.getCarteId(), new ArrayList<DishCarteDetail>());
                }
                carteDishNorms.get(dishCarteDetail.getCarteId()).add(dishCarteDetail);

                DishShop dishShop = mDishHolder.get(dishCarteDetail.getDishUuid());

                if (dishShop == null) {
                    continue;
                }
                DishCarte dishCarte = mDishCarteHolder.get(dishCarteDetail.getCarteId());

                if (dishCarte != null) {
                    if (!dishShopByCarteUuid.containsKey(dishCarte.getUuid())) {
                        dishShopByCarteUuid.put(dishCarte.getUuid(), new ArrayList<DishShop>());
                    }

                    dishShopByCarteUuid.get(dishCarte.getUuid()).add(dishShop);

                    Map<Long, List<DishShop>> mapDishShop;
                    if (!dishShopByDishType.containsKey(dishCarte.getUuid())) {
                        dishShopByDishType.put(dishCarte.getUuid(), new HashMap<Long, List<DishShop>>());
                    }

                    mapDishShop = dishShopByDishType.get(dishCarte.getUuid());

                    if (!mapDishShop.containsKey(dishShop.getDishTypeId())) {
                        mapDishShop.put(dishShop.getDishTypeId(), new ArrayList<DishShop>());
                    }

                    mapDishShop.get(dishShop.getDishTypeId()).add(dishShop);

                    Map<Long, Map<String, DishShop>> mapDishCarte;
                    if (!dishShopMapByDishType.containsKey(dishCarte.getUuid())) {
                        dishShopMapByDishType.put(dishCarte.getUuid(), new HashMap<Long, Map<String, DishShop>>());
                    }

                    mapDishCarte = dishShopMapByDishType.get(dishCarte.getUuid());

                    if (!mapDishCarte.containsKey(dishShop.getDishTypeId())) {
                        mapDishCarte.put(dishShop.getDishTypeId(), new HashMap<String, DishShop>());
                    }

                    mapDishCarte.get(dishShop.getDishTypeId()).put(dishShop.getUuid(), dishShop);
                }
            }
            return super.cache(list);
        }

        @Override
        protected List<DishCarteDetail> query(DatabaseHelper helper, Dao<DishCarteDetail, Long> dao) throws Exception {
            QueryBuilder<DishCarteDetail, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishCarteDetail.$.id,
                    DishCarteDetail.$.statusFlag,
                    DishCarteDetail.$.serverCreateTime,
                    DishCarteDetail.$.serverUpdateTime,
                    DishCarteDetail.$.dishId,
                    DishCarteDetail.$.num,
                    DishCarteDetail.$.carteId,
                    DishCarteDetail.$.dishUUid,
                    DishCarteDetail.$.setmealGroupId,
                    DishCarteDetail.$.mealId,
                    DishCarteDetail.$.updateId,
                    DishCarteDetail.$.createId);
            qb.where().eq(DishCarte.$.statusFlag, StatusFlag.VALID);
            return qb.query();
        }
    }


    public static class DishCarteNormsHolder extends BasicHolder<DishCarteNorms> {
        private final List<OnDataChangeListener> listeners;
        private Map<Long, List<DishCarteNorms>> carteTypeDishNorms;//根据菜单类型缓存套餐模版

        public void removeDataChangeListener(OnDataChangeListener listener) {
            listeners.remove(listener);
        }

        DishCarteNormsHolder() {
            super(DishCarteNorms.class);
            listeners = new CopyOnWriteArrayList<OnDataChangeListener>();
            carteTypeDishNorms = new HashMap<Long, List<DishCarteNorms>>();
        }


        public List<DishCarteNorms> getDishCarteNormsByCarteId(Long carteId) {
            return carteTypeDishNorms.get(carteId);
        }


        @Override
        protected Map<Long, DishCarteNorms> cache(List<DishCarteNorms> list) {
            carteTypeDishNorms.clear();

            for (DishCarteNorms dishCarteNorms : list) {
                if (!carteTypeDishNorms.containsKey(dishCarteNorms.getCarteId())) {
                    carteTypeDishNorms.put(dishCarteNorms.getCarteId(), new ArrayList<DishCarteNorms>());
                }
                carteTypeDishNorms.get(dishCarteNorms.getCarteId()).add(dishCarteNorms);
            }

            return super.cache(list);
        }

        @Override
        protected List<DishCarteNorms> query(DatabaseHelper helper, Dao<DishCarteNorms, Long> dao) throws Exception {
            QueryBuilder<DishCarteNorms, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishCarteNorms.$.id,
                    DishCarteNorms.$.statusFlag,
                    DishCarteNorms.$.serverCreateTime,
                    DishCarteNorms.$.serverUpdateTime,
                    DishCarteNorms.$.name,
                    DishCarteNorms.$.price,
                    DishCarteNorms.$.carteId,
                    DishCarteNorms.$.updateId,
                    DishCarteNorms.$.createId);
            qb.where().eq(DishCarte.$.statusFlag, StatusFlag.VALID);
            return qb.query();
        }
    }

    public static class DishCarteHolder extends BasicHolder<DishCarte> {
        private final List<OnDataChangeListener> listeners;
        private Map<Integer, List<DishCarte>> carteTypeDish;//根据菜单类型缓存套餐模版

        public void removeDataChangeListener(OnDataChangeListener listener) {
            listeners.remove(listener);
        }

        DishCarteHolder() {
            super(DishCarte.class);
            listeners = new CopyOnWriteArrayList<OnDataChangeListener>();
            carteTypeDish = new HashMap<Integer, List<DishCarte>>();
        }

        public List<DishCarte> getDishCarteByType(int type) {
            return carteTypeDish.get(type);
        }

        @Override
        protected Map<Long, DishCarte> cache(List<DishCarte> list) {
            carteTypeDish.clear();

            for (DishCarte dishCarte : list) {
                if (!carteTypeDish.containsKey(dishCarte.getCarteType())) {
                    carteTypeDish.put(dishCarte.getCarteType(), new ArrayList<DishCarte>());
                }

                carteTypeDish.get(dishCarte.getCarteType()).add(dishCarte);
            }
//			DishCarte dishCarte=new DishCarte();
//			dishCarte.setId(1l);
//			dishCarte.setUuid("dkdksdfwerjoqivndkalkdj");
//			dishCarte.setPrice(BigDecimal.ZERO);
//			dishCarte.setName("298套餐");
//			carteTypeDish.put(2,new ArrayList<DishCarte>());
//			carteTypeDish.get(2).add(dishCarte);
            return super.cache(list);
        }

        @Override
        protected List<DishCarte> query(DatabaseHelper helper, Dao<DishCarte, Long> dao) throws Exception {
            QueryBuilder<DishCarte, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishCarte.$.id,
                    DishCarte.$.statusFlag,
                    DishCarte.$.serverCreateTime,
                    DishCarte.$.serverUpdateTime,
                    DishCarte.$.brandIdenty,
                    DishCarte.$.shopIdenty,
                    DishCarte.$.uuid,
                    DishCarte.$.carteCode,
                    DishCarte.$.name,
                    DishCarte.$.price,
                    DishCarte.$.carteType,
                    DishCarte.$.isPublic,
                    DishCarte.$.source,
                    DishCarte.$.isDisable,
                    DishCarte.$.updateId,
                    DishCarte.$.createId);
            qb.where().eq(DishCarte.$.statusFlag, StatusFlag.VALID);
            return qb.query();
        }
    }

    /**
     * 菜品数据缓存
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static class DishHolder extends BasicHolder<DishShop> {

        private final List<OnDataChangeListener> listeners;
        private final DishCyclePeriodHolder dishCyclePeriodHolder;
        Set<Long> typeIdSet;
        private Map<String, DishShop> uuidDishMap;
        protected Map<Long, DishShop> extraMap;
        protected Map<String, DishShop> uuidExtraMap;
        private Map<Long, List<DishShop>> dishInTypeMap;

        DishHolder(DishCyclePeriodHolder dishCyclePeriodHolder) {
            super(DishShop.class);
            listeners = new CopyOnWriteArrayList<OnDataChangeListener>();
            this.dishCyclePeriodHolder = dishCyclePeriodHolder;
            uuidDishMap = new LinkedHashMap<String, DishShop>();
            uuidExtraMap = new LinkedHashMap<String, DishShop>();
            dishInTypeMap = new LinkedHashMap<Long, List<DishShop>>();
        }

        public void addDataChangeListener(OnDataChangeListener listener) {
            listeners.add(listener);
        }

        public void removeDataChangeListener(OnDataChangeListener listener) {
            listeners.remove(listener);
        }

        /**
         * 获取指定uuid的对象
         */
        public DishShop get(String uuid) {
            DishShop dishShop = uuidDishMap.get(uuid);
            if (dishShop != null && !dishCyclePeriodHolder.isValidDish(dishShop.getBrandDishId())) {
                return null;
            }
            return dishShop;
        }

        /**
         * 获取指定分类下所有的菜品
         */

        public List<DishShop> getDishShopByType(Long typeId) {
            List<DishShop> temp = dishInTypeMap.get(typeId);
            List<DishShop> dishShops = new ArrayList<DishShop>();
            if (temp != null) {
                //过滤无效菜品和临时菜
                for (DishShop dishShop : temp) {
                    if (dishShop != null && dishCyclePeriodHolder.isValidDish(dishShop.getBrandDishId()) && !dishShop.isTempDish(TEMPDISHCODE)) {
                        dishShops.add(dishShop);
                    }
                }
            }
            return dishShops;
        }


        /**
         * 获取指定分类下所有的菜品 不做什么条件过滤
         */
        public List<DishShop> getDishShopAllByType(Long typeId) {
            return dishInTypeMap.get(typeId);
        }


        /**
         * 获取指定分类下有效的菜品 不包含称重商品  变价商品 和套餐
         */

        public List<DishShop> getPrintDishShopByType(Long typeId) {
            List<DishShop> temp = dishInTypeMap.get(typeId);
            List<DishShop> dishShops = new ArrayList<DishShop>();
            if (temp != null) {
                //过滤无效菜品和临时菜
                for (DishShop dishShop : temp) {
                    if (!dishShop.isCombo() && dishShop.getSaleType() != SaleType.WEIGHING && dishShop.getIsChangePrice() != Bool.YES) {
                        dishShops.add(dishShop);
                    }
                }
            }
            return dishShops;
        }

        /**
         * 获取指定分类下有效的菜品 不包含称重商品 和套餐
         */

        public List<DishShop> getBindDishShopByType(Long typeId) {
            List<DishShop> temp = dishInTypeMap.get(typeId);
            List<DishShop> dishShops = new ArrayList<>();
            if (temp != null) {
                for (DishShop dishShop : temp) {
                    if (!dishShop.isCombo()
                            && dishShop.getSaleType() != SaleType.WEIGHING) {
                        dishShops.add(dishShop);
                    }
                }
            }
            return dishShops;
        }

        @Override
        public DishShop get(Long id) {
            DishShop dishShop = super.get(id);
            if (dishShop != null && !dishCyclePeriodHolder.isValidDish(dishShop.getBrandDishId())) {
                return null;
            }
            return dishShop;
        }

        @Override
        public Collection<DishShop> getAll() {
            Collection<DishShop> dishShops = super.getAll();
            List<DishShop> resultList = new ArrayList<DishShop>(dishShops);
            for (DishShop dishShop : dishShops) {
                if (!dishCyclePeriodHolder.isValidDish(dishShop.getBrandDishId())) {
                    resultList.remove(dishShop);
                }
            }
            return resultList;
        }

        /**
         * 获取指定uuid的对象(包含循环菜品，for商品设置)
         */
        public DishShop getSet(String uuid) {
            return uuidDishMap.get(uuid);
        }

        @Override
        public Collection<DishShop> getSetAll() {
            Collection<DishShop> dishShops = super.getSetAll();
            List<DishShop> resultList = new ArrayList<DishShop>(dishShops);
            return resultList;
        }

        public boolean containsTypeId(Long typeId) {
            if (typeIdSet == null) {
                return false;
            }
            return typeIdSet.contains(typeId);
        }

        @Override
        protected List<DishShop> query(DatabaseHelper helper, Dao<DishShop, Long> dao) throws Exception {
            //查询临时菜
            QueryBuilder<DishShop, Long> tempDishQb = dao.queryBuilder();
            tempDishShop = tempDishQb.where().eq(DishShop.$.dishCode, TEMPDISHCODE).queryForFirst();
            // 只列出可用的菜品，排除实体卡商品和临时菜
            QueryBuilder<DishShop, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishShop.$.id,
                    DishShop.$.statusFlag,
                    DishShop.$.serverCreateTime,
                    DishShop.$.serverUpdateTime,
                    DishShop.$.brandIdenty,
                    DishShop.$.barcode,
                    DishShop.$.brandDishId,
                    DishShop.$.clearStatus,
                    DishShop.$.dishCode,
                    DishShop.$.dishIncreaseUnit,
                    DishShop.$.dishNameIndex,
                    DishShop.$.dishTypeId,
                    DishShop.$.enabledFlag,
                    DishShop.$.hasStandard,
                    DishShop.$.isChangePrice,
                    DishShop.$.isDiscountAll,
                    DishShop.$.isSingle,
                    DishShop.$.marketPrice,
                    DishShop.$.name,
                    DishShop.$.aliasName,
                    DishShop.$.residueTotal,
                    DishShop.$.saleTotal,
                    DishShop.$.saleType,
                    DishShop.$.sort,
                    DishShop.$.stepNum,
                    DishShop.$.type,
                    DishShop.$.unitId,
                    DishShop.$.uuid,
                    DishShop.$.unitName,
                    DishShop.$.shortName,
                    DishShop.$.aliasShortName,
                    DishShop.$.dishQty,
                    DishShop.$.boxQty,
                    DishShop.$.weight,
                    DishShop.$.minNum,
                    DishShop.$.maxNum,
                    DishShop.$.wmType);
            qb.where().eq(DishShop.$.statusFlag, StatusFlag.VALID).and().ne(DishShop.$.type, DishType.CARD);
//			.and().ne(DishShop.$.dishCode,TEMPDISHCODE)
            return qb.orderBy(DishShop.$.sort, true).orderBy(DishShop.$.brandDishId, true)
                    .query();
        }

        @Override
        protected Map<Long, DishShop> cache(List<DishShop> list) {
            Map<Long, DishShop> dishs = new LinkedHashMap<Long, DishShop>();
            Map<Long, DishShop> extras = new LinkedHashMap<Long, DishShop>();
            Map<String, DishShop> uuidDishs = new LinkedHashMap<String, DishShop>();
            Map<String, DishShop> uuidExtras = new LinkedHashMap<String, DishShop>();
            Map<Long, List<DishShop>> dishsMap = new LinkedHashMap<Long, List<DishShop>>();
            Set<Long> typeIds = new HashSet<Long>();
            for (DishShop entity : list) {
                // DishShop中使用brandDishId，但uuid使用dishShop自己的uuid
                Long dishId = entity.getId();
                String dishUuid = entity.getUuid();
                if (entity.getType() == DishType.EXTRA) {
                    extras.put(dishId, entity);
                    uuidExtras.put(dishUuid, entity);
                } else {
                    dishs.put(dishId, entity);
                    uuidDishs.put(dishUuid, entity);
                }
                if (entity.getDishTypeId() != null && entity.getIsSingle() == Bool.YES) {
                    typeIds.add(entity.getDishTypeId());

                    if (dishsMap.containsKey(entity.getDishTypeId())) {
                        dishsMap.get(entity.getDishTypeId()).add(entity);
                    } else {
                        List<DishShop> temp = new ArrayList<DishShop>();
                        temp.add(entity);
                        dishsMap.put(entity.getDishTypeId(), temp);
                    }
                }
            }
            this.typeIdSet = typeIds;
            this.uuidDishMap = uuidDishs;
            this.extraMap = extras;
            this.uuidExtraMap = uuidExtras;
            this.dishInTypeMap = dishsMap;
            return dishs;
        }

        @Override
        protected void renewDatas(Map<Long, DishShop> theNew) {
            super.renewDatas(theNew);
            //在主线程里面回调
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    for (OnDataChangeListener listener : listeners) {
                        try {
                            listener.onDataChanged();
                        } catch (Exception e) {
                            Log.w(TAG, "", e);
                        }
                    }
                }
            });
        }
    }

    /**
     * 加料数据缓存
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static class ExtraHolder extends AbstractHolder<DishShop> {

        private final DishHolder dishHolder;

        ExtraHolder(DishHolder dishHolder) {
            this.dishHolder = dishHolder;
        }

        /**
         * 获取指定uuid的对象
         */
        public DishShop get(String uuid) {
            return dishHolder.uuidExtraMap.get(uuid);
        }

        @Override
        protected Map<Long, DishShop> getDatas() {
            return dishHolder.extraMap;
        }

        @Override
        protected void refresh(DatabaseHelper helper) throws Exception {
        }
    }

    /**
     * 菜品分类数据缓存
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    private static class DishTypeHolder extends BasicHolder<DishBrandType> {

        private DishHolder dishHolder;

        DishTypeHolder(DishHolder dishHolder) {
            super(DishBrandType.class);
            this.dishHolder = dishHolder;
        }

        @Override
        protected List<DishBrandType> query(DatabaseHelper helper, Dao<DishBrandType, Long> dao)
                throws Exception {
            QueryBuilder<DishBrandType, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishBrandType.$.id,
                    DishBrandType.$.statusFlag,
                    DishBrandType.$.serverCreateTime,
                    DishBrandType.$.serverUpdateTime,
                    DishBrandType.$.parentId,
                    DishBrandType.$.brandIdenty,
                    DishBrandType.$.name,
                    DishBrandType.$.aliasName,
                    DishBrandType.$.sort,
                    DishBrandType.$.uuid);
            qb.where().eq(DishBrandType.$.enabledFlag, Bool.YES);
            return qb.orderBy(DishBrandType.$.sort, true)
//					.orderBy(DishBrandType.$.id, true)
                    .query();
        }

        @Override
        protected void cacheEntity(Map<Long, DishBrandType> map, DishBrandType entity) {
            // 过滤掉没有菜品的分类
			/*
			if (dishHolder.containsTypeId(entity.getId())) {
				super.cacheEntity(map, entity);
			}*/
            super.cacheEntity(map, entity);
        }
    }

    /**
     * 套餐明细数据缓存
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static class DishSetmealHolder extends BasicHolder<DishSetmeal> {

        private Map<String, DishSetmeal> keySetmealMap;
        protected Map<Long, DishSetmeal> dishExtraMap;
        protected Map<String, DishSetmeal> keyDishExtraMap;
        protected  Map<Long,List<DishSetmeal>> mapDishSetmealByDishId;

        DishSetmealHolder() {
            super(DishSetmeal.class);
        }

        public DishSetmeal get(Long comboDishId, Long childDishId, Long setmealGroupId) {
            String key = toKey(comboDishId, childDishId, setmealGroupId);
            return keySetmealMap.get(key);
        }

        public List<DishSetmeal> getDishSetmealByDishId(Long dishId){
                if(mapDishSetmealByDishId==null){
                    return null;
                }
                return mapDishSetmealByDishId.get(dishId);
        }

        @Override
        protected List<DishSetmeal> query(DatabaseHelper helper, Dao<DishSetmeal, Long> dao) throws Exception {
            return dao.queryBuilder()
                    .selectColumns(DishSetmeal.$.id,
                            DishSetmeal.$.statusFlag,
                            DishSetmeal.$.serverCreateTime,
                            DishSetmeal.$.serverUpdateTime,
                            DishSetmeal.$.brandIdenty,
                            DishSetmeal.$.childDishId,
                            DishSetmeal.$.childDishType,
                            DishSetmeal.$.comboDishTypeId,
                            DishSetmeal.$.dishId,
                            DishSetmeal.$.isDefault,
                            DishSetmeal.$.isMulti,
                            DishSetmeal.$.isReplace,
                            DishSetmeal.$.leastCellNum,
                            DishSetmeal.$.price)
                    .orderBy(DishSetmeal.$.sort, true)
//					.orderBy(DishSetmeal.$.id, true)
                    .query();
        }

        @Override
        protected Map<Long, DishSetmeal> cache(List<DishSetmeal> list) {
            Map<Long, DishSetmeal> setmeals = new LinkedHashMap<Long, DishSetmeal>();
            Map<Long, List<DishSetmeal>> dishSetmealByDishId = new LinkedHashMap<Long, List<DishSetmeal>>();
            Map<Long, DishSetmeal> dishExtras = new LinkedHashMap<Long, DishSetmeal>();
            Map<String, DishSetmeal> keySetmeals = new HashMap<String, DishSetmeal>();
            Map<String, DishSetmeal> keyDishExtras = new HashMap<String, DishSetmeal>();
            for (DishSetmeal entity : list) {
                String key = toKey(entity.getDishId(), entity.getChildDishId(), entity.getComboDishTypeId());
                if (entity.getChildDishType() != ChildDishType.EXTRA) {
                    cacheEntity(setmeals, entity);
                    keySetmeals.put(key, entity);
                } else {
                    cacheEntity(dishExtras, entity);
                    keyDishExtras.put(key, entity);
                }

                if(!dishSetmealByDishId.containsKey(entity.getDishId())){
                    dishSetmealByDishId.put(entity.getDishId(),new ArrayList<DishSetmeal>());
                }
                dishSetmealByDishId.get(entity.getDishId()).add(entity);
            }
            this.dishExtraMap = dishExtras;
            this.keySetmealMap = keySetmeals;
            this.keyDishExtraMap = keyDishExtras;
            this.mapDishSetmealByDishId=dishSetmealByDishId;
            return setmeals;
        }

        /**
         * 生成套餐明细的KEY
         *
         * @param comboDishId 套餐对应的商品ID
         * @param childDishId 明细对应的商品ID
         * @param groupId     明细的分组ID，可以为null
         */
        private String toKey(Long comboDishId, Long childDishId, Long groupId) {
            if (groupId == null) {
                groupId = 0L;
            }
            return comboDishId + "_" + childDishId + "_" + groupId;
        }

    }

    /**
     * 菜品加料缓存数据
     *
     * @version: 1.0
     * @date 2015年7月14日
     */
    public static class DishExtraHolder extends AbstractHolder<DishSetmeal> {

        private final DishSetmealHolder dishSetmealHolder;

        DishExtraHolder(DishSetmealHolder dishSetmealHolder) {
            this.dishSetmealHolder = dishSetmealHolder;
        }

        public DishSetmeal get(Long parentDishId, Long childDishId) {
            String key = dishSetmealHolder.toKey(parentDishId, childDishId, null);
            return dishSetmealHolder.keyDishExtraMap.get(key);
        }

        /**
         * 获取公共加料对应的DishSetmeal
         */
        public DishSetmeal get(Long childDishId) {
            if (dishSetmealHolder.dishExtraMap != null && !dishSetmealHolder.dishExtraMap.isEmpty()) {
                for (DishSetmeal dishSetmeal : dishSetmealHolder.dishExtraMap.values()) {
                    if (Utils.equals(childDishId, dishSetmeal.getChildDishId())) {
                        return dishSetmeal;
                    }
                }
            }

            return null;
        }

        @Override
        protected Map<Long, DishSetmeal> getDatas() {
            return dishSetmealHolder.dishExtraMap;
        }

        @Override
        protected void refresh(DatabaseHelper helper) throws Exception {
        }

    }

    /**
     * 属性基础数据缓存
     *
     * @version: 1.0
     * @date 2015年7月27日
     */
    public static class PropertyHolder extends BasicHolder<DishProperty> {

        private Map<String, DishProperty> uuidPropertyMap;
        private Map<Long, List<DishProperty>> dishShopPropertyMap;

        PropertyHolder() {
            super(DishProperty.class);
        }

        public DishProperty get(String uuid) {
            return uuidPropertyMap.get(uuid);
        }

        public List<DishProperty> getDishPropertyByDishShopId(Long dishShopId) {
            return dishShopPropertyMap.get(dishShopId);
        }

        @Override
        protected List<DishProperty> query(DatabaseHelper helper, Dao<DishProperty, Long> dao)
                throws Exception {
            QueryBuilder<DishProperty, Long> qb = dao.queryBuilder();
            qb.selectColumns(DishProperty.$.id,
                    DishProperty.$.statusFlag,
                    DishProperty.$.serverCreateTime,
                    DishProperty.$.serverUpdateTime,
                    DishProperty.$.brandIdenty,
                    DishProperty.$.name,
                    DishProperty.$.propertyKind,
                    DishProperty.$.propertyTypeId,
                    DishProperty.$.reprice,
                    DishProperty.$.sort,
                    DishProperty.$.dishShopId,
                    DishProperty.$.uuid);
            qb.where().eq(DishProperty.$.statusFlag, StatusFlag.VALID);
            return qb.orderBy(DishProperty.$.sort, true)
//					.orderBy(DishProperty.$.id, true)
                    .query();
        }

        @Override
        protected Map<Long, DishProperty> cache(List<DishProperty> list) {
            Map<Long, DishProperty> properties = new LinkedHashMap<Long, DishProperty>();
            Map<Long, List<DishProperty>> dishShpPropertieMap = new LinkedHashMap<>();
            Map<String, DishProperty> uuidProperties = new LinkedHashMap<String, DishProperty>();
            for (DishProperty entity : list) {
                properties.put(entity.getId(), entity);
                uuidProperties.put(entity.getUuid(), entity);

                if (!dishShpPropertieMap.containsKey(entity.getDishShopId())) {
                    dishShpPropertieMap.put(entity.getDishShopId(), new ArrayList());
                }
                dishShpPropertieMap.get(entity.getDishShopId()).add(entity);
            }
            this.uuidPropertyMap = uuidProperties;
            this.dishShopPropertyMap = dishShpPropertieMap;
            return properties;
        }
    }

    /**
     * 商品属性数据缓存
     *
     * @version: 1.0
     * @date 2015年7月31日
     */
    public static class DishPropertyHolder extends BasicHolder<DishBrandProperty> {

        private Map<String, DishBrandProperty> keyMap;
        private Map<Long, List<DishBrandProperty>> propertyMap;

        DishPropertyHolder() {
            super(DishBrandProperty.class);
        }

        public DishBrandProperty get(Long dishId, Long propertyId) {
            if (keyMap == null) {
                return null;
            } else {
                return keyMap.get(toKey(dishId, propertyId));
            }
        }

        public List<DishBrandProperty> getPropertysByDishId(Long dishId) {
            if (propertyMap == null) {
                return null;
            } else {
                return propertyMap.get(dishId);
            }
        }

        @Override
        protected List<DishBrandProperty> query(DatabaseHelper helper, Dao<DishBrandProperty, Long> dao)
                throws Exception {
            return dao.queryBuilder()
                    .selectColumns(DishBrandProperty.$.id,
                            DishBrandProperty.$.statusFlag,
                            DishBrandProperty.$.serverCreateTime,
                            DishBrandProperty.$.serverUpdateTime,
                            DishBrandProperty.$.brandIdenty,
                            DishBrandProperty.$.dishId,
                            DishBrandProperty.$.dishName,
                            DishBrandProperty.$.propertyId,
                            DishBrandProperty.$.propertyKind)
                    .orderBy(DishBrandProperty.$.propertyId, true)
//					.orderBy(DishBrandProperty.$.id, true)
                    .query();
        }

        @Override
        protected Map<Long, DishBrandProperty> cache(List<DishBrandProperty> list) {
            Map<Long, DishBrandProperty> map = new LinkedHashMap<Long, DishBrandProperty>();
            Map<String, DishBrandProperty> keyDishProperties = new HashMap<String, DishBrandProperty>();
            //缓存规格集
            Map<Long, List<DishBrandProperty>> keyPropertys = new HashMap<Long, List<DishBrandProperty>>();
            for (DishBrandProperty entity : list) {
                map.put(entity.getId(), entity);
                keyDishProperties.put(toKey(entity.getDishId(), entity.getPropertyId()), entity);

                //过滤规格集
                if (entity.getPropertyKind() == PropertyKind.STANDARD) {
                    if (keyPropertys.containsKey(entity.getDishId())) {
                        keyPropertys.get(entity.getDishId()).add(entity);
                    } else {
                        List<DishBrandProperty> proprtys = new ArrayList<DishBrandProperty>();
                        proprtys.add(entity);
                        keyPropertys.put(entity.getDishId(), proprtys);
                    }
                }
            }
            this.keyMap = keyDishProperties;
            this.propertyMap = keyPropertys;
            return map;
        }

        private String toKey(Long dishId, Long propertyId) {
            return dishId + "_" + propertyId;
        }
    }

    /**
     * 数据改变监听器
     */
	/*private static class DataObserver extends ContentObserver {
		
		private final BasicHolder<?> holder;

		public DataObserver(BasicHolder<?> holder,Handler handler) {
			super(handler);
			this.holder = holder;
		}
		
		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}
		
		@Override
		public void onChange(boolean selfChange) {
			try {
				holder.refresh();
			} catch (Exception e) {
				Log.e(TAG, "refresh error!", e);
			}
		}
	}*/
    //modify  20170908 替换provider 监听
    private static class DataObserver implements DatabaseHelper.DataChangeObserver {

        private final BasicHolder<?> holder;
        private final Uri uri;

        public DataObserver(BasicHolder<?> holder, Uri uri) {
            this.uri = uri;
            this.holder = holder;
        }

        private void recordDishLog(boolean isLogBefore) {
            boolean isDishKey = S_URI_DISH_SHOP.equals(holder.uri) || S_URI_DISH_BRAND_TYPE.equals(holder.uri);
            if (isDishKey) {
                if (isLogBefore) {
                    RLog.i(RLog.DISH_KEY_TAG, "[DishCache]商品数据发生改变>>即将进行刷新操作Uri: " + holder.uri + " position : DishCache -> DataObserver.recordDishLog()");
                    RLog.i(RLog.DISH_KEY_TAG, "[DishCache]Uri: " + holder.uri + "刷新之前数据 " + Utils.size(holder.getAll()) + " position : DishCache -> DataObserver.recordDishLog()");
                } else {
                    RLog.i(RLog.DISH_KEY_TAG, "[DishCache]Uri: " + holder.uri + "刷新之后数据 " + Utils.size(holder.getAll()) + " position : DishCache -> DataObserver.recordDishLog()");
                }
            }
        }

        @Override
        public void onChange(Collection<Uri> uris) {
            if (uris.contains(uri)) {
                ThreadUtils.runOnWorkThread(new Runnable() {//add v8.1 添加线程池工具替换
                    public void run() {
                        try {
                            recordDishLog(true);
                            holder.refresh();
                            recordDishLog(false);
                        } catch (Exception e) {
                            Log.e(TAG, "refresh error!", e);
                        }
                    }
                });
            }
        }
    }

    /**
     * @version: 1.0
     * @date 2016年3月14日
     */
    public static interface OnDataChangeListener {

        void onDataChanged();

    }

    private void checkData() {
        try {
            Log.i(TAG, "isInit=" + inited);
            Log.i(TAG, "dishCyclePeriodHolder.count=" + dishCyclePeriodHolder.getCount());
            Log.i(TAG, "dish.count=" + dishHolder.getCount());
            Log.i(TAG, "extra.count=" + extraHolder.getCount());
            Log.i(TAG, "dishType.count=" + dishTypeHolder.getCount());
            Log.i(TAG, "propertyType.count=" + propertyTypeHolder.getCount());
            Log.i(TAG, "property.count=" + propertyHolder.getCount());
            Log.i(TAG, "dishProperty.count=" + dishPropertyHolder.getCount());
            Log.i(TAG, "setmeal.count=" + setmealHolder.getCount());
            Log.i(TAG, "setmealGroup.count=" + setmealGroupHolder.getCount());
            Log.i(TAG, "dishExtra.count=" + dishExtraHolder.getCount());
        } catch (Exception e) {
            Log.e(TAG, "checkData error!", e);
        }
    }

}