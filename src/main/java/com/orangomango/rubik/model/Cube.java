package com.orangomango.rubik.model;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Box;
import javafx.geometry.Point3D;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;

import java.util.*;

public class Cube{	
	private InnerCube[][][] cube = new InnerCube[SIZE][SIZE][SIZE];
	private Rotate rotateX = new Rotate(45, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, Rotate.X_AXIS); // 45
	private Rotate rotateY = new Rotate(-45, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, Rotate.Y_AXIS); // -45
	private Rotate rotateZ = new Rotate(0, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, (SIZE/2.0+0.5-1)*100, Rotate.Z_AXIS);
	private FaceSystem faceSystem = new FaceSystem();
	public int psY;
	private Point3D xAxis = Rotate.X_AXIS;
	private Point3D yAxis = Rotate.Y_AXIS;
	private Point3D zAxis = Rotate.Z_AXIS;
	private Group model;
	public String solvingAlgorithm;
	
	public static int SIZE = 3;
	public static final int INNER_CUBE_WIDTH = 100;
	public static int DEFAULT_DURATION = 80;
	public static int MOVE_DURATION = DEFAULT_DURATION;
	public static final int SCRAMBLE_MOVES = 70;
	
	public Cube(){
		generateCube();
	}
	
	public FaceSystem getFaces(){
		return this.faceSystem;
	}
	
