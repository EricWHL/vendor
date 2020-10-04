package com.study.mvc.controller;
     
import android.os.Message;
     
import android.util.Log;
     
import static com.study.mvc.controller.ControllerProtocol.*;
     
/**
 * <功能描述> 数据更新的状态之一：准备阶段，最前面的状态
 *
 * @author Administrator
 */
final class ReadyState implements ControllerState {
    private static final String TAG = ReadyState.class.getSimpleName();
     
    private final Controller controller;
     
    public ReadyState(Controller controller) {
        this.controller = controller;
    }
     
    @Override
    public final boolean handleMessage(Message msg) {
        Log.d(TAG, "handleMessage::running...msg.what=" + msg.what);
     
        switch (msg.what) {
        case V_REQUEST_QUIT:
            // 从View层获取到的Quite消息
            onRequestQuit();
            return true;
        case V_REQUEST_UPDATE:
            // 从View层获取到的Update的消息
            onRequestUpdate();
            return true;
        case V_REQUEST_DATA:
            // 从View层获取到的更新数据的消息
            onRequestData();
            return true;
        }
        return false;
    }
     
    /**
     * <功能描述> 请求数据
     *
     * @return void [返回类型说明]
     */
    private void onRequestData() {
        Log.d(TAG, "onRequestData::running...");
        controller.notifyOutboxHandlers(C_DATA, 0, 0, controller.getModel()
                                        .getData());
    }
     
    /**
     * <功能描述> 请求更新数据，并更改Controller的状态
     *
     * @return void [返回类型说明]
     */
    private void onRequestUpdate() {
        Log.d(TAG, "onRequestUpdate::running...");
        // 状态返回给Controller层，并将状态改变为UpdatingState
        controller.changeState(new UpdatingState(controller));
    }
     
    private void onRequestQuit() {
        Log.d(TAG, "onRequestQuit::running...");
        controller.quit();
    }
}
