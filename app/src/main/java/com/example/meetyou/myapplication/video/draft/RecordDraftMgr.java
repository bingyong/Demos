package com.example.meetyou.myapplication.video.draft;

import android.content.Context;
import android.text.TextUtils;

import com.example.meetyou.myapplication.video.bean.RecordDraftInfo;
import com.example.meetyou.myapplication.video.utils.SharedPreferencesHelper;
import com.example.meetyou.myapplication.video.utils.TCConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.liteav.basic.log.TXCLog;

import java.util.List;

/**
 * Created by vinsonswang on 2018/9/4.
 * <p>
 * 录制草稿箱管理类
 * 必须保证草稿箱中的分段以及将要录制的分段参数一致！
 * <p>
 * 一、最近一次的草稿箱
 * SharedPreferencesHelper中的 SP_KEY_RECORD_LAST_DRAFT 里保存最近录制的数据json格式如下，对应草稿箱实体类：RecordDraftInfo：
 * {
 * "aspectRatio": 0,
 * "partList":
 * [
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172350252.mp4"
 * },
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172353808.mp4"
 * },
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172413134.mp4"
 * }
 * ]
 * }
 * <p>
 * <p>
 * 二、历史草稿箱
 * SharedPreferencesHelper中的 SP_KEY_RECORD_HISTORY_DRAFT
 * 里可以保存多次历史录制任务数据json格式如下，对应草稿箱实体类：HistoryRecordDraftInfo：
 * {
 * "historyDraftInfo":
 * [
 * {
 * "aspectRatio": 0,
 * "partList":
 * [
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172350252.mp4"
 * },
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172353808.mp4"
 * }
 * ]
 * },
 * {
 * "aspectRatio": 0,
 * "partList":
 * [
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172350252.mp4"
 * },
 * {
 * "path": "/storage/emulated/0/TXUGC/TXUGCParts/temp_TXUGC_20180923_172353808.mp4"
 * }
 * ]
 * }
 * ]
 * }
 */

public class RecordDraftMgr {
    private final String TAG = "RecordDraftMgr";
    public static RecordDraftMgr instance;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    public RecordDraftMgr(Context context) {
        mSharedPreferencesHelper = new SharedPreferencesHelper(context, TCConstants.SP_NAME_RECORD);
    }

    public void setLastAspectRatio(int aspectRatio) {
        RecordDraftInfo recordDraftInfo = getLastDraftInfo();
        if (recordDraftInfo == null) {
            recordDraftInfo = new RecordDraftInfo();
        }
        recordDraftInfo.setAspectRatio(aspectRatio);
        saveLastDraft(recordDraftInfo);
    }

    /**
     * 保存最近录制的草稿的最新一段
     */
    public void saveLastPart(String partPath) {
        if (TextUtils.isEmpty(partPath)) {
            return;
        }

        RecordDraftInfo recordDraftInfo = getLastDraftInfo();
        if (recordDraftInfo == null) {
            recordDraftInfo = new RecordDraftInfo();
        }

        RecordDraftInfo.RecordPart recordPart = recordDraftInfo.new RecordPart();
        recordPart.setPath(partPath);
        recordDraftInfo.getPartList().add(recordPart);

        saveLastDraft(recordDraftInfo);
    }

    private void saveLastDraft(RecordDraftInfo recordDraftInfo) {
        Gson gson = new Gson();
        String recordDraftStr = gson.toJson(recordDraftInfo);
        mSharedPreferencesHelper.put(TCConstants.SP_KEY_RECORD_LAST_DRAFT, recordDraftStr);
    }

    /**
     * 获取最近录制的草稿
     */
    public RecordDraftInfo getLastDraftInfo() {
        String draftStr =
                mSharedPreferencesHelper.getSharedPreference(TCConstants.SP_KEY_RECORD_LAST_DRAFT
                        , "").toString();
        if (TextUtils.isEmpty(draftStr)) {
            return null;
        }
        Gson gson = new Gson();
        RecordDraftInfo recordDraftInfo = gson.fromJson(draftStr, new TypeToken<RecordDraftInfo>() {
        }.getType());
        return recordDraftInfo;
    }

    /**
     * 删除最近录制的草稿
     */
    public void deleteLastRecordDraft() {
        mSharedPreferencesHelper.put(TCConstants.SP_KEY_RECORD_LAST_DRAFT, "");
    }

    /**
     * 删除最近录制的草稿的最后一段
     */
    public void deleteLastPart() {
        RecordDraftInfo recordDraftInfo = getLastDraftInfo();
        if (recordDraftInfo == null) {
            TXCLog.e(TAG, "recordDraftInfo is null, ignore");
            return;
        }

        List<RecordDraftInfo.RecordPart> recordPartList = recordDraftInfo.getPartList();
        if (recordPartList == null || recordPartList.size() == 0) {
            TXCLog.e(TAG, "recordDraftInfo is empty, ignore");
            return;
        }
        recordPartList.remove(recordPartList.size() - 1);

        saveLastDraft(recordDraftInfo);
    }

}