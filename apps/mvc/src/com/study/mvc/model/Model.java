package com.study.mvc.model;
     
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
     
import android.os.SystemClock;
     
import android.util.Log;
     
/**
 * <功能描述> 专指Model层；使用ThreadSafe指示是线程安全的
 *
 * @author Administrator
 */
public class Model {
    private static final String TAG = Model.class.getSimpleName();
     
    public interface Listener {
        void onModelStateUpdated(Model model);
    }
     
    // Model模型中包含有ModelData
    private ModelData data = new ModelData(0);
     
    private final List<Listener> listeners = new ArrayList<Listener>();
     
    public Model() {
        Log.d(TAG, "Model constructer..");
    }
     
    public final ModelData getData() {
        synchronized (this) {
            return data;
        }
    }
     
    /**
     * <功能描述> 更新数据
     *
     * @return void [返回类型说明]
     */
    public final void updateData() {
        // 可能是请求服务器数据，执行繁重的计算
        SystemClock.sleep(3000);
        ModelData newData = new ModelData(new Random().nextInt(10) + 1);
     
        synchronized (this) {
            data = newData;
        }
     
        synchronized (listeners) {
            for (Listener listener : listeners) {
                listener.onModelStateUpdated(this);
            }
        }
    }
     
    /**
     * <功能描述>增加监听器
     *
     * @param listener [参数说明]
     * @return void [返回类型说明]
     */
    public final void addListener(Listener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
     
    /**
     * <功能描述>移除监听器
     *
     * @param listener [参数说明]
     * @return void [返回类型说明]
     */
    public final void removeListener(Listener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
