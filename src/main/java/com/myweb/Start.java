package com.myweb;

import com.myweb.icris.IcrisApi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {

    private static final int MAX_THREADS = 50;
    public static final int SLEEP = 5000;
    public static ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);

    public static void main(String[] args){
        IcrisApi api = new IcrisApi();
        try {
            api.agree();
            api.chinese();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                final int time = api.getMax();
                for(int i = time; i < 3000000 ; i ++) {
                    final int count = i;
                    Thread.sleep(SLEEP);
                    executorService.execute(new Thread(() -> {
                        try {
                            boolean flag = api.pachong(count);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                    if (i % 3000 == 0) api.agree();
                }
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
        }
    }
}
