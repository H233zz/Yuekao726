package bawei.com.yuekao726;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.xutils.x.http;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private List<String> mUserList = new ArrayList<>();
    private List<String> mOtherList = new ArrayList<>();
    private OtherAdapter mUserAdapter, mOtherAdapter;
    private GridView mUserGv, mOtherGv;
    private View view;
    private List<Data.DataBean.ComicsBean>list=new ArrayList<>();
    private final String[] str = {"wifi", "手机流量"};
    private int rate;
    private NumberProgressBar proBar;
    private  int mCurrProgress = -1;
    private boolean isDown;
    private int mCruuDownsize = 0 ;
    private File downApk;
    private int fileSize;
    private String urlPath="http://api.kkmh.com/v1/daily/comic_lists/0?since=0&gender=0&sa_event=eyJwcm9qZWN0Ijoia3VhaWthbl9hcHAiLCJ0aW1lIjoxNDg3NzQyMjQwNjE1LCJwcm9wZXJ0aWVzIjp7IkhvbWVwYWdlVGFiTmFtZSI6IueDremXqCIsIlZDb21tdW5pdHlUYWJOYW1lIjoi54Ot6ZeoIiwiJG9zX3ZlcnNpb24iOiI0LjQuMiIsIkdlbmRlclR5cGUiOiLlpbPniYgiLCJGcm9tSG9tZXBhZ2VUYWJOYW1lIjoi54Ot6ZeoIiwiJGxpYl92ZXJzaW9uIjoiMS42LjEzIiwiJG5ldHdvcmtfdHlwZSI6IldJRkkiLCIkd2lmaSI6dHJ1ZSwiJG1hbnVmYWN0dXJlciI6ImJpZ25veCIsIkZyb21Ib21lcGFnZVVwZGF0ZURhdGUiOjAsIiRzY3JlZW5faGVpZ2h0IjoxMjgwLCJIb21lcGFnZVVwZGF0ZURhdGUiOjAsIlByb3BlcnR5RXZlbnQiOiJSZWFkSG9tZVBhZ2UiLCJGaW5kVGFiTmFtZSI6IuaOqOiNkCIsImFidGVzdF9ncm91cCI6MTEsIiRzY3JlZW5fd2lkdGgiOjcyMCwiJG9zIjoiQW5kcm9pZCIsIlRyaWdnZXJQYWdlIjoiSG9tZVBhZ2UiLCIkY2FycmllciI6IkNoaW5hIE1vYmlsZSIsIiRtb2RlbCI6IlZQaG9uZSIsIiRhcHBfdmVyc2lvbiI6IjMuNi4yIn0sInR5cGUiOiJ0cmFjayIsImRpc3RpbmN0X2lkIjoiQTo2YWRkYzdhZTQ1MjUwMzY1Iiwib3JpZ2luYWxfaWQiOiJBOjZhZGRjN2FlNDUyNTAzNjUiLCJldmVudCI6IlJlYWRIb21lUGFnZSJ9";
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            proBar.setProgress(mCurrProgress);
            if(mCurrProgress==100) {
                isDown = false;
                mCruuDownsize=0;
                fileSize=0;
                mCurrProgress=-1;

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(downApk),
                        "application/vnd.android.package-archive");
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proBar= (NumberProgressBar) findViewById(R.id.progre);
        downApk = new File(getExternalCacheDir(), "jinritoutiao.apk");
        Log.e("File//////////////","-----------------"+getExternalCacheDir());
        init();
    }

    private void init() {
        mUserGv = (GridView) findViewById(R.id.userGridView);
        mOtherGv = (GridView) findViewById(R.id.otherGridView);
        RequestParams params=new RequestParams(urlPath);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Data data=new Gson().fromJson(result,Data.class);
                list.addAll(data.getData().getComics());
                for (int i=0;i<10;i++){
                    mUserList.add(list.get(i).getLabel_text());
                    mUserAdapter.notifyDataSetChanged();
                }
                for (int i=11;i<list.size();i++){
                    mOtherList.add(list.get(i).getLabel_text());
                    mOtherAdapter.notifyDataSetChanged();
                }



            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
        initView();
    }

    private void initView() {
        mUserAdapter = new OtherAdapter(this, mUserList, true);
        mOtherAdapter = new OtherAdapter(this, mOtherList, false);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);
        view = View.inflate(MainActivity.this, R.layout.alertdialog, null);
        mUserGv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                load();
                return false;
            }
        });

        mOtherGv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                load();
                return false;
            }
        });

    }

    private void load() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //builder.setView(view);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("网络选择");

        builder.setSingleChoiceItems(str, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            loadwifi();
                        } else if (which == 1) {
                            Toast.makeText(MainActivity.this, "您选择的是手机流量，请选择wifi进行更新", Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                }
        );
        builder.create().show();

    }

    private void loadwifi() {
        AlertDialog.Builder wbuilder = new AlertDialog.Builder(MainActivity.this);
        wbuilder.setTitle("版本更新")
                .setMessage("现在检测到新版本，是否安装")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        xiazai();

                    }
                }).create().show();


    }

    private void xiazai() {
        if(isDown)
            return;

        Thread thread =new Thread(){
            @Override
            public void run() {
                try {
                    URL url =new URL("http://gdown.baidu.com/data/wisegame/038d77c9af95d110/jinritoutiao_627.apk");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int responseCode = 200;
                    if(fileSize!=0) {
                        conn.setRequestProperty("Range", "bytes=" + mCruuDownsize + "-" + fileSize);
                        responseCode=206;
                    }else{
                        fileSize=conn.getContentLength();
                    }
                    Log.e("run", "run: "+conn.getResponseCode());
                    if(conn.getResponseCode()==responseCode){

                        InputStream in = conn.getInputStream();
                        RandomAccessFile raf =new RandomAccessFile(downApk,"rw");

                        raf.seek(mCruuDownsize);

                        byte [] buff = new byte[1024];
                        int len =-1;
                        while(isDown && (len=in.read(buff))!=-1){
                            raf.write(buff,0,len);
                            mCruuDownsize+=len;

                            int pro = (int)(mCruuDownsize*100L/fileSize);
                            if(pro!=mCurrProgress){
                                mHandler.sendEmptyMessage(1);
                                mCurrProgress = pro;
                            }
                        }
                        raf.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        isDown= true;
    }


    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     *   
     *      * 获取移动的VIEW，放入对应ViewGroup布局容器  
     *      * @param viewGroup  
     *      * @param view  
     *      * @param initLocation  
     *      * @return  
     *      
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     *   
     *      * 创建移动的ITEM对应的ViewGroup布局容器  
     *      * 用于存放我们移动的View  
     *      
     */
    private ViewGroup getMoveViewGroup() {
        //window中最顶层的view
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     *   
     *      * 点击ITEM移动动画  
     *      *  
     *      * @param moveView  
     *      * @param startLocation  
     *      * @param endLocation  
     *      * @param moveChannel  
     *      * @param clickGridView  
     *      
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final String moveChannel,
                          final GridView clickGridView, final boolean isUser) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标  
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中  
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间  
        //动画配置  
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置  
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // 判断点击的是DragGrid还是OtherGridView  
                if (isUser) {
                    mOtherAdapter.setVisible(true);
                    mOtherAdapter.notifyDataSetChanged();
                    mUserAdapter.remove();
                } else {
                    mUserAdapter.setVisible(true);
                    mUserAdapter.notifyDataSetChanged();
                    mOtherAdapter.remove();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0 && position != 1) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final String channel = (String) ((OtherAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        mOtherAdapter.setVisible(false);
                        //添加到最后一个
                        mOtherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    mOtherGv.getChildAt(mOtherGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, mUserGv, true);
                                    mUserAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final String channel = (String) ((OtherAdapter) parent.getAdapter()).getItem(position);
                    mUserAdapter.setVisible(false);
                    //添加到最后一个
                    mUserAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                mUserGv.getChildAt(mUserGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, mOtherGv, false);
                                mOtherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }
}
