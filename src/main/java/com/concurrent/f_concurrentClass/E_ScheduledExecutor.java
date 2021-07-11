package com.concurrent.f_concurrentClass;



import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author heshuai
 * @title: E_ScheduledExecutor
 * @description: 线程机制，演示java编程思想中21.7.5节案例
 * @date 2021年03月23日 22:20
 */
public class E_ScheduledExecutor {

    public static void main(String[] args) {
        F_GreenhouseScheduler gh = new F_GreenhouseScheduler();
        gh.schedule(gh.new Terminate(),5000);
        gh.repeat(gh.new Bell(),0,1000);
        gh.repeat(gh.new thermostatNight(),0,2000);
        gh.repeat(gh.new LightOn(),0,   200);
        gh.repeat(gh.new LightOff(),0,400);
        gh.repeat(gh.new waterOn(),0,600);
        gh.repeat(gh.new waterOff(),0,800);
        gh.repeat(gh.new thermostatDay(),0,1400);
        gh.repeat(gh.new CollectData(),0,1000);
        gh.repeat(gh.new Bell(),500,500);

    }
}
