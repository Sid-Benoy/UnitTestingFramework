package assignment4.runners;

import assignment4.assertions.AssertionException;
import assignment4.results.TestClassResult;

public class AlphabeticalTestRunner extends TestRunner {
    private Class testclass;
    public AlphabeticalTestRunner(Class testClass) {
        super(testClass);
    }
}
