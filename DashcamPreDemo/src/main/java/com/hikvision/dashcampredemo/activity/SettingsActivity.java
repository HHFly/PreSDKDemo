package com.hikvision.dashcampredemo.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.dashcampredemo.R;
import com.hikvision.dashcamsdkpre.AllParamWithChanBO;
import com.hikvision.dashcamsdkpre.AllParamWithoutChanBO;
import com.hikvision.dashcamsdkpre.BaseBO;
import com.hikvision.dashcamsdkpre.FormatStorageDeviceDTO;
import com.hikvision.dashcamsdkpre.GetAllCurrentSettingsBO;
import com.hikvision.dashcamsdkpre.GetDeviceInfoBO;
import com.hikvision.dashcamsdkpre.GetStorageInfoBO;
import com.hikvision.dashcamsdkpre.GetStorageInfoDTO;
import com.hikvision.dashcamsdkpre.SetSettingDTO;
import com.hikvision.dashcamsdkpre.api.ControlApi;
import com.hikvision.dashcamsdkpre.api.GettingApi;
import com.hikvision.dashcamsdkpre.api.SettingApi;
import com.hikvision.dashcamsdkpre.enums.DeviceCapability.PowerOffDelayType;
import com.hikvision.dashcamsdkpre.enums.ImageCapability.VideoResolutionType;
import com.hikvision.dashcamsdkpre.enums.IntelligentCapability.GSensorType;
import com.hikvision.dashcamsdkpre.enums.StorageCapability.ClipDurationType;
import com.hikvision.dashcamsdkpre.listener.DashcamResponseListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SettingsActivity extends AppCompatActivity {
    //缓冲线程池
    private ExecutorService mCacheThreadPool = Executors.newCachedThreadPool();
    //分辨率按钮组
    private RadioGroup rgSwitching;
    //录像时长按钮组
    private RadioGroup rgTime;
    //敏感度按钮组
    private RadioGroup rgSensor;
    //延时关机按钮组
    private RadioGroup rgClose;
    //随手拍按钮组
    private RadioGroup rgCamera;
    //宽动态开关
    private Switch stKdt;
    /**
     * 畸变校正
     */
    private Switch stDistCorr;
    //停车监控开关
    private Switch stRec;
    //录音开关
    private Switch stSwitch;
    //录像开关
    private Switch stRadio;
    //行驶信息叠加开关
    private Switch stPlus;
    //循环覆盖开关
    private Switch stCycle;
    //硬件文本
    private TextView tvYj;
    //固件文本
    private TextView tvGj;
    //总容量文本
    private TextView tvZrl;
    //剩余容量文本
    private TextView tvSyrl;
    //卡状态开关
    private TextView tvKzt;
    //剩余寿命开关
    private TextView tvSysm;
    //格式化按钮
    private Button btFormat;
    //重置开关
    private RelativeLayout rlReset;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rgSwitching = (RadioGroup) findViewById(R.id.rg_switching);
        rgTime = (RadioGroup) findViewById(R.id.rg_time);
        rgSensor = (RadioGroup) findViewById(R.id.rg_sensor);
        rgClose = (RadioGroup) findViewById(R.id.rg_close);
        rgCamera = (RadioGroup) findViewById(R.id.rg_camera);
        stKdt = (Switch) findViewById(R.id.st_kdt);
        stDistCorr = (Switch) findViewById(R.id.st_dist_corr);
        stDistCorr.setOnClickListener(mOnClickListener);
        stRec = (Switch) findViewById(R.id.st_rec);
        stSwitch = (Switch) findViewById(R.id.st_switch);
        stRadio = (Switch) findViewById(R.id.st_radio);
        stPlus = (Switch) findViewById(R.id.st_plus);
        stCycle = (Switch) findViewById(R.id.st_cycle);
        btFormat = (Button) findViewById(R.id.bt_format);
        rlReset = (RelativeLayout) findViewById(R.id.rl_reset);
        tvYj = (TextView) findViewById(R.id.tv_yj);
        tvGj = (TextView) findViewById(R.id.tv_gj);
        tvZrl = (TextView) findViewById(R.id.tv_zrl);
        tvSyrl = (TextView) findViewById(R.id.tv_syrl);
        tvKzt = (TextView) findViewById(R.id.tv_kzt);
        tvSysm = (TextView) findViewById(R.id.tv_sysm);
        findViewById(R.id.rb_high).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_low).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_one_min).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_three_min).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_five_min).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_off).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_lower).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_mid).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_higher).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_close).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_ten).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_six).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_one).setOnClickListener(mOnClickListener);
        findViewById(R.id.rb_five).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_kdt).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_rec).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_switch).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_radio).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_plus).setOnClickListener(mOnClickListener);
        findViewById(R.id.st_cycle).setOnClickListener(mOnClickListener);
        findViewById(R.id.bt_format).setOnClickListener(mOnClickListener);
        findViewById(R.id.rl_reset).setOnClickListener(mOnClickListener);
        findViewById(R.id.rl_shutdown).setOnClickListener(mOnClickListener);
        findViewById(R.id.rl_reboot).setOnClickListener(mOnClickListener);
        ImageButton ibBack_main = (ImageButton) findViewById(R.id.ib_back_main);
        //返回上层界面
        ibBack_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                //获取类指令
                GettingApi.getAllCurrentSettings(new DashcamResponseListener<GetAllCurrentSettingsBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetAllCurrentSettingsBO getAllCurrentSettingsBO) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 通道相关
                                List<AllParamWithChanBO> allParamWithChanBOList = getAllCurrentSettingsBO.getAllParamWithChanBOList();
                                if (allParamWithChanBOList != null && allParamWithChanBOList.size() > 0) {
                                    AllParamWithChanBO allParamWithChanBO = allParamWithChanBOList.get(0);

                                    VideoResolutionType resolutionType = allParamWithChanBO.getResolution();
                                    if (resolutionType != null) {
                                        switch (resolutionType) {
                                            //高分辨率
                                            case RESOLUTION_0:
                                                rgSwitching.check(R.id.rb_high);
                                                break;
                                            //低分辨率
                                            case RESOLUTION_1:
                                                rgSwitching.check(R.id.rb_low);
                                                break;
                                        }
                                    }

                                    Boolean soundSwitch = allParamWithChanBO.isSoundSwitch();
                                    if (soundSwitch != null) {
                                        if (soundSwitch) {
                                            stSwitch.setChecked(true);
                                        } else {
                                            stSwitch.setChecked(false);
                                        }
                                    }

                                    Boolean wdrSwitch = allParamWithChanBO.isWDRSwitch();
                                    if (wdrSwitch != null) {
                                        if (wdrSwitch) {
                                            stKdt.setChecked(true);
                                        } else {
                                            stKdt.setChecked(false);
                                        }
                                    }

                                    Boolean distCorrSwitch = allParamWithChanBO.isDistCorrSwitch();
                                    if (distCorrSwitch != null) {
                                        if (distCorrSwitch) {
                                            stDistCorr.setChecked(true);
                                        } else {
                                            stDistCorr.setChecked(false);
                                        }
                                    }

                                    Boolean osdSwitch = allParamWithChanBO.isOSDSwitch();
                                    if (osdSwitch != null) {
                                        if (osdSwitch) {
                                            stPlus.setChecked(true);
                                        } else {
                                            stPlus.setChecked(false);
                                        }
                                    }
                                }

                                // 通道无关
                                AllParamWithoutChanBO allParamWithoutChanBO = getAllCurrentSettingsBO.getAllParamWithoutChanBO();

                                ClipDurationType clipDurationType = allParamWithoutChanBO.getClipDuration();
                                if (clipDurationType != null) {
                                    switch (clipDurationType) {
                                        //一分钟时长
                                        case CLIP_DURATION_ONE_MINUTE:
                                            rgTime.check(R.id.rb_one_min);
                                            break;
                                        //三分钟时长
                                        case CLIP_DURATION_THREE_MINUTES:
                                            rgTime.check(R.id.rb_three_min);
                                            break;
                                        //五分钟时长
                                        case CLIP_DURATION_FIVE_MINUTES:
                                            rgTime.check(R.id.rb_five_min);
                                            break;
                                    }
                                }

                                PowerOffDelayType powerOffDelayType = allParamWithoutChanBO.getPowerOffDelay();
                                if (powerOffDelayType != null) {
                                    switch (powerOffDelayType) {
                                        //关闭
                                        case SHUTDOWN_DELAY_OFF:
                                            rgClose.check(R.id.rb_close);
                                            break;
                                        //十秒
                                        case SHUTDOWN_DELAY_TEN_SECONDS:
                                            rgClose.check(R.id.rb_ten);
                                            break;
                                        //一分钟
                                        case SHUTDOWN_DELAY_SIXTY_SECONDS:
                                            rgClose.check(R.id.rb_six);
                                            break;
                                    }
                                }

                                Boolean parkSwitch = allParamWithoutChanBO.isParkMonitorSwith();
                                if (parkSwitch != null) {
                                    if (parkSwitch) {
                                        stRec.setChecked(true);
                                    } else {
                                        stRec.setChecked(false);
                                    }
                                }

                                Boolean recordSwitch = allParamWithoutChanBO.isRecordSwitch();
                                if (recordSwitch != null) {
                                    if (recordSwitch) {
                                        stRadio.setChecked(true);
                                    } else {
                                        stRadio.setChecked(false);
                                    }
                                }

                                Boolean eventSwitch = allParamWithoutChanBO.isEventRecordCycleSwitch();
                                if (eventSwitch != null) {
                                    if (eventSwitch) {
                                        stCycle.setChecked(true);
                                    } else {
                                        stCycle.setChecked(false);
                                    }
                                }

                                GSensorType gSensorType = allParamWithoutChanBO.getGSensorSensitivity();
                                if (gSensorType != null) {
                                    switch (gSensorType) {
                                        case SENSOR_TYPE_OFF:
                                            rgSensor.check(R.id.rb_off);
                                            break;
                                        //低敏感度
                                        case SENSOR_TYPE_LOW:
                                            rgSensor.check(R.id.rb_lower);
                                            break;
                                        //中敏感度
                                        case SENSOR_TYPE_MIDDLE:
                                            rgSensor.check(R.id.rb_mid);
                                            break;
                                        //高敏感度
                                        case SENSOR_TYPE_HIGH:
                                            rgSensor.check(R.id.rb_higher);
                                            break;
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                    }
                });
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                //获取类指令
                GettingApi.getDeviceInfo(new DashcamResponseListener<GetDeviceInfoBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetDeviceInfoBO getDeviceInfoBO) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String hardwareVer = getDeviceInfoBO.getHardwareVer();
                                tvYj.setText(String.format("硬件版本号:%s", hardwareVer));
                                String firmVer = getDeviceInfoBO.getFirmVer();
                                tvGj.setText(String.format("固件版本号:%s", firmVer));
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                    }
                });
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                GetStorageInfoDTO dto = new GetStorageInfoDTO();
                dto.setDriver(1);
                //获取类指令
                GettingApi.getStorageInfo(dto, new DashcamResponseListener<GetStorageInfoBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetStorageInfoBO getStorageInfoBO) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                int totalSpace = getStorageInfoBO.getTotalSpace();
                                int freeSpace = getStorageInfoBO.getFreeSpace();
                                String residualLife = getStorageInfoBO.getResidualLife();
                                String healthStatus = getStorageInfoBO.getHealthStatus();
                                tvZrl.setText(String.format("总容量：%d", totalSpace));
                                tvSyrl.setText(String.format("剩余容量：%d", freeSpace));
                                tvKzt.setText(String.format("卡状态：%s", healthStatus));
                                tvSysm.setText(String.format("剩余寿命：%s", residualLife));
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                    }
                });
            }
        };
        mCacheThreadPool.execute(runnable1);
        mCacheThreadPool.execute(runnable2);
        mCacheThreadPool.execute(runnable3);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (R.id.rb_high == id) {
                //高分辨率单选框
                SetSettingDTO dto1 = new SetSettingDTO();
                dto1.setChanNo(1);
                dto1.setVideoResolution(VideoResolutionType.RESOLUTION_0);
                SettingApi.setParam(dto1, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_low == id) {
                //低分辨率单选框
                SetSettingDTO dto2 = new SetSettingDTO();
                dto2.setChanNo(1);
                dto2.setVideoResolution(VideoResolutionType.RESOLUTION_1);
                SettingApi.setParam(dto2, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_one_min == id) {
                //一分钟单选框
                SetSettingDTO dto3 = new SetSettingDTO();
                dto3.setChanNo(1);
                dto3.setClipDuration(ClipDurationType.CLIP_DURATION_ONE_MINUTE);
                SettingApi.setParam(dto3, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_three_min == id) {
                //三分钟单选框
                SetSettingDTO dto4 = new SetSettingDTO();
                dto4.setChanNo(1);
                dto4.setClipDuration(ClipDurationType.CLIP_DURATION_THREE_MINUTES);
                SettingApi.setParam(dto4, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_five_min == id) {
                //五分钟单选框
                SetSettingDTO dto5 = new SetSettingDTO();
                dto5.setChanNo(1);
                dto5.setClipDuration(ClipDurationType.CLIP_DURATION_FIVE_MINUTES);
                SettingApi.setParam(dto5, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_off == id) {
                // 关闭G-Sensor
                SetSettingDTO dto = new SetSettingDTO();
                dto.setChanNo(1);
                dto.setGSensor(GSensorType.SENSOR_TYPE_OFF);
                SettingApi.setParam(dto, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_lower == id) {
                //低敏感度单选框
                SetSettingDTO dto6 = new SetSettingDTO();
                dto6.setChanNo(1);
                dto6.setGSensor(GSensorType.SENSOR_TYPE_LOW);
                SettingApi.setParam(dto6, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_mid == id) {
                //中敏感度单选框
                SetSettingDTO dto7 = new SetSettingDTO();
                dto7.setChanNo(1);
                dto7.setGSensor(GSensorType.SENSOR_TYPE_MIDDLE);
                SettingApi.setParam(dto7, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_higher == id) {
                //高敏感度单选框
                SetSettingDTO dto8 = new SetSettingDTO();
                dto8.setChanNo(1);
                dto8.setGSensor(GSensorType.SENSOR_TYPE_HIGH);
                SettingApi.setParam(dto8, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_close == id) {
                //关闭单选框
                SetSettingDTO dto9 = new SetSettingDTO();
                dto9.setChanNo(1);
                dto9.setPowerOffDelay(PowerOffDelayType.SHUTDOWN_DELAY_OFF);
                SettingApi.setParam(dto9, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_ten == id) {
                //十秒单选框
                SetSettingDTO dto10 = new SetSettingDTO();
                dto10.setChanNo(1);
                dto10.setPowerOffDelay(PowerOffDelayType.SHUTDOWN_DELAY_TEN_SECONDS);
                SettingApi.setParam(dto10, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_six == id) {
                //一分钟单选框
                SetSettingDTO dto11 = new SetSettingDTO();
                dto11.setChanNo(1);
                dto11.setPowerOffDelay(PowerOffDelayType.SHUTDOWN_DELAY_SIXTY_SECONDS);
                SettingApi.setParam(dto11, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.rb_one == id) {
                //单拍单选框
                SetSettingDTO dto12 = new SetSettingDTO();
                dto12.setChanNo(1);
                dto12.setPhotoRandom(1);
                SettingApi.setParam(dto12, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {

                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {

                    }
                });
            } else if (R.id.rb_five == id) {
                //五连拍单选框
                SetSettingDTO dto13 = new SetSettingDTO();
                dto13.setChanNo(1);
                dto13.setPhotoRandom(5);
                SettingApi.setParam(dto13, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {

                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {

                    }
                });
            } else if (R.id.st_kdt == id) {
                //宽动态开关
                SetSettingDTO dto14 = new SetSettingDTO();
                dto14.setChanNo(1);
                if (stKdt.isChecked()) {
                    dto14.setWdrSwitch(true);
                } else {
                    dto14.setWdrSwitch(false);
                }
                SettingApi.setParam(dto14, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.st_dist_corr == id) {
                //宽动态开关
                SetSettingDTO dto = new SetSettingDTO();
                dto.setChanNo(1);
                dto.setDistCorr(stDistCorr.isChecked());
                SettingApi.setParam(dto, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.st_rec == id) {
                //停车监控开关
                SetSettingDTO dto15 = new SetSettingDTO();
                dto15.setChanNo(1);
                if (stRec.isChecked()) {
                    dto15.setParkMonitorSwitch(true);
                } else {
                    dto15.setParkMonitorSwitch(false);
                }
                SettingApi.setParam(dto15, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.st_switch == id) {
                //录音开关
                SetSettingDTO dto16 = new SetSettingDTO();
                dto16.setChanNo(1);
                if (stSwitch.isChecked()) {
                    dto16.setSoundSwitch(true);
                } else {
                    dto16.setSoundSwitch(false);
                }
                SettingApi.setParam(dto16, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.st_radio == id) {
                //录像记录开关
                SetSettingDTO dto17 = new SetSettingDTO();
                dto17.setChanNo(1);
                if (stRadio.isChecked()) {
                    dto17.setRecordSwitch(true);
                } else {
                    dto17.setRecordSwitch(false);
                }
                SettingApi.setParam(dto17, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.st_plus == id) {
                //行驶信息叠加开关
                SetSettingDTO dto18 = new SetSettingDTO();
                dto18.setChanNo(1);
                if (stPlus.isChecked()) {
                    dto18.setSupportOSD(true);
                } else {
                    dto18.setSupportOSD(false);
                }
                SettingApi.setParam(dto18, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {

                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {

                    }
                });
            } else if (R.id.st_cycle == id) {
                //循环覆盖开关
                SetSettingDTO dto19 = new SetSettingDTO();
                dto19.setChanNo(1);
                if (stCycle.isChecked()) {
                    dto19.setEventRecordCycle(true);
                } else {
                    dto19.setEventRecordCycle(false);
                }
                SettingApi.setParam(dto19, new DashcamResponseListener<BaseBO>() {
                    @Override
                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, getString(R.string.setting_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(SettingsActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (R.id.bt_format == id) {
                //格式化按钮
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(R.string.format);
                builder.setPositiveButton(R.string.qd, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Runnable runnable4 = new Runnable() {
                            @Override
                            public void run() {
                                FormatStorageDeviceDTO dto = new FormatStorageDeviceDTO();
                                dto.setDriver(1);
                                ControlApi.formatStorageDevice(dto, new DashcamResponseListener<BaseBO>() {
                                    @Override
                                    public void onDashcamResponseSuccess(final BaseBO baseBO) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SettingsActivity.this, R.string.succformat, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDashcamResponseFailure(BaseBO baseBO) {
                                    }
                                });
                            }
                        };
                        mCacheThreadPool.execute(runnable4);

                    }
                });
                builder.setNegativeButton(R.string.qx, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            } else if (R.id.rl_reset == id) {
                //重置开关
                AlertDialog.Builder reset = new AlertDialog.Builder(SettingsActivity.this);
                reset.setTitle(R.string.resetsettings);
                reset.setPositiveButton(R.string.setcer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Runnable runnable5 = new Runnable() {
                            @Override
                            public void run() {
                                ControlApi.factoryReset(new DashcamResponseListener<BaseBO>() {
                                    @Override
                                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SettingsActivity.this, R.string.successreset, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDashcamResponseFailure(BaseBO baseBO) {
                                    }
                                });

                            }
                        };
                        mCacheThreadPool.execute(runnable5);

                    }
                });
                reset.setNegativeButton(R.string.cancelreset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                reset.create().show();
            } else if (R.id.rl_shutdown == id) {
                showShutdownDialog();
            } else if (R.id.rl_reboot == id) {
                showRebootDialog();
            }
        }
    };

    /**
     * 设备关机
     */
    private void showShutdownDialog() {
        AlertDialog.Builder reset = new AlertDialog.Builder(SettingsActivity.this);
        reset.setTitle(R.string.shutdown);
        reset.setPositiveButton(R.string.setcer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Runnable runnable5 = new Runnable() {
                    @Override
                    public void run() {
                        ControlApi.systemShutdown(new DashcamResponseListener<BaseBO>() {
                            @Override
                            public void onDashcamResponseSuccess(BaseBO baseBO) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SettingsActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(BaseBO baseBO) {
                            }
                        });

                    }
                };
                mCacheThreadPool.execute(runnable5);

            }
        });
        reset.setNegativeButton(R.string.cancelreset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        reset.create().show();
    }

    /**
     * 设备重启
     */
    private void showRebootDialog() {
        AlertDialog.Builder reset = new AlertDialog.Builder(SettingsActivity.this);
        reset.setTitle(R.string.reboot);
        reset.setPositiveButton(R.string.setcer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Runnable runnable5 = new Runnable() {
                    @Override
                    public void run() {
                        ControlApi.systemReboot(new DashcamResponseListener<BaseBO>() {
                            @Override
                            public void onDashcamResponseSuccess(BaseBO baseBO) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SettingsActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(BaseBO baseBO) {
                            }
                        });

                    }
                };
                mCacheThreadPool.execute(runnable5);

            }
        });
        reset.setNegativeButton(R.string.cancelreset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        reset.create().show();
    }
}
