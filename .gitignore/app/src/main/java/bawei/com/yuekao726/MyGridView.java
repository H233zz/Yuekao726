package bawei.com.yuekao726;

import android.content.Context;
import android.widget.GridView;

/**
 * 类描述：
 * 创建人：郝艳晴
 * 创建时间：2017/7/26  14:42
 */

public class MyGridView extends GridView{
    public MyGridView(Context context) {
            super(context);
        }
        @Override
        public void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,
            MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec,expandSpec);
        }
}
