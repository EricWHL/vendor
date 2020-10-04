package com.study.mvc.model;
     
import android.util.Log;
     
import java.io.Serializable;
     
/**
 * <功能描述> Model层中的数据，指示数据层的状态；Immutable指示是不变的
 *
 * @author Administrator
 */
public final class ModelData implements Serializable {
    private static final String TAG = ModelData.class.getSimpleName();
    private static final long serialVersionUID = 1L;
     
    private final int answer;
     
    public ModelData(int answer) {
        Log.d(TAG, "ModelData::answer=" + answer);
        this.answer = answer;
    }
     
    public final int getAnswer() {
        Log.d(TAG, "getAnswer::answer=" + answer);
     
        return answer;
    }
}
