package com.merlin.file.transport;

import com.merlin.task.Networker;
import com.merlin.task.Task;
import com.merlin.task.file.FileProgress;

import java.util.Random;

public class TestTask extends Task {

    public TestTask(String name) {
        super(name);
    }

    @Override

    protected void onExecute(Networker networker) {
        FileProgress progress=new FileProgress(0,1000000);
        while (true){
            try {
                if (progress.getDone()>=progress.getTotal()){
                    progress.setDone(0);
                }
                Thread.sleep(100);
                progress.setDone(Math.min(progress.getTotal(),progress.getDone()+(int)(10000* new Random().nextFloat())));
                progress.setPerBytes((int)(10000* new Random().nextFloat()));
                notifyStatus(DOING,null,progress);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
