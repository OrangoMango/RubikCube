package com.orangomango.rubik;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.Color;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;

import com.orangomango.rubik.model.Cube;
import com.orangomango.rubik.model.Move;

public class MainApplication extends Application {
	
	private boolean scr;
	private List<String> moves = new ArrayList<>();
	private int counter;
	private int acounter;
	private int thisMove;
	private static final Rectangle2D bounds = new Rectangle2D(0, 0, 420, 840);
	private static final int CAMERA_X = -110;
	private static final int CAMERA_Y = -200;
	private static final int CAMERA_Z = -350;
	
	// Camera rotation
	private double mouseOldX, mouseOldY;
    private int rx, ry, rz;
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage){
		Cube cube = new Cube();
		
		PerspectiveCamera camera = new PerspectiveCamera(false);
		camera.setTranslateX(CAMERA_X);
		camera.setTranslateY(CAMERA_Y);
		camera.setTranslateZ(CAMERA_Z);
		
		// Axis are used for debugging
		Box xAxis = new Box(600, 3, 3);
		xAxis.setMaterial(new PhongMaterial(Color.RED));
		xAxis.setTranslateX(300);
		Box yAxis = new Box(3, 600, 3);
		yAxis.setMaterial(new PhongMaterial(Color.BLUE));
		yAxis.setTranslateY(300);
		Box zAxis = new Box(3, 3, 600);
		zAxis.setMaterial(new PhongMaterial(Color.GREEN));
		zAxis.setTranslateZ(300);
		
		TilePane controls = new TilePane();
		controls.setHgap(5);
		controls.setVgap(5);
		Button front = new Button("F");
		front.setOnAction(e -> Move.FRONT_CLOCKWISE(cube));
		Button frontP = new Button("F'");
		frontP.setOnAction(e -> Move.FRONT_COUNTERCLOCKWISE(cube));
		Button back = new Button("B");
		back.setOnAction(e -> Move.BACK_CLOCKWISE(cube));
		Button backP = new Button("B'");
		backP.setOnAction(e -> Move.BACK_COUNTERCLOCKWISE(cube));
		Button right = new Button("R");
		right.setOnAction(e -> Move.RIGHT_CLOCKWISE(cube));
		Button rightP = new Button("R'");
		rightP.setOnAction(e -> Move.RIGHT_COUNTERCLOCKWISE(cube));
		Button left = new Button("L");
		left.setOnAction(e -> Move.LEFT_CLOCKWISE(cube));
		Button leftP = new Button("L'");
		leftP.setOnAction(e -> Move.LEFT_COUNTERCLOCKWISE(cube));
		Button up = new Button("U");
		up.setOnAction(e -> Move.UP_CLOCKWISE(cube));
		Button upP = new Button("U'");
		upP.setOnAction(e -> Move.UP_COUNTERCLOCKWISE(cube));
		Button down = new Button("D");
		down.setOnAction(e -> Move.DOWN_CLOCKWISE(cube));
		Button downP = new Button("D'");
		downP.setOnAction(e -> Move.DOWN_COUNTERCLOCKWISE(cube));

		/*
		Button rxp = new Button("RX +");
		rxp.setOnAction(e -> cube.getRotateX().setAngle(cube.getRotateX().getAngle()+10));
		Button rxn = new Button("RX -");
		rxn.setOnAction(e -> cube.getRotateX().setAngle(cube.getRotateX().getAngle()-10));
		Button ryp = new Button("RY +");
		ryp.setOnAction(e -> cube.getRotateY().setAngle(cube.getRotateY().getAngle()+10));
		Button ryn = new Button("RY -");
		ryn.setOnAction(e -> cube.getRotateY().setAngle(cube.getRotateY().getAngle()-10));
		Button rzp = new Button("RZ +");
		rzp.setOnAction(e -> cube.getRotateZ().setAngle(cube.getRotateZ().getAngle()+10));
		Button rzn = new Button("RZ -");
		rzn.setOnAction(e -> cube.getRotateZ().setAngle(cube.getRotateZ().getAngle()-10));
		*/

		Button reset = new Button("Reset");
		Button scramble = new Button("Scramble");
		Button reassemble = new Button("Reassemble");
		Label currentMove = new Label("Move: 0");
		reassemble.setOnAction(e -> {
			if (scr || moves.size() == 0) return;
			reset.setDisable(true);
			scr = true;
			Collections.reverse(moves);
			Timeline reassembling = new Timeline(new KeyFrame(Duration.millis(Cube.MOVE_DURATION*1.3), evt -> {
				String mv = moves.get(counter++);
				Move.applyMove("FRULBD".contains(mv) ? mv.toLowerCase() : mv.toUpperCase(), cube);
				currentMove.setText("Move: "+(Cube.SCRAMBLE_MOVES-counter));
			}));
			reassembling.setCycleCount(Cube.SCRAMBLE_MOVES);
			reassembling.setOnFinished(evt -> {
				scr = false;
				counter = 0;
				moves.clear();
				reset.setDisable(false);
			});
			reassembling.play();
		});
		
		TextField input = new TextField();
		input.setPromptText("RUF3F'U'L2B'2");
		Button read = new Button("Apply");
		Button oppo = new Button("Opposite");
		Button solve = new Button("Solve");
		solve.setOnAction(e -> cube.solve());
		solve.setDisable(true);
		read.setOnAction(e -> {
			String parsed = Move.parseNotation(input.getText());
			if (parsed == null){
				System.out.println("Error in your syntax");
				return;
			}
			read.setDisable(true);
			reset.setDisable(true);
			oppo.setDisable(true);
			char[] m = parsed.toCharArray();
			Timeline moving = new Timeline(new KeyFrame(Duration.millis(Cube.MOVE_DURATION*1.3), evt -> Move.applyMove(Character.toString(m[acounter++]), cube)));
			moving.setCycleCount(m.length);
			moving.setOnFinished(evt -> {
				acounter = 0;
				read.setDisable(false);
				reset.setDisable(false);
				oppo.setDisable(false);
			});
			moving.play();
		});
		oppo.setOnAction(e -> {
			String parsed = Move.parseNotation(input.getText());
			if (parsed == null){
				System.out.println("Error in your syntax");
				return;
			}
			read.setDisable(true);
			reset.setDisable(true);
			oppo.setDisable(true);
			char[] m = parsed.toCharArray();
			Timeline moving = new Timeline(new KeyFrame(Duration.millis(Cube.MOVE_DURATION*1.3), evt -> {
				String str = Character.toString(m[m.length-1-(acounter++)]);
				Move.applyMove("FRULBD".contains(str) ? str.toLowerCase() : str.toUpperCase(), cube);
			}));
			moving.setCycleCount(m.length);
			moving.setOnFinished(evt -> {
				acounter = 0;
				read.setDisable(false);
				reset.setDisable(false);
				oppo.setDisable(false);
			});
			moving.play();
		});

		Random random = new Random();
		scramble.setOnAction(e -> {
			if (scr || moves.size() != 0) return;
			reset.setDisable(true);
			read.setDisable(true);
			oppo.setDisable(true);
			scr = true;
			Timeline scrambling = new Timeline(new KeyFrame(Duration.millis(Cube.MOVE_DURATION*1.3), evt -> {
				String mv = Move.moves[random.nextInt(Move.moves.length)];
				moves.add(mv);
				Move.applyMove(mv, cube);
				currentMove.setText("Move: "+(thisMove++));
			}));
			scrambling.setCycleCount(Cube.SCRAMBLE_MOVES);
			scrambling.setOnFinished(evt -> {
				scr = false;
				thisMove = 0;
				reset.setDisable(false);
				read.setDisable(false);
				oppo.setDisable(false);
			});
			scrambling.play();
		});

		controls.getChildren().addAll(front, frontP, back, backP, right, rightP, left, leftP, up, upP, down, downP, new Separator(), reset, scramble, reassemble, currentMove);
		
		SubScene scene = new SubScene(new Group(cube.getModel()), bounds.getWidth()-10, bounds.getHeight()*0.70, true, SceneAntialiasing.BALANCED);
		reset.setOnAction(e -> {
			cube.generateCube();
			scene.setRoot(new Group(cube.getModel()));
			currentMove.setText("Move: 0");
			cube.getRotateX().setAngle(45);
			cube.getRotateY().setAngle(-45);
			cube.getRotateZ().setAngle(0);
			camera.getTransforms().clear();
		});
		scene.setFocusTraversable(true);
		scene.setFill(Color.CYAN);
		scene.setCamera(camera);
		
		scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
			double rotX = event.getSceneY() - mouseOldY;
			double rotY = event.getSceneX() - mouseOldX;
            this.rx -= rotX;
            this.ry += rotY;
            camera.getTransforms().addAll(new Rotate(-rotX, 100-CAMERA_X, 100-CAMERA_Y, 100-CAMERA_Z, Rotate.X_AXIS), new Rotate(rotY, 100-CAMERA_X, 100-CAMERA_Y, 100-CAMERA_Z, Rotate.Y_AXIS));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });
        
        //System.out.println(Move.getRotationDirection(Move.getRotationDirection(Move.getRotationDirection("U", 1, Rotate.X_AXIS), 1, Rotate.Y_AXIS), 0, Rotate.Z_AXIS));
		
		VBox layout = new VBox(scene, controls, new HBox(10, input, read, oppo, solve));
		layout.setSpacing(15);
		layout.setPadding(new Insets(5, 5, 5, 5));
		
		stage.setScene(new Scene(layout, bounds.getWidth(), bounds.getHeight()));
		stage.setTitle("Rubik's cube");
		stage.show();
	}
}
