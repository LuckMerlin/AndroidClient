package com.merlin.dialog;
import android.content.Context;
import android.view.View;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.client.R;
import com.merlin.client.databinding.LayoutFileConveyingBinding;
import com.merlin.conveyor.Convey;
import com.merlin.conveyor.FileConvey;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.view.OnTapClick;

public final class FileConveyDialog {
    private final Dialog mDialog;
    private final LayoutFileConveyingBinding mBinding;
    private Convey.Confirm mConfirm;

    public FileConveyDialog(LayoutFileConveyingBinding binding){
        mBinding=binding;
        View root=null!=binding?binding.getRoot():null;
        Context context=null!=root?root.getContext():null;
        mDialog=null!=context?new Dialog(context,binding,null).setCanceledOnTouchOutside(false):null;
    }

    private final OnTapClick mOnTapClick=(View view, int clickCount, int resId, Object data)-> {
            switch (clickCount){
                case 1:
                    Convey.Confirm confirm=mConfirm;
                    if (null!=confirm){
                        switch (resId){
                            case R.string.sure:
                            case R.string.cover:
                                confirm.onConfirm(What.WHAT_SUCCEED,"After dialog tap.");
                                break;
                            case R.string.skip:
                                confirm.onConfirm(What.WHAT_SKIP,"After dialog tap.");
                                break;
                            case R.string.cancel:
                                confirm.onConfirm(What.WHAT_CANCEL,"After dialog tap.");
                                break;
                        }
                    }
                    break;
            }
        return true;
    };

    public FileConveyDialog title(Object title){
        Dialog dialog=mDialog;
        if (null!=dialog){
            dialog.title(title);
        }
        return this;
    }

    public boolean convey(Retrofit retrofit,Convey convey,String debug){
        return null!=retrofit&&null!=convey&&convey.convey(retrofit,new OnConveyStatusChange() {
            @Override
            public void onConveyStatusChanged(int status, Convey parent, Convey innerConvey, Reply reply) {
                LayoutFileConveyingBinding binding=mBinding;
                Dialog dialog=mDialog;
                if (null!=binding&&null!=dialog){
                    binding.setStatus(Integer.toString(status));
                    if (null!=innerConvey&&innerConvey instanceof FileConvey){
                        binding.setFrom(((FileConvey)innerConvey).getFrom());
                        binding.setTo(((FileConvey)innerConvey).getTo());
                    }
                    Debug.D(getClass()," "+status+" "+(null!=innerConvey?innerConvey.getStatus():null));
                    switch (status){
                        case CONFIRM:
                            Object object=null!=reply?reply.getData():null;
                            mConfirm=null!=object&&object instanceof Convey.Confirm?((Convey.Confirm)object):null;
                            dialog.left(R.string.skip).center(R.string.cover).right(R.string.cancel);
                            break;
                        case PROGRESS:
                            Object proObject=null!=reply?reply.getData():null;
                            FileConvey.Progress progress=null!=proObject&&proObject instanceof FileConvey.Progress?(FileConvey.Progress)proObject:null;
                            if (null!=progress){
                                binding.setProgress((int)progress.getProgress());
                            }
                            break;
                        case FINISHED:
                            if (null!=innerConvey&&innerConvey==convey){
                                dialog.dismiss();
                            }
                            break;
                    }
                }
            }
        },debug);
    }

    public boolean show(){
        Dialog dialog=mDialog;
        return null!=dialog&&dialog.show(mOnTapClick);
    }
}
