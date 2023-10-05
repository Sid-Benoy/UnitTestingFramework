package assignment4.gui;

import assignment4.annotations.Alphabetical;
import assignment4.annotations.Order;
import assignment4.annotations.Ordered;
import assignment4.annotations.Test;
import assignment4.driver.TestDriver;
import assignment4.listeners.GUITestListener;
import assignment4.listeners.TestListener;
import assignment4.results.TestClassResult;
import assignment4.runners.TestRunner;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import javax.swing.*;

import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TestGUI extends Application {

    @FXML
    private Button button;
    @FXML
    private Button clear;
    @FXML
    private Button runAll;
    @FXML
    private TextField field;
    @FXML
    private ChoiceBox choice;
    @FXML
    private TextArea area;
    @FXML
    private TextArea area2;
    @FXML
    private ChoiceBox choice2;
    @FXML
    private Arc smile;

    List<Method> methods = new ArrayList<>();

    @Override
    public void start(Stage applicationStage) throws Exception {
//
        Parent root = FXMLLoader.load(getClass().getResource("test.fxml"));
        applicationStage.setScene(new Scene(root, 892, 639));
        applicationStage.setTitle("GUI");
        applicationStage.show();
        URL directory = ClassLoader.getSystemClassLoader().getResource("/src/test");
        System.out.println(directory);
        // TODO: Implement this method
    }

    @FXML
    public void handleTextFieldEvent(ActionEvent event) throws URISyntaxException {
        String classpath = field.getText();
        getClassPath(classpath);
    }
    public void getClassPath(String classpath) throws URISyntaxException {
        URL directoryurl = ClassLoader.getSystemClassLoader().getResource(classpath);
        File directory = new File(directoryurl.toURI());
        File[] files = directory.listFiles();
        
        for(int i = 0; i < files.length; i++) {
        	int index = files[i].toString().indexOf(classpath);
        	index = index + classpath.length() + 1;
        	int endIndex = files[i].toString().indexOf(".class");
            choice.getItems().add(files[i].toString().substring(index, endIndex));
        }
        
        System.out.println(Arrays.toString(files));
    }
    @FXML
    public void handleClassSelection(ActionEvent event) throws ClassNotFoundException {
    	choice2.getItems().clear();
        GUITestListener yea = new GUITestListener(this);
        String testSelection = "test." + choice.getValue().toString();
        System.out.println(testSelection);
        Class testClass = Class.forName(testSelection);
        TestRunner yo = new TestRunner(testClass);
        for(Method method : testClass.getDeclaredMethods()){
        	if(method.isAnnotationPresent(Test.class)) {
	            choice2.getItems().add(method);        
	        }
        }
        yo.addListener(yea);
    }

    @FXML
    public void addTestMethod() throws NoSuchMethodException, ClassNotFoundException {
        int index = choice2.getValue().toString().indexOf('(');
        String methodname = choice2.getValue().toString().substring(23,index);       
        Method method = (Class.forName("test." + choice.getValue().toString())).getMethod(methodname);
        methods.add(method);
    }
    @FXML
    public void addAllMethods() throws NoSuchMethodException, ClassNotFoundException {
    	methods.clear();
    	ObservableList<Method> choice2Items = choice2.getItems();
    	methods.addAll(choice2Items);
    	System.out.println(methods);
    }
    @FXML
    public void handleTestMethod() throws ClassNotFoundException {
        TestClassResult trial = null;
        GUITestListener listen = new GUITestListener(this);
        String classSelection = "test." + choice.getValue().toString();
        Class classyea = Class.forName(classSelection);
        TestRunner classRunner = new TestRunner(classyea);
        classRunner.TestRunner1(methods);
        classRunner.addListener(listen);
        if(classyea.isAnnotationPresent(Alphabetical.class)){
            trial = classRunner.runAlpha();
        }
        else if(classyea.isAnnotationPresent(Ordered.class)){
            trial = classRunner.runOrdered();
        }
        else{
            trial = classRunner.run();
        }

        area.appendText("==========\n");
        area.appendText("FAILURES:\n");
        int numOfFails = 0;
        String stackTrace = "";
        for(int j = 0; j < trial.getTestMethodResults().size(); j++) {
            if(!trial.getTestMethodResults().get(j).isPass()) {
                numOfFails++;
                area.appendText(Class.forName(classSelection).getName() + "." + trial.getTestMethodResults().get(j).getName() + ":\n");
                System.out.println(this.getClass().getName());
                area.appendText(trial.getTestMethodResults().get(j).getException().getClass().getName());
                trial.getTestMethodResults().get(j).getException().printStackTrace();
                for (StackTraceElement ste : trial.getTestMethodResults().get(j).getException().getStackTrace()) {
                    stackTrace += "\n    at " + ste.toString();

                    if (!ste.getClassName().equals("assignment4.assertions.Assert")) {
                        break;
                    }
                }
                area.appendText(stackTrace + "\n");
            }
        }
       
        area.appendText("==========\n");
        
        if(numOfFails > 0) {
        	smile.setStartAngle(0);
        	smile.setCenterY(50);
        }else {
        	smile.setStartAngle(180);
        	smile.setCenterY(0);
        }
        
        methods.clear();

    }
    
    @FXML
    public void chooseNewTests() {
        area2.clear();
        area.clear();
        choice2.getItems().clear();
    }
    
    @FXML
    public void checkRunning(String str) {
    	area2.appendText(str + ": Started\n");
 //   	testLabel.setText(testLabel.getText() + str + ": Starting\n");
    }
    
    @FXML
    public void checkPassed(String str) {
    	area2.appendText(str + " Finished: Passed\n");
    }
    
    @FXML
    public void checkFailed( String str) {
    	area2.appendText(str + " Finished: Failed\n");
    }

    public static void main(String[] args) {
        launch(args); // Launch application
    }
}
