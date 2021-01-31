package com.concurrent.common;

import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @program: JDK8-java
 * @description: 基本线程机制，演示java编程思想中21.2.4节案例
 *              Callable和Runnable类似，但是Runnable只是单纯的任务执行，并没有返回值，而Callable是有返回值的，它的返回值由Future来接受
 * @author: hs
 * @create: 2021-01-13 00:46
 **/
public class D_SimpleCallable {

    public static void main(String[] args) {
        ExecutorService exec= Executors.newCachedThreadPool();
        ArrayList<Future<String>> results=new ArrayList<Future<String>>();
        for(int i=0;i<10;i++){
            results.add(exec.submit(new TaskWithResult(i)));
            //isDone()方法用来检查future是否已经完成,当正常完成、发生异常、关闭程序时判定为完成
            System.out.println(results.get(0).isDone());
        }
        for(Future<String> fs:results){
            try{
                // 将等待返回Callable返回的结果，若Callable未执行完毕，则阻塞当前线程，直至Callable执行完成并返回结果。
                System.out.println(fs.get());
            }catch(InterruptedException e){
                System.out.println(e);
                return;
            }catch (ExecutionException e){
                System.out.println(e);
            }finally {
                exec.shutdown();
            }
        }
    }

    static class TaskWithResult  implements Callable<String> {
        private int id;
        public TaskWithResult(int id) {
            this.id = id;
        }

        @Override
        public String call() throws Exception {
            Thread.currentThread().yield();
            return "call 返回一个值 " + id;
        }
    }
}
