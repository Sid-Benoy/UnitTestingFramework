package assignment4.driver;

import assignment4.annotations.*;
import assignment4.results.TestClassResult;

import assignment4.runners.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestDriver {


    public static void runTests(String[] testclasses) {
		List<Class> classList = new ArrayList<>();
		String temp = null;
		String classNameTemp = null;
		int index;
		int flag = 0;
		List<TestClassResult> resultList = new ArrayList<>();

		for(int i = 0; i < testclasses.length; i++){
			if(testclasses[i].indexOf('#') > 0){
				temp = testclasses[i].substring(testclasses[i].indexOf('#')+1);
				testclasses[i] = testclasses[i].substring(0, testclasses[i].indexOf('#'));
				classNameTemp = testclasses[i];
				try {
					classList.add(Class.forName(testclasses[i]));
				}catch(ClassNotFoundException e){

				}

			}
			else{
				try {
					classList.add(Class.forName(testclasses[i]));
				}catch(ClassNotFoundException e){

				}
			}
		}

        for(int j = 0; j < classList.size();j++) {
        	Class sample = classList.get(j);
			int index1 = sample.getName().indexOf('#');
			TestClassResult trial = null;

			if(sample.isAnnotationPresent(Alphabetical.class)) {
				AlphabeticalTestRunner alpha = null;
				try {
					alpha = new AlphabeticalTestRunner(sample);
				} finally {
					trial = alpha.runAlpha();
				}
				resultList.add(trial);
			}
			else if(sample.isAnnotationPresent(Ordered.class)){
				OrderedTestRunner order = null;
				try {
					order = new OrderedTestRunner(sample);
				} finally{
					trial = order.runOrdered();
				}
				resultList.add(trial);
			}
			else if(sample.getName().equals(classNameTemp)){
				List<String> testmethods = new ArrayList<>(Arrays.asList(temp.split(",")));
				FilteredTestRunner filter = null;
				filter = new FilteredTestRunner(sample, testmethods);
				trial = filter.run();
				resultList.add(trial);
			}
			else{
				TestRunner test = null;
				try {
					test = new TestRunner(sample);

				}finally {
					trial = test.run();

				}
				resultList.add(trial);
			}

        }
        
        int numOfTests = 0;
        
        for(int i = 0; i < resultList.size(); i++) {
        	for(int j = 0; j < resultList.get(i).getTestMethodResults().size(); j++) {
	        	System.out.print(classList.get(i).getName() + "." + resultList.get(i).getTestMethodResults().get(j).getName());
	        	numOfTests++;
				if(resultList.get(i).getTestMethodResults().get(j).isPass()) {
					System.out.println(" : PASS");
				}else {
					System.out.println(" : FAIL");
				}
        	}
        }
		
		System.out.println("==========");
		System.out.println("FAILURES:");
		int numOfFails = 0;
		for(int i = 0; i < resultList.size(); i++) {
			for(int j = 0; j < resultList.get(i).getTestMethodResults().size(); j++) {
				if(!resultList.get(i).getTestMethodResults().get(j).isPass()) {
					numOfFails++;
					System.out.println(classList.get(i).getName() + "." + resultList.get(i).getTestMethodResults().get(j).getName() + ":");
					resultList.get(i).getTestMethodResults().get(j).getException().printStackTrace();
				}
			}
		}
		System.out.println("==========");
		
		System.out.println("Tests run: " + numOfTests + ", Failures: " + numOfFails);

    }

    public static void main(String[] args){
        // Use this for your testing.  We will not be calling this method.
    	
    	String[] tests = new String[1];
    	tests[0] = "test.TestF#testA,testC,atest,b0,foo,foo1,bruh";



		
        runTests(tests);
    }
}
