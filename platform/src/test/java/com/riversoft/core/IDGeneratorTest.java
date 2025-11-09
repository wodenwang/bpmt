/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2014 by Riversoft System, all rights reserved.
 */
package com.riversoft.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author woden
 * 
 */
public class IDGeneratorTest {

    static class OneThread implements Runnable {

        private ConcurrentHashMap<String, Integer> map;
        private LinkedBlockingQueue<String> poisons;

        private int loop;

        OneThread(ConcurrentHashMap<String, Integer> map, LinkedBlockingQueue<String> poisons, int loop) {
            this.map = map;
            this.poisons = poisons;
            this.loop = loop;
        }

        @Override
        public void run() {
            for (int i = 0; i < loop; i++) {
                String val = IDGenerator.next();
                System.out.println("放入:" + val);
                map.putIfAbsent(val, 1);
            }

            poisons.add("ok");// 放入毒丸
        }
    }

    /**
     * 
     */
    @Test
    public void testUUID() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
        LinkedBlockingQueue<String> poisons = new LinkedBlockingQueue<String>();

        int loop = 1000;// 每条线程循环次数
        int theadSize = 20;// 线程数

        ExecutorService exec = Executors.newFixedThreadPool(100);
        for (int i = 0; i < theadSize; i++) {
            exec.execute(new OneThread(map, poisons, loop));
        }

        while (poisons.size() < theadSize) {// 每条线程一颗毒丸
            try {
                Thread.sleep(5000);
                System.out.println("继续等待5秒.");
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        Assert.assertEquals(map.size(), theadSize * loop);
    }

}
