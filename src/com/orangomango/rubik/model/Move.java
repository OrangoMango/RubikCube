package com.orangomango.rubik.model;

import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;

public class Move {
	public static boolean animating = false;
	public static String[] moves = new String[]{"F", "f", "R", "r", "U", "u", "L", "l", "B", "b", "D", "d"};
	
	/**
	 * Useful algorithms to solve the cube:
	 * 
	 */
	public static String parseNotation(String input){
		input = input.toUpperCase();
		if (input.length() == 0) return null;
		StringBuilder output = new StringBuilder();
		for (int count = 0; count < input.toCharArray().length; count++){
			String m = Character.toString(input.toCharArray()[count]);
			if (count+1 < input.length()){
				String after = Character.toString(input.charAt(count+1));
				if (after.equals("'")){
					if ("FRULBD".contains(m)){
						output.append(m.toLowerCase());
						// check number
						if (count+2 < input.length()){
							if ("23456789".contains(Character.toString(input.charAt(count+2)))){
								output.append(m.toLowerCase().repeat(Integer.parseInt(Character.toString(input.charAt(count+2)))-1)); // '(' and ')' not supported
								count++;
							}
						}
						count++;
					} else {
						return null;
					}
				} else if ("23456789".contains(after)){
					int repeat = Integer.parseInt(after);
					output.append(m.repeat(repeat));
					count++;
				} else {
					if ("FRULBD".contains(m)){
						output.append(m);
					} else {
						return null;
					}
				}
			} else {
				if ("FRULBD".contains(m)){
					output.append(m);
				} else {
					return null;
				}
			}
		}
		return output.toString();
	}
	
	public static void applyMove(String move, Cube cube){
		switch (move){
			case "F":
				FRONT_CLOCKWISE(cube);
				break;
			case "f":
				FRONT_COUNTERCLOCKWISE(cube);
				break;
			case "R":
				RIGHT_CLOCKWISE(cube);
				break;
			case "r":
				RIGHT_COUNTERCLOCKWISE(cube);
				break;
			case "U":
				UP_CLOCKWISE(cube);
				break;
			case "u":
				UP_COUNTERCLOCKWISE(cube);
				break;
			case "L":
				LEFT_CLOCKWISE(cube);
				break;
			case "l":
				LEFT_COUNTERCLOCKWISE(cube);
				break;
			case "B":
				BACK_CLOCKWISE(cube);
				break;
			case "b":
				BACK_COUNTERCLOCKWISE(cube);
				break;
			case "D":
				DOWN_CLOCKWISE(cube);
				break;
			case "d":
				DOWN_COUNTERCLOCKWISE(cube);
				break;
			default:
				System.out.println("Invalid move: "+move);
		}
	}
	
	public static String getRotationDirection(String move, int amount, Point3D axis){
		String[] xMoves, yMoves, zMoves;
		switch (move.toUpperCase()){
			case "F":
				xMoves = new String[]{"D", "b", "U"};
				yMoves = new String[]{"R", "B", "l"};
				zMoves = new String[]{"F", "F", "F"};
				break;
			case "R":
				xMoves = new String[]{"R", "R", "R"};
				yMoves = new String[]{"B", "L", "F"};
				zMoves = new String[]{"U", "L", "D"};
				break;
			case "U":
				xMoves = new String[]{"b", "D", "F"};
				yMoves = new String[]{"U", "U", "U"};
				zMoves = new String[]{"L", "D", "R"};
				break;
			case "L":
				xMoves = new String[]{"L", "L", "L"};
				yMoves = new String[]{"F", "R", "B"};
				zMoves = new String[]{"D", "R", "U"};
				break;
			case "B":
				xMoves = new String[]{"D", "F", "U"};
				yMoves = new String[]{"L", "F", "R"};
				zMoves = new String[]{"B", "B", "B"};
				break;
			case "D":
				xMoves = new String[]{"F", "U", "B"};
				yMoves = new String[]{"D", "D", "D"};
				zMoves = new String[]{"R", "U", "L"};
				break;
			default:
				return null;
		}
		
		if (amount == 0) return move;
		
		if (amount > 3){
			return null;
		}
		
		if (axis == Rotate.X_AXIS){
			return xMoves[amount-1];
		} else if (axis == Rotate.Y_AXIS){
			return yMoves[amount-1];
		} else if (axis == Rotate.Z_AXIS){
			return zMoves[amount-1];
		} else {
			return null;
		}
	}
	
	public static void FRONT_CLOCKWISE(Cube cube){		
		if (animating) return;
		animating = true;
		cube.rotateZ(0, 1);
	}
	
	public static void RIGHT_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(2, 1);
	}
	
	public static void UP_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(0, -1);
	}
	
	public static void LEFT_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(0, -1);
	}
	
	public static void BACK_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(2, -1);
	}
	
	public static void DOWN_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(2, 1);
	}
	
	public static void FRONT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(0, -1);
	}
	
	public static void RIGHT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(2, -1);
	}
	
	public static void UP_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(0, 1);
	}
	
	public static void LEFT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(0, 1);
	}
	
	public static void BACK_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(2, 1);
	}
	
	public static void DOWN_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(2, -1);
	}
}
