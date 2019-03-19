package com.hikvision.dashcampredemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hikvision.dashcampredemo.R;
import com.hikvision.dashcampredemo.adapter.AlbumAdapter;
import com.hikvision.dashcampredemo.member.AlbumItem;
import com.hikvision.dashcampredemo.member.GlobalInfo;
import com.hikvision.dashcamsdkpre.BaseBO;
import com.hikvision.dashcamsdkpre.DeleteFileListDTO;
import com.hikvision.dashcamsdkpre.FileInfoBO;
import com.hikvision.dashcamsdkpre.GetFileListBO;
import com.hikvision.dashcamsdkpre.GetFileListDTO;
import com.hikvision.dashcamsdkpre.MoveFileListDTO;
import com.hikvision.dashcamsdkpre.api.GettingApi;
import com.hikvision.dashcamsdkpre.enums.MediaType;
import com.hikvision.dashcamsdkpre.listener.DashcamResponseListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListActivity extends AppCompatActivity {
    //缓冲线程池
    private ExecutorService mCacheThreadPool = Executors.newCachedThreadPool();
    //智能刷新加载布局
    private SmartRefreshLayout srlRefresh;
    //网格试图
    private GridView gvList;
    //适配器
    private AlbumAdapter albumAdapter;
    //意图
    private Intent intent;
    //路径
    private String path;
    //列表项集合
    private List<AlbumItem> list = new ArrayList<>();
    //索引
    private int index = 0;
    //选择按钮
    private Button btChoose;
    //完成按钮
    private Button btComplete;
    //删除按钮
    private Button btDel;
    //编辑模式
    private boolean EditMode;

    private MediaType mMediaType;
    private MediaType mDstType;
    private String mLastFileName;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        btChoose = (Button) findViewById(R.id.bt_choose);
        //选择按钮响应事件，点击进入编辑模式，显示完成按钮
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMode = true;
                btChoose.setVisibility(View.GONE);
                btComplete.setVisibility(View.VISIBLE);
            }
        });
        //完成按钮响应事件，点击退出编辑模式，显示选择按钮
        btComplete = (Button) findViewById(R.id.bt_complete);
        btComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMode = false;
                btChoose.setVisibility(View.VISIBLE);
                btComplete.setVisibility(View.GONE);
                AlbumItem albumItem;
                for (int i = 0; i < list.size(); i++) {
                    albumItem = list.get(i);
                    if (albumItem.ismChecked()) {
                        albumItem.setmChecked(false);
                    }
                }
                albumAdapter.notifyDataSetChanged();
            }
        });
        btDel = (Button) findViewById(R.id.bt_del);
        //删除按钮响应事件，点击删除文件
        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框确认是否删除
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle(R.string.cerdel);
                builder.setPositiveButton(R.string.dele, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (list.size() == 0) {
                            Toast.makeText(ListActivity.this, R.string.select_file_first, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Runnable runnable4 = new Runnable() {
                            @Override
                            public void run() {
                                DeleteFileListDTO deleteFileListDTO = new DeleteFileListDTO();
                                List<String> fileList = new ArrayList<>();
                                AlbumItem albumItem;
                                for (int i = 0; i < list.size(); i++) {
                                    albumItem = list.get(i);
                                    if (albumItem.ismChecked()) {
                                        fileList.add(albumItem.getmFileName());
                                    }
                                }
                                deleteFileListDTO.setFileList(fileList);
                                GettingApi.deleteFileList(deleteFileListDTO, new DashcamResponseListener<BaseBO>() {
                                    @Override
                                    public void onDashcamResponseSuccess(BaseBO baseBO) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(ListActivity.this, R.string.sucdel, Toast.LENGTH_SHORT).show();
                                                refresh();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onDashcamResponseFailure(BaseBO baseBO) {
                                        Toast.makeText(ListActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        };
                        mCacheThreadPool.execute(runnable4);
                    }
                });
                builder.setNegativeButton(R.string.cac, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });
        findViewById(R.id.bt_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveFileList();
            }
        });
        intent = getIntent();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                GetFileListDTO dto = new GetFileListDTO();
                dto.setDriver(1);
                dto.setLastFileName("");
                dto.setPageNumber(20);
                String extra = intent.getStringExtra("extra");
                //根据传过来的字符串类型和文件类型来获取对应的路径
                switch (extra) {
                    case "event":
                        mMediaType = MediaType.EVENT_VIDEO;
                        break;
                    case "cycle":
                        mMediaType = MediaType.NORMAL_VIDEO;
                        break;
                    case "user":
                        mMediaType = MediaType.USER_DATA;
                        break;
                }
                dto.setMediaType(mMediaType);

                GettingApi.getFileList(dto, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //文件列表
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList == null || fileList.size() == 0) {
                                    Toast.makeText(ListActivity.this, getString(R.string.file_empty), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                //foreach循环
                                for (FileInfoBO fileInfoBO : fileList) {
                                    String fileName = fileInfoBO.getFileName();
                                    //文件路径为空也跳过去
                                    if (TextUtils.isEmpty(fileName)) {
                                        continue;
                                    }
                                    AlbumItem albumItem = new AlbumItem();
                                    albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                    albumItem.setmFileName(fileInfoBO.getFileName());
                                    list.add(albumItem);
                                }
                                //列表加入到适配器里面
                                albumAdapter.setmItems(list);
                                //通知适配器数据改变
                                albumAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(ListActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        mCacheThreadPool.execute(runnable1);
        gvList = (GridView) findViewById(R.id.gv_list);
        albumAdapter = new AlbumAdapter(this);
        //网格视图加入适配器
        gvList.setAdapter(albumAdapter);
        //网格视图设置列表项点击监听
        gvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取列表项
                AlbumItem albumItem = list.get(position);
                //编辑模式点击选择列表项，再次点击取消选择，非编辑模式点击图片进入图片加载，点击视频进入视频播放
                if (EditMode) {
                    albumItem.setmChecked(!albumItem.ismChecked());
                    albumAdapter.notifyDataSetChanged();
                } else {
                    if (albumItem.getmFileName().endsWith(".jpg")) {
                        Intent intentPhoto = new Intent(ListActivity.this, PhotoActivity.class);
                        intentPhoto.putExtra("url", albumItem.getmThumbnailUrl());
                        startActivity(intentPhoto);
                    } else {
                        Intent intentReview = new Intent(ListActivity.this, ReviewActivity.class);
                        intentReview.putExtra("extra", GlobalInfo.sDownloadPath + albumItem.getmFileName());
                        startActivity(intentReview);
                    }
                }
            }
        });
        ImageButton ibBack = (ImageButton) findViewById(R.id.ib_back);
        //返回上层界面
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        srlRefresh = (SmartRefreshLayout) findViewById(R.id.srl_refresh);
        srlRefresh.setEnableLoadmore(true);
        srlRefresh.setEnableAutoLoadmore(false);
        //下拉刷新上拉加载监听器
        srlRefresh.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refresh();
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore();
            }
        });
    }

    //上拉加载
    private void loadMore() {
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                GetFileListDTO getFileListDTO = new GetFileListDTO();
                getFileListDTO.setLastFileName(mLastFileName);
                getFileListDTO.setDriver(1);
                getFileListDTO.setPageNumber(20);
                getFileListDTO.setMediaType(mMediaType);
                GettingApi.getFileList(getFileListDTO, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList != null && fileList.size() > 0) {
                                    mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                    for (FileInfoBO fileInfoBO : fileList) {
                                        String fileName = fileInfoBO.getFileName();
                                        if (TextUtils.isEmpty(fileName)) {
                                            continue;
                                        }
                                        AlbumItem albumItem = new AlbumItem();
                                        albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                        albumItem.setmFileName(fileInfoBO.getFileName());
                                        list.add(albumItem);
                                    }
                                    albumAdapter.setmItems(list);
                                    albumAdapter.notifyDataSetChanged();
                                }
                                //结束加载
                                srlRefresh.finishLoadmore();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(ListActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mCacheThreadPool.execute(runnable2);
    }

    //下拉刷新
    private void refresh() {
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                //清空列表
                list.clear();
                index = 0;
                GetFileListDTO getFileListDTO = new GetFileListDTO();
                getFileListDTO.setLastFileName("");
                getFileListDTO.setDriver(1);
                getFileListDTO.setPageNumber(20);
                getFileListDTO.setMediaType(mMediaType);
                GettingApi.getFileList(getFileListDTO, new DashcamResponseListener<GetFileListBO>() {
                    @Override
                    public void onDashcamResponseSuccess(final GetFileListBO getFileListBO) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<FileInfoBO> fileList = getFileListBO.getFileList();
                                if (fileList != null && fileList.size() > 0) {
                                    mLastFileName = fileList.get(fileList.size() - 1).getFileName();
                                    for (FileInfoBO fileInfoBO : fileList) {
                                        String fileName = fileInfoBO.getFileName();
                                        if (TextUtils.isEmpty(fileName)) {
                                            continue;
                                        }
                                        AlbumItem albumItem = new AlbumItem();
                                        albumItem.setmThumbnailUrl(GlobalInfo.sDownloadPath + fileInfoBO.getFileThumbnail());
                                        albumItem.setmFileName(fileInfoBO.getFileName());
                                        list.add(albumItem);
                                    }
                                    albumAdapter.setmItems(list);
                                    albumAdapter.notifyDataSetChanged();
                                }
                                //结束刷新
                                srlRefresh.finishRefresh();
                            }
                        });
                    }

                    @Override
                    public void onDashcamResponseFailure(BaseBO baseBO) {
                        Toast.makeText(ListActivity.this, baseBO.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        mCacheThreadPool.execute(runnable3);
    }

    /**
     * 文件移动
     */
    private void moveFileList() {
        if (MediaType.EVENT_VIDEO == mMediaType) {
            mDstType = MediaType.NORMAL_VIDEO;
        } else if (MediaType.NORMAL_VIDEO == mMediaType) {
            mDstType = MediaType.EVENT_VIDEO;
        } else {
            Toast.makeText(ListActivity.this, R.string.can_not_move, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setTitle(R.string.cermove);
        builder.setPositiveButton(R.string.move, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (list.size() == 0) {
                    Toast.makeText(ListActivity.this, R.string.select_file_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                Runnable runnable4 = new Runnable() {
                    @Override
                    public void run() {
                        List<String> fileList = new ArrayList<>();
                        AlbumItem albumItem;
                        for (int i = 0; i < list.size(); i++) {
                            albumItem = list.get(i);
                            if (albumItem.ismChecked()) {
                                fileList.add(albumItem.getmFileName());
                            }
                        }
                        MoveFileListDTO moveFileListDTO = new MoveFileListDTO();
                        moveFileListDTO.setDstDriver(1);
                        moveFileListDTO.setDstType(mDstType);
                        moveFileListDTO.setFileList(fileList);

                        GettingApi.moveFileList(moveFileListDTO, new DashcamResponseListener<BaseBO>() {
                            @Override
                            public void onDashcamResponseSuccess(BaseBO bo) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ListActivity.this, R.string.sucmove, Toast.LENGTH_SHORT).show();
                                        refresh();
                                    }
                                });
                            }

                            @Override
                            public void onDashcamResponseFailure(BaseBO bo) {
                                Toast.makeText(ListActivity.this, bo.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                mCacheThreadPool.execute(runnable4);
            }
        });
        builder.setNegativeButton(R.string.cac, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

}
