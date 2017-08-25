package bawei.com.yuekao726;

import android.app.Application;

import org.xutils.x;

/**
 * 类描述：
 * 创建人：郝艳晴
 * 创建时间：2017/7/26  16:37
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }

}
