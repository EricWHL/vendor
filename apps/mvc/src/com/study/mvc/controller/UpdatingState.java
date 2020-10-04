package com.study.mvc.controller;
     
import static com.study.mvc.controller.ControllerProtocol.C_UPDATE_FINISHED;
import static com.study.mvc.controller.ControllerProtocol.C_UPDATE_STARTED;
import static com.study.mvc.controller.ControllerProtocol.V_REQUEST_QUIT;
     
import android.os.Message;
     
import android.util.Log;
     
/**
 * <功能描述> 数据更新的状态之一：正在更新阶段
 *
 * @author Administrator
 */
final class UpdatingState implements ControllerState {
    private static final String TAG = UpdatingState.class.getSimpleName();
     
    private final Controller controller;
    private final Thread updateThread;
     
    public UpdatingState(Controller controller) {
        this.controller = controller;
     
        // 新建一个Thread工作线程
        updateThread = new Thread("Model Update") {
     
                @Override
                public void run() {
                    Controller controller = UpdatingState.this.controller;
                    try {
                        // 在工作线程中执行数据请求（抽象为相对耗时的操作），可为其他：数据库操作
                        controller.getModel().updateData();
                    } catch (Throwable t) {
                        Log.d(TAG, "Error in the update thread");
                    } finally {
                        // controller.getModel().updateData()执行完后，反馈数据给Controller层
                        notifyControllerOfCompletion();
                    }
                }
            };
        updateThread.start();
        // Controller层向View层发送Message，开始更新数据
        controller.notifyOutboxHandlers(C_UPDATE_STARTED, 0, 0, null);
    }
     
    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "handleMessage::msg.what=" + msg.what);
     
        switch (msg.what) {
        case V_REQUEST_QUIT:
            onRequestQuit();
            return true;
        }
        return false;
    }
     
    /**
     * <功能描述> 数据获取到后，最终会反馈给Controller层
     *
     * @return void [返回类型说明]
     */
    private void notifyControllerOfCompletion() {
        Log.d(TAG, "notifyControllerOfCompletion::starting...");
     
        controller.getInboxHandler().post(new Runnable() {
     
                @Override
                public void run() {
                    // 将状态改变为ReadyState
                    controller.changeState(new ReadyState(controller));
                    // 并通知结束更新
                    controller.notifyOutboxHandlers(C_UPDATE_FINISHED, 0, 0, null);
                }
            });
    }
     
    private void onRequestQuit() {
        Log.d(TAG, "onRequestQuit::starting...");
     
        updateThread.interrupt();
        controller.quit();
    }
}
