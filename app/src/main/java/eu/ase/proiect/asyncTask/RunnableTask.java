package eu.ase.proiect.asyncTask;

import android.os.Handler;
import android.util.Log;

import java.util.concurrent.Callable;

public class RunnableTask<R> implements Runnable {

    private final Callable<R> asyncOperation;
    private final Handler handler;
    private final Callback<R> mainThreadOperation;

    public RunnableTask(Callable<R> asyncOperation, Handler handler, Callback<R> mainThreadOperation) {
        this.asyncOperation = asyncOperation;
        this.handler = handler;
        this.mainThreadOperation = mainThreadOperation;
    }

    @Override
    public void run() {
        try {
            R result = asyncOperation.call();
            handler.post(new HandlerMessage<>(result, mainThreadOperation));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Runnable Task", "failed call runnableTask " + e.getMessage());
        }


    }
}
