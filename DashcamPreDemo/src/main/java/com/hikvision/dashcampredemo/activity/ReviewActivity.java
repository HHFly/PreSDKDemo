package com.hikvision.dashcampredemo.activity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hikvision.dashcampredemo.R;
import com.hikvision.playerlibrary.HikLog;
import com.hikvision.playerlibrary.HikRecordPlayer;
import com.hikvision.playerlibrary.HikVideoCallBack;
import com.hikvision.playerlibrary.HikVideoConstant;
import com.hikvision.playerlibrary.HikVideoModel;

import java.text.DecimalFormat;

public class ReviewActivity extends AppCompatActivity implements HikVideoCallBack {
    //回看播放器
    private HikRecordPlayer hikRecordPlayer;
    //获取数据
    private Intent intent;
    //文件路径
    private String url;
    //控制暂停和恢复的控件
    private ImageButton ibPlay;
    //加载视频的控件
    private TextureView tvReview;
    //显示当前进度的控件
    private TextView tvNow;
    //显示总时间的控件
    private TextView tvTotal;
    //拖动进度条
    private SeekBar sbReview;
    //计数器
    private int count;
    //开始播放的时间点
    private long playedTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        sbReview = (SeekBar) findViewById(R.id.sb_review);
        //拖动进度条改变状态的监听器
        sbReview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //状态改变
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = seekBar.getProgress();
                playedTime = progress;
            }

            @Override
            //开始拖动
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            //停止拖动
            public void onStopTrackingTouch(SeekBar seekBar) {
//                hikRecordPlayer.playOnline(tvReview, playedTime, url, true);
                ibPlay.setBackground(getResources().getDrawable(R.drawable.photo_view_mid_pause));
            }
        });
        tvNow = (TextView) findViewById(R.id.tv_now);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        intent = getIntent();
        url = intent.getStringExtra("extra");
        hikRecordPlayer = new HikRecordPlayer();
        //回到上层界面的组件
        ImageButton ibReturn = (ImageButton) findViewById(R.id.ib_return);
        ibReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvReview = (TextureView) findViewById(R.id.tv_review);
        //加载视频的监听器
        tvReview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                hikRecordPlayer.playOnline(tvReview, 0, url, false);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        //回看播放器的回调
//        hikRecordPlayer.setCallback(new HikRecordPlayer.Callback() {
//            @Override
//            public void onFail(int i, String s) {
//
//            }
//
//            @Override
//            public void onPause(int i, String s) {
//
//            }
//
//            @Override
//            public void onResume(int i, String s) {
//
//            }
//
//            @Override
//            public void onEnd() {
//
//            }
//
//            @Override
//            public void onProgressChanged(final long l, final long l1) {
//                //主线程运行
//                runOnUiThread(new Runnable() {
//                    @SuppressLint("DefaultLocale")
//                    @Override
//                    public void run() {
//                        long hour1 = l / 3600;
//                        long minute1 = (l % 3600) / 60;
//                        long second1 = (l % 3600) % 60;
//                        long hour2 = l1 / 3600;
//                        long minute2 = (l1 % 3600) / 60;
//                        long second2 = (l1 % 3600) % 60;
//                        if (second1 < 10) {
//                            tvNow.setText(String.format("0%d:0%d:0%d", hour1, minute1, second1));
//                        } else {
//                            tvNow.setText(String.format("0%d:0%d:%d", hour1, minute1, second1));
//                        }
//                        if (second2 < 10) {
//                            tvTotal.setText(String.format("0%d:0%d:0%d", hour2, minute2, second2));
//                        } else {
//                            tvTotal.setText(String.format("0%d:0%d:%d", hour2, minute2, second2));
//                        }
//                        //设置拖动进度条的进度
//                        sbReview.setProgress((int) l);
//                        //设置拖动进度条的最大值
//                        sbReview.setMax((int) l1);
//                    }
//                });
//            }
//
//            @Override
//            public void onStopSuccess() {
//
//            }
//
//            @Override
//            public void onStartSuccess() {
//
//            }
//        });
        ibPlay = (ImageButton) findViewById(R.id.ib_play);
        count = 0;
        //暂停和恢复的点击响应
        ibPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                count++;
                //奇数次点击暂停播放，偶数次点击恢复播放
                if (count % 2 == 1) {
                    v.setBackground(getResources().getDrawable(R.drawable.photo_view_mid_play));
                    hikRecordPlayer.pausePlay();
                } else if (count != 0 && count % 2 == 0) {
                    v.setBackground(getResources().getDrawable(R.drawable.photo_view_mid_pause));
                    hikRecordPlayer.resumePlay();
                }
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private HikRecordPlayer mRecordPlayer;
    private boolean isPaused;
    private long mTotalTime;

    private void startPlay() {
        mRecordPlayer = new HikRecordPlayer();
        mRecordPlayer.setVideoCallBack(this);
        HikVideoModel videoModel = new HikVideoModel();
        videoModel.setTextureView(tvReview);
        videoModel.setUrl(url);
//        videoModel.setUrl("http://192.168.42.1/SMCAR/DOWNLOAD/tmp/SD0/DCIM/ch1_20170101_045431_0200.mp4");
//        videoModel.setPlaySound(false);
        videoModel.setHardDecode(true);
        videoModel.setPackageFormat(1); // 0:PS 1:MP4
        mRecordPlayer.playOnline(videoModel);
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (mRecordPlayer != null) {
            mRecordPlayer.stopPlay();
        }
    }


    @Override
    public void onVideoSuccess(int msgID, String msg) {
        switch (msgID) {
            case HikVideoConstant.PLAYER_START_PLAY_SUCCESS:
                if (mRecordPlayer == null) {
                    return;
                }
                long totalTime = mRecordPlayer.getTotalTime();
                tvTotal.setText(formatLongToTimeStr(totalTime));
                sbReview.setMax((int) totalTime);
                break;
            case HikVideoConstant.PLAYER_ON_DISPLAY:
                if (mRecordPlayer == null) {
                    return;
                }
                int playedTime = mRecordPlayer.getPlayedTime();
                tvNow.setText(formatLongToTimeStr((long) playedTime));
                sbReview.setProgress(playedTime);
                break;
        }
    }

    @Override
    public void onVideoFailure(int i, String s, int i1) {
        HikLog.infoLog("test111", i + " " + s + " " + i1);
    }

    public static String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = l.intValue();

        if (second < 0)
            second = 0;

        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return (decimalFormat.format(hour) + ":" + decimalFormat.format(minute) + ":" + decimalFormat.format(second));
    }
}