	public void generateCube(){
		psY = 0;
		solvingAlgorithm = null;
		this.model = null;
		this.xAxis = Rotate.X_AXIS;
		this.yAxis = Rotate.Y_AXIS;
		this.zAxis = Rotate.Z_AXIS;
		this.faceSystem = new FaceSystem();
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					this.cube[z][y][x] = new InnerCube(x, y, z);
				}
			}
		}
	}
	
	public List<InnerCube> findCubesByColor(Color... colors){
		List<InnerCube> output = new ArrayList<>();
		
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					List<Face.Faces> faces = this.cube[z][y][x].getVisibleFaces();
					if (faces.size() >= colors.length){
						boolean accepted = false;
						for (Face.Faces f : faces){
							if (Arrays.asList(colors).contains(f.getColor())){
								accepted = true;
							} else {
								accepted = false;
								break;
							}
						}
						if (accepted){
							output.add(this.cube[z][y][x]);
							break;
						}
					}
				}
			}
		}
		
		return output;
	}
	
	public void solve(){
		CubeSolver solver = new CubeSolver(this);
		this.solvingAlgorithm = Move.printAlgorithm(solver.solve());
	}
	
	public void rotateCubeY(int direction){
		if (this.psY == 0 && direction < 0) return;
		this.psY += direction;
		Point3D xA = this.zAxis.multiply(direction);
		Point3D zA = this.xAxis.multiply(-1).multiply(direction);
		this.xAxis = xA;
		this.zAxis = zA;
		this.faceSystem.rotate(-direction, Rotate.Y_AXIS);
		if (Move.ANIMATION){
			Move.animating = true;
			Timeline loop = new Timeline(new KeyFrame(Duration.millis(Cube.MOVE_DURATION/9), e -> getModel().getTransforms().add(new Rotate(10*direction, 100, 100, 100, this.yAxis))));
			loop.setCycleCount(9);
			loop.setOnFinished(e -> Move.animating = false);
			loop.play();
		}
		//System.out.println("--- "+this.faceSystem);
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					this.cube[z][y][x].getFaceSystem().rotate(-direction, this.yAxis);
				}
			}
		}
	}
	
	private void sortArray(){
		InnerCube[][][] temp = new InnerCube[SIZE][SIZE][SIZE];
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					temp[z][y][x] = findCubeByPos(x, y, z);
					temp[z][y][x].relX = null;
					temp[z][y][x].relY = null;
				}
			}
		}
		this.cube = temp;
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					InnerCube ic = this.cube[z][y][x];
					ic.getModel().setTranslateX(ic.x*INNER_CUBE_WIDTH);
					ic.getModel().setTranslateY(ic.y*INNER_CUBE_WIDTH);
					ic.getModel().setTranslateZ(ic.z*INNER_CUBE_WIDTH);
				}
			}
		}
		
		Move.animating = false;
	}
	
	public Group getModel(){
		if (this.model != null) return this.model;
		Group root = new Group();
		this.model = root;
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					root.getChildren().addAll(this.cube[z][y][x].getModel());
				}
			}
		}
		
		root.getTransforms().addAll(this.rotateX, this.rotateY, this.rotateZ);
		return root;
	}
	
	public void rotateX(int index, int direction, boolean animation){
		InnerCube[][] slice = new InnerCube[SIZE][SIZE];
		
		// Select the slice from the cube
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					if (this.cube[z][y][x].x == index){
						slice[y][z] = this.cube[z][y][x];
					}
				}
			}
		}
		
		// Setup relX and relY
		final double offset = (SIZE/2.0+0.5)-1;
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-offset;
				slice[y][x].relY = y-offset;
			}
		}
		
		final Runnable rotatorX = () -> {
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					double oldX = slice[y][x].relX;
					double oldY = slice[y][x].relY;
					double newX = oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction);
					double newY = oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction);
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].y = (int)Math.round(newY+offset);
					slice[y][x].z = (int)Math.round(newX+offset);
					slice[y][x].getFaceSystem().rotate(direction, this.xAxis);
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(-90*direction, slice[y][x].xAxis)));
					Point3D yA = slice[y][x].zAxis.multiply(direction);
					Point3D zA = slice[y][x].yAxis.multiply(-1).multiply(direction);
					slice[y][x].yAxis = yA;
					slice[y][x].zAxis = zA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		};
		
		if (animation){
			Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().add(new Rotate(-10*direction, offset*100-slice[y][x].getStartX(), offset*100-slice[y][x].getStartY(), offset*100-slice[y][x].getStartZ(), slice[y][x].xAxis));
					}
				}
			}));
			movement.setCycleCount(9);
			movement.setOnFinished(e -> {
				// Remove animation
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().remove(slice[y][x].getModel().getTransforms().size()-9, slice[y][x].getModel().getTransforms().size());
					}
				}
				rotatorX.run();
			});
			movement.play();
		} else {
			rotatorX.run();
		}
	}
	
	public void rotateY(int index, int direction, boolean animation){
		InnerCube[][] slice = new InnerCube[SIZE][SIZE];
		
		// Select the slice from the cube
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					if (this.cube[z][y][x].y == index){
						slice[z][x] = this.cube[z][y][x];
					}
				}
			}
		}
		
		// Setup relX and relY
		final double offset = (SIZE/2.0+0.5)-1;
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-offset;
				slice[y][x].relY = y-offset;
			}
		}
		
		final Runnable rotatorY = () -> {
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					double oldX = slice[y][x].relX;
					double oldY = slice[y][x].relY;
					double newX = oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction);
					double newY = oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction);
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].x = (int)Math.round(newX+offset);
					slice[y][x].z = (int)Math.round(newY+offset);
					slice[y][x].getFaceSystem().rotate(direction, this.yAxis);
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(-90*direction, slice[y][x].yAxis)));
					Point3D xA = slice[y][x].zAxis.multiply(-1).multiply(direction);
					Point3D zA = slice[y][x].xAxis.multiply(direction);
					slice[y][x].xAxis = xA;
					slice[y][x].zAxis = zA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		};
		
		if (animation){
			Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().add(new Rotate(-10*direction, offset*100-slice[y][x].getStartX(), offset*100-slice[y][x].getStartY(), offset*100-slice[y][x].getStartZ(), slice[y][x].yAxis));
					}
				}
			}));
			movement.setCycleCount(9);
			movement.setOnFinished(e -> {
				// Remove animation
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().remove(slice[y][x].getModel().getTransforms().size()-9, slice[y][x].getModel().getTransforms().size());
					}
				}
				
				rotatorY.run();
			});
			movement.play();
		} else {
			rotatorY.run();
		}
	}
	
	public void rotateZ(int index, int direction, boolean animation){
		InnerCube[][] slice = new InnerCube[SIZE][SIZE];
		
		// Select the slice from the cube
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					if (this.cube[z][y][x].z == index){
						slice[y][x] = this.cube[z][y][x];
					}
				}
			}
		}
		
		// Setup relX and relY
		final double offset = (SIZE/2.0+0.5)-1;
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-offset;
				slice[y][x].relY = y-offset;
			}
		}
		
		final Runnable rotatorZ = () -> {
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					double oldX = slice[y][x].relX;
					double oldY = slice[y][x].relY;
					double newX = oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction);
					double newY = oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction);
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].y = (int)Math.round(newY+offset);
					slice[y][x].x = (int)Math.round(newX+offset);
					slice[y][x].getFaceSystem().rotate(direction, this.zAxis);
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(90*direction, slice[y][x].zAxis)));
					Point3D xA = slice[y][x].yAxis.multiply(-1).multiply(direction);
					Point3D yA = slice[y][x].xAxis.multiply(direction);
					slice[y][x].xAxis = xA;
					slice[y][x].yAxis = yA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		};
		
		if (animation){
			Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().add(new Rotate(10*direction, offset*100-slice[y][x].getStartX(), offset*100-slice[y][x].getStartY(), offset*100-slice[y][x].getStartZ(), slice[y][x].zAxis));
					}
				}
			}));
			movement.setCycleCount(9);
			movement.setOnFinished(e -> {
				// Remove animation
				for (int y = 0; y < SIZE; y++){
					for (int x = 0; x < SIZE; x++){
						slice[y][x].getModel().getTransforms().remove(slice[y][x].getModel().getTransforms().size()-9, slice[y][x].getModel().getTransforms().size());
					}
				}
			
				rotatorZ.run();
			});
			movement.play();
		} else {
			rotatorZ.run();
		}
	}
	
	public InnerCube findCubeByPos(int pX, int pY, int pZ){
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					InnerCube ic = this.cube[z][y][x];
					if (ic.x == pX && ic.y == pY && ic.z == pZ) return ic;
				}
			}
		}
		return null;
	}
	
	public InnerCube findCubeByViewCoords(int pX, int pY, int pZ){
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					InnerCube ic = this.cube[z][y][x];
					if (ic.getViewCoords(this).getX() == pX && ic.getViewCoords(this).getY() == pY && ic.getViewCoords(this).getZ() == pZ) return ic;
				}
			}
		}
		return null;
	}
	
	public Rotate getRotateZ(){
		return this.rotateZ;
	}
	
	public Rotate getRotateY(){
		return this.rotateY;
	}
	
	public Rotate getRotateX(){
		return this.rotateX;
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					builder.append(this.cube[z][y][x]).append(" ");
				}
				builder.append("\n");
			}
			builder.append("\n\n");
		}
		
		return builder.toString();
	}
}
