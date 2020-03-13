//package com.merlin.model;
//
//import android.content.Context;
//import android.view.View;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//
//import com.merlin.activity.MediaSheetDetailActivity;
//import com.merlin.adapter.BaseAdapter;
//import com.merlin.adapter.GridSpacingItemDecoration;
//import com.merlin.adapter.MediaSheetAdapter;
//import com.merlin.api.Address;
//import com.merlin.api.ApiList;
//import com.merlin.api.Reply;
//import com.merlin.bean.MediaSheet;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.POST;
//
//public class ActivityMediaSheetModel extends DataListModel<MediaSheet> implements BaseAdapter.OnItemClickListener<MediaSheet> {
//
//    private interface SheetApi{
//        @POST(Address.PREFIX_MEDIA+"sheets")
//        @FormUrlEncoded
//        Observable<Reply<ApiList<MediaSheet>>> querySheets(@Field("name") String name, @Field("page") int page, @Field("limit") int limit);
//    }
//
//    public ActivityMediaSheetModel(Context context){
//        super(context,new MediaSheetAdapter(),new GridLayoutManager(context,3,
//                GridLayoutManager.VERTICAL,false),new GridSpacingItemDecoration(3,10,true));
////                GridLayoutManager.VERTICAL,false));
//        List<MediaSheet> list=new ArrayList<>();
//        MediaSheet sheet=null;
//        for (int i = 0; i < 20; i++) {
//            sheet=new MediaSheet();
//            sheet.setImageUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4038297574,3426702532&fm=26&gp=0.jpg");
//            sheet.setTitle("流行 "+i);
////            sheet.setSheetId("Id "+i);
//            sheet.setSize(400);
//            list.add(sheet);
//        }
//        getAdapter().setData(list);
//    }
//
////    private void test(){
////        setRefreshing(true);
////        call(SheetApi.class,(Retrofit.OnApiFinish<Reply<ApiList<MediaSheet>>>)(what, note, response)->{
////            setRefreshing(false);
////            List<MediaSheet> list=null!=response?response.getData():null;
////            getAdapter().setData(list);
////        }).querySheets("牛大幅 ",0,10);
////    }
//
//    @Override
//    public void onItemClick(View view, int sourceId, int position, MediaSheet data) {
//    }
//
//}
