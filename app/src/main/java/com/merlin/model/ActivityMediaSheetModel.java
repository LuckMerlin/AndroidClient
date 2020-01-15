package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.GridLayoutManager;

import com.merlin.activity.MediaSheetActivity;
import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.GridSpacingItemDecoration;
import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.client.Client;
import com.merlin.client.OnObjectRequestFinish;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.media.Sheet;
import com.merlin.server.Frame;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityMediaSheetModel extends DataListModel<Sheet> implements BaseAdapter.OnItemClickListener<Sheet> {
    private final String mCloudAccount;

    public ActivityMediaSheetModel(Context context){
        super(context,new MediaSheetAdapter(),new GridLayoutManager(context,3,
                GridLayoutManager.VERTICAL,false),new GridSpacingItemDecoration(3,10,true));
        mCloudAccount="linqiang";
    }

    @Override
    protected void onViewAttached(View root) {
        super.onViewAttached(root);
        JSONObject object=new JSONObject();
        putIfNotNull(object,TAG_PAGE,0);
        putIfNotNull(object,TAG_LIMIT,10);
        request(mCloudAccount,"/sheet",object,new OnObjectRequestFinish<List<Sheet>>(){
            @Override
            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Sheet> data) {
                Debug.D(getClass(),"@@@@@@@@@@@ "+data);
                getAdapter().setData(data);
            }
        });
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Sheet data) {
        if (null!=data){
            data.setAccount(mCloudAccount);
            startActivity(MediaSheetDetailActivity.class,data);
            finishAllActivity(MediaSheetActivity.class);
        }
    }

}
