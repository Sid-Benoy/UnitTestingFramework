package assignment4.runners;


import java.lang.reflect.Method;
import java.util.*;


import assignment4.annotations.Order;
import assignment4.annotations.Test;
import assignment4.assertions.AssertionException;
import assignment4.listeners.TestListener;
import assignment4.results.TestClassResult;
import assignment4.results.TestMethodResult;


public class TestRunner {
	
	private Object test;
	private Class testClass;


//	private TestMethodResult res;

	public TestRunner(Class testClass) {
        // TODO: complete this constructor
    	this.testClass = testClass;
		try {
			test = testClass.newInstance();
		}catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

    public TestClassResult run(){
		boolean pass = true;
		TestClassResult result = new TestClassResult(testClass.getName());
		AssertionException a;
		Throwable error = null;

    	for(Method method : testClass.getDeclaredMethods()) {
			try {
				test = testClass.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			pass = true;
    		if(method.isAnnotationPresent(Test.class)) {
    				
					try{
						method.invoke(test);
					}catch(Exception e) {
						
						pass = false;
						error= e.getCause();
					}
					
					finally {
						if(pass) {
							a = new AssertionException();	
							TestMethodResult methodResult = new TestMethodResult(method.getName(),true, a);
							result.addTestMethodResult(methodResult);
						}else{
							a = (AssertionException) error;
							TestMethodResult methodResult = new TestMethodResult(method.getName(),false, a);
							result.addTestMethodResult(methodResult);
						}
					}
					
					
    		}

    	}

        return result;
    }

	public TestClassResult runAlpha(){
		boolean pass = true;
		List<String> stringListFinal = new ArrayList<>();
		AssertionException a;
		Throwable error = null;
		TestClassResult resultAlpha = new TestClassResult(testClass.getName());
		List<String> stringList = new ArrayList<>();
		List<Method> methodList = new ArrayList<>();
		for(Method method : testClass.getDeclaredMethods()){			//sorts methods alphabetically
			stringList.add(method.toString());
			Collections.sort(stringList);
		}
		for(int i = 0; i < stringList.size(); i++){
			int index = stringList.get(i).indexOf('(');
			stringListFinal.add(stringList.get(i).substring(23, index));		//gets substrings from string to get method name
		}
		for(String method : stringListFinal) {
			Method methods = null;
			try {
				methods = testClass.getMethod(method);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			} finally {
				methodList.add(methods);
			}
			;

		}

		for(Method method : methodList){
			try {
				test = testClass.newInstance();
			}catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}

			pass = true;
			if(method.isAnnotationPresent(Test.class)){
				try{
					method.invoke(test);
				} catch (Exception e) {
					error = e.getCause();
					pass = false;
				}
				finally{
					if(pass){
						a = new AssertionException();
						TestMethodResult result = new TestMethodResult(method.getName(), true, a);
						resultAlpha.addTestMethodResult(result);
					}
					else{
						a = (AssertionException) error;
						TestMethodResult result = new TestMethodResult(method.getName(), false, a);
						resultAlpha.addTestMethodResult(result);
					}
				}
			}


		}
		return resultAlpha;
	}

	public TestClassResult runOrdered() {
		boolean pass = true;
		AssertionException a;
		Throwable error = null;
		TestClassResult resultOrder = new TestClassResult(testClass.getName());
		List<Integer> rankings = new ArrayList<>();
		List<Method> orderList = new ArrayList<>();
		List<Method> finalOrderList = new ArrayList<>();
		List<Integer> check = new ArrayList<>();
		int flag = 0;
		for(Method method : testClass.getDeclaredMethods()){			//adds ranking numbers and method names
			if(method.isAnnotationPresent(Order.class) && method.isAnnotationPresent(Test.class)){
				rankings.add(method.getAnnotation(Order.class).value());
		//		orderList.put(method.getAnnotation(Order.class).value(), method);
				orderList.add(method);
				
			}
			
		}
		
		Collections.sort(rankings);
		for (int i = 0; i < rankings.size(); i++) {
			flag = 0;			
				for(Method method : testClass.getDeclaredMethods()) {
				if(method.getAnnotation(Order.class).value() == rankings.get(i) && flag == 0 && !(check.contains(i))){
					finalOrderList.add(method);
					flag = 1;
					check.add(i);
				}

			}
		}

		

        finalOrderList = orderList;
        for(Method method : testClass.getDeclaredMethods()){			//adds ranking numbers and method names
			if(!method.isAnnotationPresent(Order.class) && method.isAnnotationPresent(Test.class)){

				finalOrderList.add(method);
				
			}
			
		}
		for(Method method : finalOrderList){
			try {
				test = testClass.newInstance();
			}
			catch(IllegalAccessException | InstantiationException e){

			}
			pass = true;
			if(method.isAnnotationPresent(Test.class)){
				try{
					method.invoke(test);
				} catch (Exception e) {
					error = e.getCause();
					pass = false;
				}
				finally{
					if(pass){
						a = new AssertionException();
						TestMethodResult result = new TestMethodResult(method.getName(), true, a);
						resultOrder.addTestMethodResult(result);
					}
					else{
						a = (AssertionException) error;
						TestMethodResult result = new TestMethodResult(method.getName(), false, a);
						resultOrder.addTestMethodResult(result);
					}
				}
			}
		}
		return resultOrder;
	}

    public void addListener(TestListener listener) {
        // Do NOT implement this method for Assignment 4
    }
}
