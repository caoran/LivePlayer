package com.wangshuo.wslive.wslivedemo;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.WatermarkFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;

/**
 * https://github.com/WangShuo1143368701/WSLiveDemo
 * 推流的时候需要将推流端的Activity设为横屏，拉流端菜显示满屏
 * <p>
 * me.lake.librestreaming.client.RESClient使用到了restreaming.so
 * me.lake.librestreaming.rtmp.RtmpClient使用到了 resrtmp.so
 *
 * me.lake.librestreaming.ws.StreamConfig 设置 帧速率fps、GOP、码率分辨率
 */
public class LiveActivity extends AppCompatActivity {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption streamAVOption;
    private String rtmpUrl = "rtmp://ossrs.net/" + StatusBarUtils.getRandomAlphaString(3) + '/' + StatusBarUtils.getRandomAlphaDigitString(5);

    private LiveUI mLiveUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        StatusBarUtils.setTranslucentStatus(this);

//        if(createFile()!= null){
//            rtmpUrl = createFile();
//        }


        initLiveConfig();
        mLiveUI = new LiveUI(this, mLiveCameraView, rtmpUrl);
        Log.e("desaco", "rtmpUrl=" + rtmpUrl);
    }

    /**
     * 设置推流参数
     */
    public void initLiveConfig() {
        mLiveCameraView = (StreamLiveCameraView) findViewById(R.id.stream_previewView);

        //参数配置 start ,VIDEO_WIDTH：640*360；VIDEO_BITRATE：600 * 1024;VIDEO_FPS：20；VIDEO_GOP：2（gop 关键帧间隔）
        streamAVOption = new StreamAVOption();
        streamAVOption.streamUrl = rtmpUrl;
        //参数配置 end

        mLiveCameraView.init(this, streamAVOption);
        mLiveCameraView.addStreamStateListener(resConnectionListener);
        ////设置滤镜组
        LinkedList<BaseHardVideoFilter> files = new LinkedList<>();
        files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));
        files.add(new WatermarkFilter(BitmapFactory.decodeResource(getResources(), R.mipmap.live), new Rect(100, 100, 200, 200)));
        mLiveCameraView.setHardVideoFilter(new HardVideoGroupFilter(files));
    }

    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this, "打开推流连接 状态：" + result + " 推流地址：" + rtmpUrl, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWriteError(int errno) {
            Toast.makeText(LiveActivity.this, "推流出错,请尝试重连", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this, "关闭推流连接 状态：" + result, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveCameraView.destroy();
    }
}
