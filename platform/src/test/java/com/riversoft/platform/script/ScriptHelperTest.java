/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.riversoft.util.jackson.JsonMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.riversoft.core.BeanFactory;
 
/**
 * @author Borball
 *
 */
public class ScriptHelperTest {

    @BeforeClass
    public static void beforeClass(){
        BeanFactory.init("classpath:applicationContext-scripts-test.xml");
    }
    
    class ScriptHelperTester implements Callable<Boolean>{

        Map<String, Object> context = new HashMap<>();
        private String script;
        private ScriptTypes scriptType;
        
        public ScriptHelperTester(int id, ScriptTypes type){
            this.scriptType = type;
            
            switch (type) {
            case GROOVY:
                script = "return attr " + " == 'para" + id + "'";
                break;
            case EL:
                script = "${attr " + " == 'para" + id + "'}";
                break;
            default:
                break;
            }
            context.put("attr", "para" + id);
        }
        
        @Override
        public Boolean call() throws Exception {
            Object result = ScriptHelper.evel(scriptType, script, context);
            return Boolean.valueOf(result.toString());
        }
        
    }
    
    @Test
    public void testGroovyConcurrent() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        List<Future<Boolean>> futures = new ArrayList<>();
        
        for (int i = 0; i < 500; i++) {
            Future<Boolean> future = executor.submit(new ScriptHelperTester(i, ScriptTypes.GROOVY));
            
            futures.add(future);
        }
        
        for (Future<Boolean> future : futures) {
            Assert.assertTrue(future.get());
        }
    }
    
    @Test
    public void testGroovyOne() throws Exception {
        ScriptHelperTester ScriptHelperTester = new ScriptHelperTester(10, ScriptTypes.GROOVY);
        Assert.assertTrue(ScriptHelperTester.call());
    }
    
    @Test
    public void testElConcurrent() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        List<Future<Boolean>> futures = new ArrayList<>();
        
        for (int i = 0; i < 500; i++) {
            Future<Boolean> future = executor.submit(new ScriptHelperTester(i, ScriptTypes.EL));
            
            futures.add(future);
        }
        
        for (Future<Boolean> future : futures) {
            Assert.assertTrue(future.get());
        }
    }
    
    @Test
    public void testElOne() throws Exception {
        ScriptHelperTester ScriptHelperTester = new ScriptHelperTester(10, ScriptTypes.EL);
        Assert.assertTrue(ScriptHelperTester.call());
    }

    @Test
    public void testGroovy(){
        Object o = ScriptHelper.evel(ScriptTypes.GROOVY, "return ['kf': true]", null);
        System.out.println(o);
        System.out.println(JsonMapper.defaultMapper().toJson(o));
    }

}
