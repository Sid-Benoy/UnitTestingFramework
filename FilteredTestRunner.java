package assignment4.runners;

import assignment4.annotations.Test;
import assignment4.assertions.AssertionException;
import assignment4.results.TestClassResult;
import assignment4.results.TestMethodResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FilteredTestRunner extends TestRunner {
    private List<String> testMethods;
    private Object test;
    private Class testClass;

    public FilteredTestRunner(Class testClass, List<String> testMethods) {
        super(testClass);
        this.testClass = testClass;
        this.testMethods = testMethods;
        try {
            test = testClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TestClassResult run() {
        boolean pass = true;
        AssertionException a;
        Throwable error = null;
        List<String> filters = new ArrayList<>();
        List<Method> filterFinal = new ArrayList<>();
        TestClassResult filterOrder = new TestClassResult(testClass.getName());


        for (String method : testMethods) {
            try {
                Method methods = testClass.getMethod(method);
                filterFinal.add(methods);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        for (Method method : filterFinal) {
            try {
                test = testClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {

            }

            pass = true;
            if (method.isAnnotationPresent(Test.class)) {
                try {
                    method.invoke(test);
                } catch (Exception e) {
                    error = e.getCause();
                    pass = false;
                } finally {
                    if (pass) {
                        a = new AssertionException();
                        TestMethodResult result = new TestMethodResult(method.getName(), true, a);
                        filterOrder.addTestMethodResult(result);
                    } else {
                        a = (AssertionException) error;
                        TestMethodResult result = new TestMethodResult(method.getName(), false, a);
                        filterOrder.addTestMethodResult(result);
                    }
                }
            }


        }
        return filterOrder;
    }
}


