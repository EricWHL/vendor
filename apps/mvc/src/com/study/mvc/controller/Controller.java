package com.study.mvc.controller;
     
import static com.study.mvc.controller.ControllerProtocol.C_QUIT;
     
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
     
import com.study.mvc.model.*;
import android.util.Log;
     
import java.util.ArrayList;
import java.util.List;
     
public class Controller {
    private static final String TAG = Controller.class.getSimpleName();
     
    private final Model model;
     
    // 使用HandlerThread、Handler、Message机制实现不同层的消息通信
    // inBox：用于接收从View层发送的Message
    private final HandlerThread inboxHandlerThread;
    private final Handler inboxHandler;
    // outBox：用于Controller给View发送Message
    private final List<Handler> outboxHandlers = new ArrayList<Handler>();
     
    // 将消息处理委托给ControllerState，不在Controller中处理Message
    private ControllerState state;
     
    /**
     * <默认构造函数> Controller层初始化
     */
    public Controller(Model model) {
        this.model = model;
     
        // Handler就存在于该Thread子线程（也就是工作线程）中
        inboxHandlerThread = new HandlerThread("Controller Inbox");
        inboxHandlerThread.start();
        // HandlerThread的getLooper()获取到Handler实例
        inboxHandler = new Handler(inboxHandlerThread.getLooper()) {
     
                @Override
                public void handleMessage(Message msg) {
                    Controller.this.handleMessage(msg);
                }
            };
     
        // 初始化ControllerState，准备接受Message
        this.state = new ReadyState(this);
    }
     
    // 处理从View层传递过来的Message
    private void handleMessage(Message msg) {
        Log.d(TAG, "handleMessage::Received message...msg.what=" + msg.what);
     
        // 把消息处理委托给它的 ControllerState
        if (!state.handleMessage(msg)) {
            Log.d(TAG, "Unknown message: " + msg);
        }
    }
     
    /**
     * <功能描述> inboxHandlerThread退出
     *
     * @return void [返回类型说明]
     */
    public final void dispose() {
        inboxHandlerThread.getLooper().quit();
    }
     
    /**
     * <功能描述>获取inBox的Handler实例，并返回该实例
     *
     * @return [参数说明]
     * @return Handler [返回类型说明]
     */
    public final Handler getInboxHandler() {
        return inboxHandler;
    }
     
    /**
     * <功能描述> 增加Handler到outBox中；outBox用于Controller传送Message到View层
     *
     * @param handler [参数说明]
     * @return void [返回类型说明]
     */
    public final void addOutboxHandler(Handler handler) {
        outboxHandlers.add(handler);
    }
     
    /**
     * <功能描述> 从outBox移除Handler
     *
     * @param handler [参数说明]
     * @return void [返回类型说明]
     */
    public final void removeOutboxHandler(Handler handler) {
        outboxHandlers.remove(handler);
    }
     
    /**
     * <功能描述> 更新Controller的状态，ReadyState和UpdatingState之间的切换
     *
     * @param newState [参数说明]
     * @return void [返回类型说明]
     */
    final void changeState(ControllerState newState) {
        Log.d(TAG,
                  String.format("Changing state from %s to %s", state, newState));
        state = newState;
    }
     
    /**
     * <功能描述> Controller层向View层发送Message
     *
     * @param what
     * @param arg1
     * @param arg2
     * @param obj [参数说明]
     * @return void [返回类型说明]
     */
    final void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
        Log.d(TAG, "notifyOutboxHandlers::running...what=" + what);
     
        if (outboxHandlers.isEmpty()) {
            Log.d(TAG, String.format(
                          "No outbox handler to handle outgoing message (%d)", what));
        } else {
            for (Handler handler : outboxHandlers) {
                Message msg = Message.obtain(handler, what, arg1, arg2, obj);
                msg.sendToTarget();
            }
        }
    }
     
    /**
     * <功能描述> 获取到Model层
     *
     * @return [参数说明]
     * @return Model [返回类型说明]
     */
    final Model getModel() {
        return model;
    }
     
    /**
     * <功能描述> 接收用户输入，准备退出应用
     *
     * @return void [返回类型说明]
     */
    final void quit() {
        notifyOutboxHandlers(C_QUIT, 0, 0, null);
    }
}
