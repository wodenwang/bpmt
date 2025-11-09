package com.riversoft.core.script;

import com.riversoft.core.BeanFactory;
import com.riversoft.platform.script.BasicScriptExecutionContext;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @borball on 10/07/2014.
 */
public class ScriptEngineConcurrentTest {

    class ScriptHelperTester implements Callable<Boolean> {

        Map<String, Object> context = new HashMap<>();
        private String script;
        private ScriptType scriptType;

        public ScriptHelperTester(int id, ScriptType type){
            this.scriptType = type;

            switch (type) {
                case GROOVY:
                    script = "return attr " + " == 'para" + id + "'";
                    break;
                case JSR223:
                    script = "${attr " + " == 'para" + id + "'}";
                    break;
                default:
                    break;
            }
            context.put("attr", "para" + id);
        }

        @Override
        public Boolean call() throws Exception {
            ExpressionAndScriptExecutors executors = (ExpressionAndScriptExecutors) BeanFactory.getInstance().getBean(
                    "expressionAndScriptExecutors");
            Object result;
            switch (this.scriptType) {
                case GROOVY:
                     result = executors.evaluateScript(scriptType, script, new BasicScriptExecutionContext(context));
                    break;
                case JSR223:
                    result = executors.evaluateEL(script, new BasicScriptExecutionContext(context));
                    break;
                default:
                    return false;
            }

            return result == null ? false: Boolean.valueOf(result.toString());
        }

    }

    @BeforeClass
    public static void beforeClass() {
        BeanFactory.init("classpath:applicationContext-scripts.xml");
    }

    @Test
    public void testGroovyConcurrent() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(20);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            Future<Boolean> future = executor.submit(new ScriptHelperTester(i, ScriptType.GROOVY));

            futures.add(future);
        }

        for (Future<Boolean> future : futures) {
            Assert.assertTrue(future.get());
        }
    }


    @Test
    public void testElConcurrent() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Future<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Future<Boolean> future = executor.submit(new ScriptHelperTester(i, ScriptType.JSR223));

            futures.add(future);
        }

        for (Future<Boolean> future : futures) {
            Assert.assertTrue(future.get());
        }
    }
}
