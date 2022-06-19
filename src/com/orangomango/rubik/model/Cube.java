package com.orangomango.rubik.model;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.Box;
import javafx.geometry.Point3D;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.util.*;

public class Cube {	
	private InnerCube[][][] cube = new InnerCube[SIZE][SIZE][SIZE];
	private Rotate rotateX = new Rotate(45, 100, 100, 100, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(-45, 100, 100, 100, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, 100, 100, 100, Rotate.Z_AXIS);
	public int mx, my, mz;
	
	public static final int INNER_CUBE_WIDTH = 100;
	public static final int SIZE = 3;
	public static final int MOVE_DURATION = 400;
	public static final int SCRAMBLE_MOVES = 10;
	
	public Cube(){
		generateCube();
	}
	
	public void generateCube(){
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					this.cube[z][y][x] = new InnerCube(x, y, z);
				}
			}
		}
	}
	
	private List<InnerCube> findCubesByColor(Color color){
		List<InnerCube> output = new ArrayList<>();
		
		for (int z = 0; z < SIZE; z++){
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					List<Face.Faces> faces = this.cube[z][y][x].getVisibleFaces();
					for (Face.Faces f : faces){
						if (f.getColor() == color){
							output.add(this.cube[z][y][x]);
							break;
						}
					}
				}
			}
		}
		
		return output;
	}
	
	public List<String> solve(){
		List<String> solution = new ArrayList<>();
		
		// build white face (top)
		List<InnerCube> whites = findCubesByColor(Color.WHITE);
		List<InnerCube> middleWhites = new ArrayList<>();
		for (InnerCube ic : whites){
			if (ic.getVisibleFaces().size() == 2) middleWhites.add(ic);
		}
		System.out.println(middleWhites);
		
		return solution;
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
		Group root = new Group();
		
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
	
	public void rotateX(int index, int direction){
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
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-1;
				slice[y][x].relY = y-1;
			}
		}
		
		Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					slice[y][x].getModel().getTransforms().add(new Rotate(-10*direction, 100-slice[y][x].getStartX(), 100-slice[y][x].getStartY(), 100-slice[y][x].getStartZ(), slice[y][x].xAxis));
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
			
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					int oldX = slice[y][x].relX;
					int oldY = slice[y][x].relY;
					int newX = (int)Math.round(oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction));
					int newY = (int)Math.round(oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction));
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].y = newY+1;
					slice[y][x].z = newX+1;
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(-90*direction, slice[y][x].xAxis)));
					Point3D yA = slice[y][x].zAxis.multiply(direction);
					Point3D zA = slice[y][x].yAxis.multiply(-1).multiply(direction);
					slice[y][x].yAxis = yA;
					slice[y][x].zAxis = zA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		});
		movement.play();
	}
	
	public void rotateY(int index, int direction){
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
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-1;
				slice[y][x].relY = y-1;
			}
		}
		
		Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					slice[y][x].getModel().getTransforms().add(new Rotate(-10*direction, 100-slice[y][x].getStartX(), 100-slice[y][x].getStartY(), 100-slice[y][x].getStartZ(), slice[y][x].yAxis));
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
			
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					int oldX = slice[y][x].relX;
					int oldY = slice[y][x].relY;
					int newX = (int)Math.round(oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction));
					int newY = (int)Math.round(oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction));
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].x = newX+1;
					slice[y][x].z = newY+1;
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(-90*direction, slice[y][x].yAxis)));
					Point3D xA = slice[y][x].zAxis.multiply(-1).multiply(direction);
					Point3D zA = slice[y][x].xAxis.multiply(direction);
					slice[y][x].xAxis = xA;
					slice[y][x].zAxis = zA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		});
		movement.play();
	}
	
	public void rotateZ(int index, int direction){
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
		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++){
				slice[y][x].relX = x-1;
				slice[y][x].relY = y-1;
			}
		}
		
		Timeline movement = new Timeline(new KeyFrame(Duration.millis(MOVE_DURATION/9), e -> {
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					slice[y][x].getModel().getTransforms().add(new Rotate(10*direction, 100-slice[y][x].getStartX(), 100-slice[y][x].getStartY(), 100-slice[y][x].getStartZ(), slice[y][x].zAxis));
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
		
			// Rotate the slice
			for (int y = 0; y < SIZE; y++){
				for (int x = 0; x < SIZE; x++){
					int oldX = slice[y][x].relX;
					int oldY = slice[y][x].relY;
					int newX = (int)Math.round(oldX*Math.cos(Math.PI/2*direction)-oldY*Math.sin(Math.PI/2*direction));
					int newY = (int)Math.round(oldX*Math.sin(Math.PI/2*direction)+oldY*Math.cos(Math.PI/2*direction));
					slice[y][x].relX = newX;
					slice[y][x].relY = newY;
					slice[y][x].y = newY+1;
					slice[y][x].x = newX+1;
					slice[y][x].getModel().getTransforms().set(0, slice[y][x].getModel().getTransforms().get(0).createConcatenation(new Rotate(90*direction, slice[y][x].zAxis)));
					Point3D xA = slice[y][x].yAxis.multiply(-1).multiply(direction);
					Point3D yA = slice[y][x].xAxis.multiply(direction);
					slice[y][x].xAxis = xA;
					slice[y][x].yAxis = yA;
				}
			}
			
			// Update the array with the new slice
			this.sortArray();
		});
		movement.play();
	}
	
	private InnerCube findCubeByPos(int pX, int pY, int pZ){
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
