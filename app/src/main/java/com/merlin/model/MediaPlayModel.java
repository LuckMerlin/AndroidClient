package com.merlin.model;

import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.binding.StatusBar;
import com.merlin.client.R;

public class MediaPlayModel extends MediaDisplayModel implements Label, What {

//    private interface Api{
//        @POST(Address.PREFIX_MEDIA+"/favorite")
//        @FormUrlEncoded
//        Observable<Reply<NasMedia>> makeFavorite(@Field(LABEL_MD5) String md5, @Field(LABEL_DATA) boolean favorite);
//    }

    public MediaPlayModel() {
//        mPlayingAdapter=new MediaListAdapter();
//        mPlayingAdapter.setOnItemClickListener(this);
//        post(()->{setStatusBar(R.string.cancel, StatusBar.CENTER);},3000);

//        String title=null!=playing?playing.getTitle():null;
//        setStatusBar(null!=playing?title:R.string.mediaPlay, StatusBar.CENTER);
    }

//    @Override
//    protected void onRootAttached(View root) {
//        super.onRootAttached(root);
//        final SeekBar seekbar=null!=root?root.findViewById(R.id.mediaPlayBottomProgressSB):null;
//        if (null!=seekbar){
//            root.setOnTouchListener((v,event)-> {
//                    Rect seekRect = new Rect();
//                    seekbar.getHitRect(seekRect);
//                    if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
//                        float y = seekRect.top + seekRect.height() / 2;
//                        float x = event.getX() - seekRect.left;
//                        if (x < 0) {
//                            x = 0;
//                        } else if (x > seekRect.width()) {
//                            x = seekRect.width();
//                        }
//                        MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
//                                event.getAction(), x, y, event.getMetaState());
//                        return seekbar.onTouchEvent(me);
//                    }
//                    return false;
//            });
//        }
//    }

//    @Override
//    public void onPlayingChange(IPlayable playable) {
//        super.onPlayingChange(playable);
//        String title=null!=playable?playable.getTitle():null;
//        setStatusBar(null!=playable?title:R.string.mediaPlay, StatusBar.CENTER);
//    }

    //    private boolean makeFavorite(NasMedia meta, boolean favorite){
//        final String md5=null!=meta?meta.getMd5():null;
//        if (null==md5||md5.length()<=0){
//            return false;
//        }
//        return null!=call(Api.class,(OnApiFinish<Reply<File_>>)(what, note, data, arg)->{
////            AllMediasAdapter adapter=mAdapter;
//            toast(note);
//            if (what==WHAT_SUCCEED&&null!=data){
////                adapter.notifyFavoriteChange(md5, favorite);
//            }
//        }).makeFavorite(md5,favorite);
//    }


    //    private boolean setMediaPlayer(MediaPlayer player){
//        MediaPlayer curr=mMediaPlayer;
//        mMediaPlayer=player;
//        if (null!=player){
//            updateStatus();
//            updateProgress();
//            updateMode(null);
//            player.addListener(this);
//            return true;
//        }else if (null!=curr){
//            curr.removeListener(this);
//        }
//        return true;
//    }

//    @Override
//    public void onViewClick(View v, int id,Object obj) {
//        switch (id){
//            case R.id.media_display_play_rootRL:
////                ViewGroup vg=null!=v&&v instanceof ViewGroup?(ViewGroup)v:null;
////                int count=null!=vg?vg.getChildCount():0;
////                View next=null;
////                for (int i = count-1; i >=0; i--) {
////                    View child=vg.getChildAt(i);
////                    if (child.getVisibility() == View.VISIBLE) {
////                        i = -1;
////                        next = vg.getChildAt(i >= 0 ? i : count - 1);
////                        next.setVisibility(View.VISIBLE);
////                        child.setVisibility(View.GONE);
////                    }else if(null==next||child!=next){
////                        child.setVisibility(View.GONE);
////                    }
////                }
//                break;
//            case R.id.activityMediaPlay_preIV:
////                player.pre();
//                break;
//            case R.id.activityMediaPlay_playModeIV:
////                updateMode(player.playMode(Mode.CHANGE_MODE));
//                break;
//            case R.id.activityMediaPlay_playPauseIV:
////                player.togglePlayPause(null);
//                break;
//            case R.id.activityMediaPlay_nextIV:
////                player.next();
//                break;
//            case R.drawable.selector_menu:
//                toast("菜单");
//                break;
//            case R.drawable.ic_back:
//                finishActivity();
//                break;
//            case R.string.cancel:
//                toast("取消");
//                break;
//            default:
////                toast("哒哒哒哒哒哒多 "+id );
////                MediaPlayDisplayLayout layout=findViewById(R.id.activityMediaDisplay_layoutMPDL, MediaPlayDisplayLayout.class);
////                if (null!=layout){
////                    layout.onViewClick(v,id,obj);
////                }
//                break;
//
////            case R.id.title_backIV:
////                finishAllActivity(MediaPlayActivity.class);
////                break;
//        }
//
//    }

//
//    private void updateMode(Mode mode){
//      MediaPlayer player=null==mode?mMediaPlayer:null;
//      mode=null!=player?player.playMode(null):mode;
//      mPlayMode.set(mode);
//    }
//
//    private void  updateStatus(){
//        MediaPlayer player=mMediaPlayer;
//        mPlayState.set(null!=player?player.getPlayState():STATUS_UNKNOWN);
////        mPlayState.set(null!=player?player.getPlayState():STATUS_UNKNOW);
//    }

    public ActivityMediaPlayModel getModel(){
        return ActivityMediaPlayModel.getModelFromChild(this);
    }

}
