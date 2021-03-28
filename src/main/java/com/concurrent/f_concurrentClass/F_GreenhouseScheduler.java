package com.concurrent.f_concurrentClass;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: F_GreenhouseScheduler
 * @description: TODO
 * @date 2021年03月23日 23:08
 */
public class F_GreenhouseScheduler {
    private volatile boolean light = false;
    private volatile boolean water = false;
    private String thermostat = "Day";

    public synchronized void setThermostat(String thermostat) {
        this.thermostat = thermostat;
    }

    public synchronized String getThermostat() {
        return thermostat;
    }

    /**
     * ScheduledThreadPoolExecutor 继承自ThreadPoolExecutor，内部依旧使用ThreadPoolExecutor实现线程池的方式，10是核心线程数，最大线程是Integer.MAX_VALUE
     */
    ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(10);
    public void schedule(Runnable event, long delay){
        // 执行一次给定时间的调度行为
        scheduler.schedule(event,delay, TimeUnit.MILLISECONDS);
    }
    public void repeat(Runnable event, long initialDelay, long period){
        /**
         * 执行多次给定时间调度任务，每个任务固定时间执行一次
         * command：需要执行的任务
         * initialDelay：第一次开始执行时间
         * period：每个任务执行间隔时间，如第二次执行时间为：initialDelay+period。第三次：initialDelay+period*2
         * unit： 时间单位
         */
        scheduler.scheduleAtFixedRate(event,initialDelay,period,TimeUnit.MILLISECONDS);
    }
    class LightOn implements Runnable{
        @Override
        public void run() {
            System.out.println("开灯");
            light = true;
        }
    }
    class LightOff implements Runnable{
        @Override
        public void run() {
            System.out.println("关灯");
            light = false;
        }
    }
    class waterOn implements Runnable{
        @Override
        public void run() {
            System.out.println("开水闸");
            water = true;
        }
    }
    class waterOff implements Runnable{
        @Override
        public void run() {
            System.out.println("关水闸");
            water = false;
        }
    }
    class thermostatNight implements Runnable{
        @Override
        public void run() {
            System.out.println("恒温器，夜间模式");
            setThermostat("夜间模式");
        }
    }
    class thermostatDay implements Runnable{
        @Override
        public void run() {
            System.out.println("恒温器，日间模式");
            light = true;
            setThermostat("日间模式");
        }
    }
    class Bell implements Runnable{
        @Override
        public void run() {
            System.out.println("响铃.........");
        }
    }
    class Terminate implements Runnable{
        @Override
        public void run() {
            System.out.println("终止....");
            scheduler.shutdownNow();
            new Thread() {
                public void run(){
                    for (DataPoint d: data){
                        System.out.println(d);
                    }
                }
            }.start();
        }
    }
    class DataPoint {
        final Calendar time;
        final float temperature;
        final float humidity;

        public DataPoint(Calendar time, float temperature, float humidity) {
            this.time = time;
            this.temperature = temperature;
            this.humidity = humidity;
        }

        @Override
        public String toString() {
            return "DataPoint{" +
                    "time=" + time +
                    ", temperature=" + temperature +
                    ", humidity=" + humidity +
                    '}';
        }
    }
    private Calendar lastTime = Calendar.getInstance();
    {
        lastTime.set(Calendar.MINUTE,30);
        lastTime.set(Calendar.SECOND,00);
    }
    private float lastTemp =65.0f;
    private int tempDirection = +1;
    private float lastHumility= 50.0f;
    private int humilityDirection = +1;
    private Random random = new Random(47);
    // Collections.synchronizedList() 看了内部实现和注释说明，这个工具主要是确保线程安全，防止资源竞争。它会在每一个list方法包装
    // 一层synchronized(){}语句块来确保线程安全，mutex默认this，可以指定
    List<DataPoint> data= Collections.synchronizedList(new ArrayList<DataPoint>());
    class CollectData implements Runnable{
        @Override
        public void run() {
            System.out.println("收集数据");
            synchronized (F_GreenhouseScheduler.this){
                lastTime.set(Calendar.MINUTE,lastTime.get(Calendar.MINUTE)+30);
                if (random.nextInt(5)==4){
                    tempDirection = -tempDirection;
                }
                lastTemp += tempDirection*(1.0f+random.nextFloat());;
                if (random.nextInt(5)==4){
                    humilityDirection = -humilityDirection;
                }
                lastHumility+=humilityDirection*random.nextFloat();
                data.add(new DataPoint((Calendar)lastTime.clone(),lastTemp,lastHumility));
            }
        }
    }
}
