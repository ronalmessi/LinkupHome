package com.ihomey.linkuphome.scan.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ihomey.linkuphome.R;
import com.ihomey.linkuphome.scan.ScanActivity;
import com.zbar.lib.ZbarManager;


/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p>
 * 时间: 2014年5月9日 下午12:24:13
 * <p>
 * 版本: V_1.0.0
 * <p>
 * 描述: 接受消息后解码
 */
final class DecodeHandler extends Handler {

    ScanActivity activity = null;

    DecodeHandler(ScanActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }

    private void decode(byte[] data, int width, int height) {
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width;// Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;
        ZbarManager manager = new ZbarManager();
        String result = manager.decode(rotatedData, width, height, true, activity.getX(), activity.getY(), activity.getCropWidth(),
                activity.getCropHeight());
        if (result != null) {
            if (null != activity.getHandler()) {
                Message msg = new Message();
                msg.obj = result;
                msg.what = R.id.decode_succeeded;
                activity.getHandler().sendMessage(msg);
            }
        } else {
            if (null != activity.getHandler()) {
                activity.getHandler().sendEmptyMessage(R.id.decode_failed);
            }
        }
    }

}
