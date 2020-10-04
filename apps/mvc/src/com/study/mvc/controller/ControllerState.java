package com.study.mvc.controller;
 
import android.os.Message;
 
/**
 * <功能描述> 将Controller层的消息处理逻辑转交给ControllerState处理；ControllerState有两个实现类：
 * ReadyState和UpdatingState
 *
 * @author Administrator
 */
public interface ControllerState {
    boolean handleMessage(Message msg);
}
