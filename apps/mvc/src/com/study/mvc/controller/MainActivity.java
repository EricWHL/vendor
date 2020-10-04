package com.study.mvc.controller;
     
import static com.study.mvc.controller.ControllerProtocol.C_DATA;
import static com.study.mvc.controller.ControllerProtocol.C_QUIT;
import static com.study.mvc.controller.ControllerProtocol.C_UPDATE_FINISHED;
import static com.study.mvc.controller.ControllerProtocol.C_UPDATE_STARTED;
import static com.study.mvc.controller.ControllerProtocol.V_REQUEST_DATA;
import static com.study.mvc.controller.ControllerProtocol.V_REQUEST_QUIT;
import static com.study.mvc.controller.ControllerProtocol.V_REQUEST_UPDATE;
     
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
     
import com.study.mvc.R;
import com.study.mvc.model.Model;
import com.study.mvc.model.ModelData;
     
public class MainActivity extends Activity implements Callback,
                                          View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
     
    private Controller controller;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     
        findViewById(R.id.update).setOnClickListener(this);
        findViewById(R.id.quit).setOnClickListener(this);
     
        // 初始化层次结构中的交互
        controller = new Controller(new Model());
        // 使用MvcActivity所在的主线程初始化outBox中的Handler
        controller.addOutboxHandler(new Handler(MainActivity.this));
        // Controller层发送请求数据的Message
        controller.getInboxHandler().sendEmptyMessage(V_REQUEST_DATA);
    }
     
    @Override
    public void onClick(View v) {
        // 接收用户输入
        switch (v.getId()) {
        case R.id.update:
            Log.d(TAG, "User click input: R.id.update");
            // 接收用户输入并得到Controller层响应，Controller层发送请求数据的Message
            controller.getInboxHandler().sendEmptyMessage(V_REQUEST_UPDATE);
            break;
        case R.id.quit:
            Log.d(TAG, "User click input: R.id.quit");
            // 接收用户输入并得到Controller层响应，Controller层发送请求数据的Message
            controller.getInboxHandler().sendEmptyMessage(V_REQUEST_QUIT);
            break;
        }
    }
     
    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "Received message: " + msg);
     
        switch (msg.what) {
        case C_QUIT:
            // 接收Controller层输入，准备退出应用
            onQuit();
            return true;
        case C_DATA:
            // 将数据显示到View层
            onData((ModelData) msg.obj);
            return true;
        case C_UPDATE_STARTED:
            // （状态）正在后台请求数据
            onUpdateStarted();
            return true;
        case C_UPDATE_FINISHED:
            // （状态）后台请求数据结束，准备显示
            onUpdateFinished();
            return true;
        }
        return false;
    }
     
    @Override
    protected void onDestroy() {
        try {
            controller.dispose();
        } catch (Throwable t) {
            Log.d(TAG, "Failed to destroy the controller");
        }
     
        super.onDestroy();
    }
     
    /**
     * <功能描述> Controller层做出update响应，更新View层
     *
     * @return void [返回类型说明]
     */
    private void onQuit() {
        Log.d(TAG, "Activity quitting");
        finish();
    }
     
    /**
     * <功能描述> 从Controller层传递过来的Message，表明获取到返回的数据，更新View
     *
     * @param data [参数说明]
     * @return void [返回类型说明]
     */
    private void onData(ModelData data) {
        Log.d(TAG, "onData::running...");
        TextView dataView = (TextView) findViewById(R.id.data_view);
        dataView.setText("The answer is " + data.getAnswer());
    }
     
    /**
     * <功能描述> 开始Update数据显示ProgressBar
     *
     * @return void [返回类型说明]
     */
    private void onUpdateStarted() {
        Log.d(TAG, "onUpdateStarted::running...");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }
     
    /**
     * <功能描述> Update数据过程结束后，ProgressBar消失，显示数据
     *
     * @return void [返回类型说明]
     */
    private void onUpdateFinished() {
        Log.d(TAG, "onUpdateFinished::running...");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        controller.getInboxHandler().sendEmptyMessage(V_REQUEST_DATA);
    }
     
}
