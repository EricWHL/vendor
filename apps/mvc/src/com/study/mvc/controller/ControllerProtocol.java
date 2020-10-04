package com.study.mvc.controller;
     
public interface ControllerProtocol {
    // 从View层传递的Message
    int V_REQUEST_QUIT = 101;
    int V_REQUEST_UPDATE = 102;
    int V_REQUEST_DATA = 103;
     
    // 从Controller层传递的Message
    int C_QUIT = 201;
    int C_UPDATE_STARTED = 202;
    int C_UPDATE_FINISHED = 203;
    int C_DATA = 204; // obj = (ModelData) data
}

