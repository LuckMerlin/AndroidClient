package com.merlin.dialog;

import android.content.Context;

import com.merlin.client.R;
import com.merlin.task.Transport;

import java.util.List;

public class DownloadFileExistDialog extends Dialog {
    private Callback mCallback;

    public DownloadFileExistDialog(Context context){
        super(context);
        setContentView(R.layout.dlg_download_file_exist);
    }


    public interface Callback{
        int WHAT_REPLACE_ALL = 1;
        int WHAT_KEEP_ALL = 2;
        void onConfirm(int what,List<Transport> result);
    }

    public DownloadFileExistDialog setCallback(Callback callback){
        mCallback=callback;
        return this;
    }

    public boolean show(String title, List<Transport> list){
//        (findViewById(R.id.dlg_message_layout_msgTV,TextView.class)).setText(msg);
        super.show();
        return false;
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mCallback=null;
    }
}
