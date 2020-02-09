package com.merlin.model;
import com.merlin.activity.OnBackPressed;
import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.player.Status;

public class MediaPlayModel extends Model implements Label, What,Status {

    public MediaPlayModel(){
//        mPlayingAdapter=new MediaListAdapter();
//        mPlayingAdapter.setOnItemClickListener(this);
//        post(()->{setStatusBar(R.string.cancel, StatusBar.CENTER);},3000);
    }

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
