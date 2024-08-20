/* **********************************************
* File Name: Topic 5 Individual Project #3 - Random Walk
* Programmer: Celia Ho
* Last Modified: Sat. May 11, 2024
* Description: In this project you are going to model interaction behavior with JavaFX. 
* 
* First, you are going to model the simple "Random Walk" - in a random walk, you start with a blue point, and the point moves from the current position randomly in any of the 8 cardinal (N, NE, E, SE, S, SW, W, NW) directions a random distance (say between 1 and 9 "steps"). Your random walk should be at least 2000 steps. (These points should be circles with small radius)
*
Display your "random walk" as a series of circles on the stage, connected by a blue line.
*
Next display 10 random GREEN circles on your stage - these should have radii at least 2x the radii of your random walk. Rerun your random walk - when your blue random walk line encounters a GREEN circle, the GREEN circle changes color to RED and also starts to random walk in the same manner as the blue walk. If *any* random walk (a RED one or the original blue one) encounters a GREEN circle, the GREEN circle changes color to RED and also starts to random walk, again each random walk should be at least 2000 steps.
*
* Learning outcomes assessed:
*    JavaFX
*    JavaFX animations
*    JavaFX event driven programming
* ************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Random_Walk_2 extends Application {
    // *****GLOBAL FIELDS FOR STAGE*****
    BorderPane borderPane;
    VBox 
        controlPane;
    Pane animationPane;
    Button 
        playButton,
        pauseButton,
        stopAndClearButton;
    // Label labelAnimationSpeed;
    ChoiceBox<String> speedChoiceBox;
    Circle
        circle,
        blueCircle,
        greenCircle;
    Integer
        blueCircleRadius = 5,
        greenCircleRadius = 10,
        numberInitialGreenCircles = 10,
        intersectedCircleRadius = 8,
        displayInset = 10;
    int delay = 250;
    Double
        sceneWidth = 900.0,  // For scene size
        sceneHeight = 700.0,  // For scene size
        //displayWidth = sceneWidth - (displayInset * 2),
        //displayHeight = sceneHeight - (displayInset * 2),
        x1,
        y1,
        x2,
        y2;
    Timeline 
        animation,
        animation2;
    KeyFrame keyframe;
    Color color;
    Line line;
    List<Circle>
        blueCircleArray,
        greenCircleArray;
    Random random = new Random();   // for random number generator


    // *****MAIN METHOD*****
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    // Override the start method in the Application class
    @Override
    public void start(Stage primaryStage) {

        // Instantiate array lists
        blueCircleArray = new ArrayList<>();
        greenCircleArray = new ArrayList<>();


        //*****CONFIGURE STAGE*****

        // Create a border pane
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(displayInset));  // Set 10-px inset all around

        // Create HBox control panel for BorderPane Left Region
        controlPane = new VBox(10);   // 10 px = space between widgets

        // Create Pane display pane for BorderPane Center Region
        animationPane = new Pane();

        // Place panes in Border Pane
        borderPane.setLeft(controlPane);
        borderPane.setCenter(animationPane);


        //*****CONFIGURE WIDGETS*****

        // Create Play button
        playButton = new Button("Play");
        playButton.setOnMouseClicked(e -> handlerActivateCircle(blueCircle)); // Register the button with the event handler

        // Create Pause button
        Button pauseButton = new Button("Pause");
        pauseButton.setOnMouseClicked(e -> handlerPauseDrawing());

        // Create Stop & Reset button
        stopAndClearButton = new Button("Stop & Clear");
        stopAndClearButton.setOnMouseClicked(e -> handlerStopAndClearDrawing());

        // Create ChoiceBox for animation delay options (in milliseconds)
        speedChoiceBox = new ChoiceBox<>();
        speedChoiceBox.getItems().addAll("Slow", "Medium", "Fast");
        speedChoiceBox.setValue("Medium");  // Default speed
        speedChoiceBox.setOnAction(e -> handlerSetDelay());

        // Place widgets in controlPane pane
        controlPane.getChildren().addAll(playButton, pauseButton, stopAndClearButton, speedChoiceBox);

        
        // *****CONFIGURE ANIMATION*****
        drawBlueCircle();
        drawGreenCircles();
        createRandomCircle(blueCircleRadius, Color.BLUE);


        // *****PLACE SCENE IN STAGE*****
        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, sceneWidth, sceneHeight);
        primaryStage.setTitle("Random Walk by Celia Ho");  // Set stage title
        primaryStage.setScene(scene);   // Place scene in stage
        primaryStage.show();    // Display the stage
    
    // Start method ends    
    }


    //*****METHODS FOR EVENT HANDLERS*****

    // Event handler for Start button
    public void handlerActivateCircle(Circle anyCircle) {
        // Create a new animation timeline with the keyframe timing duration (animation speed) selected in the Speed ChoiceBox, and use a Lambda expression to create an event listener that calls the randomWalk method
        animation = new Timeline(new KeyFrame(Duration.millis(delay), e -> randomWalk(anyCircle)));
        // Repeat animation 2000 times
        animation.setCycleCount(2000);
        animation.play();
        System.out.println("Playing animation");
    } 

    // Event handler for Pause button
    public void handlerPauseDrawing() {
        animation.pause();
        System.out.println("Pausing animation");
    }

    // Event handler for Stop & Clear button
    public void handlerStopAndClearDrawing() {
        animation.stop();
        animationPane.getChildren().clear();      // Clear existing circles & lines
        // circleArray.clear();
        System.out.println("Stopping & clearing animation");
    }

    // Event handler for Animation Speed ChoiceBox
    public void handlerSetDelay() {
        String speed = speedChoiceBox.getValue();
        switch (speed) {
            case "Slow":
                delay = 500;
                System.out.println("Slow animation speed selected");
                break;
            case "Medium":
                delay = 250;
                System.out.println("Medium animation speed selected");
                break;
            case "Fast":
                delay = 50;
                System.out.println("Fast animation speed selected");
                break;
            default: 
                delay = 250;    // Default speed is medium
        }
    }


    // *****OTHER METHOD DEFINITIONS*****

    public Circle createRandomCircle(double radius, Color color) {
        // Create random x & y for circle that's within screen bounds
        double centerX = random.nextDouble() * (sceneWidth - 2 * radius) + radius;  // range = (max - min) + 1
        double centerY = random.nextDouble() * (sceneHeight - 2 * radius) + radius;
        // Create a circle with randomized centerX and randomized centerY and specified radius and color fill
        circle = new Circle(centerX, centerY, radius, color);
        return circle;
    }

    public void drawBlueCircle() {
        blueCircle = new Circle(blueCircleRadius, Color.BLUE);
        // Set circle at center of pane, widthwise & heightwise
        blueCircle.setCenterX(sceneWidth / 2);
        blueCircle.setCenterY(sceneHeight / 2);
        animationPane.getChildren().add(blueCircle);
        blueCircleArray.add(blueCircle);
    }
    
    public void drawGreenCircles() {
        for (int i = 0; i < numberInitialGreenCircles; i++) {
            greenCircle = createRandomCircle(greenCircleRadius, Color.GREEN);
            animationPane.getChildren().add(greenCircle);
            greenCircleArray.add(greenCircle);
        }
    }

    public void drawCirclesAndLines(double x2, double y2, Circle anyCircle) { 
        double x1 = anyCircle.getCenterX();
        double y1 = anyCircle.getCenterY();
        Circle aCircle = new Circle(anyCircle.getRadius(), anyCircle.getFill());
        aCircle.setCenterX(x1);
        aCircle.setCenterY(y1);
        anyCircle.setCenterX(x2);
        anyCircle.setCenterY(y2);
        line = new Line(x1, y1, x2, y2);
        line.setStroke(anyCircle.getFill());
        animationPane.getChildren().addAll(aCircle, line);

        // Check for intersection with green circles and turn them red...
        for (Circle greenCircle : greenCircleArray) {
            if (greenCircle.getFill() == Color.GREEN &&
                    greenCircle.intersects(aCircle.getBoundsInLocal())) {
                greenCircle.setFill(Color.RED);
                greenCircle.setRadius(intersectedCircleRadius);
                // ...then mimic blue circles' random walking pattern
                animation2 = new Timeline(new KeyFrame(Duration.millis(delay), e -> randomWalk(greenCircle)));
                animation2.setCycleCount(2000);
                animation2.play();    
            }
        }
    }
    
    public void randomWalk(Circle anyCircle) {
        int direction = random.nextInt(8);
        int randomSteps = random.nextInt(90) + 10;   // between 1-10 steps; 1 step = 10 px

        double x2 = anyCircle.getCenterX();
        double y2 = anyCircle.getCenterY();

        switch(direction) {
            case 0:     // Move N
                y2 -= randomSteps;   
                    break;
            case 1:     // Move NE
                x2 += randomSteps;
                y2 -= randomSteps;
                break;
            case 2:     // Move E
                x2 += randomSteps;
                break;
            case 3:     // Move SE
                x2 += randomSteps;
                y2 += randomSteps;
                break;
            case 4:     // Move S
                y2 += randomSteps;
                break;
            case 5:     // Move SW
                x2 -= randomSteps;
                y2 += randomSteps;
                break;
            case 6:     // Move W
                x2 -= randomSteps;
                break;
            case 7:     // Move NW
                x2 -= randomSteps;
                y2 -= randomSteps;
        }

        // Keep animation within bounds of screen
        if (x2 > sceneWidth - 200) {
            x2 = sceneWidth - 150;
        }
        if (x2 < displayInset) {
            x2 = 100;
        }
        if (y2 > sceneHeight - 50) {
            y2 = sceneHeight - 100;
        }
        if (y2 < displayInset) {
            y2 = 30;
        }

        drawCirclesAndLines(x2, y2, anyCircle);
    }

// End of Random_Walk_2 class
}