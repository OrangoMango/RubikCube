package com.orangomango.rubik.model;

import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;

import java.util.*;

public class Move {
	public static class Wrapper{
		private int axis, pos, dir;
		
		public Wrapper(int a, int p, int d){
			this.axis = a;
			this.pos = p;
			this.dir = d;
		}
		
		public Wrapper opposite(){
			return new Wrapper(this.axis, this.pos, -this.dir);
		}
	}
	
	public static volatile boolean animating = false;
	public static String[] moves = new String[]{"F", "f", "R", "r", "U", "u", "L", "l", "B", "b", "D", "d"};
	public static final String CAPS = "FRULBDMESY";
	public static boolean ANIMATION = true;
	public static String currentMove;
	
	public static String printAlgorithm(String input){
		if (input == null) return null;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++){
			char c = input.charAt(i);
			if (Character.isLowerCase(c)){
				builder.append(Character.toUpperCase(c)+"'");
			} else {
				builder.append(Character.toString(c));
			}
			if (i != input.length()-1){
				char next = input.charAt(i+1);
				if (next == c){
					builder.append("2 ");
					i++;
				} else {
					builder.append(" ");
				}
			} else {
				builder.append(" ");
			}
		}
		return builder.toString();
	}
	
	public static String parseNotation(String input){
		input = input.toUpperCase();
		if (input.length() == 0) return null;
		StringBuilder output = new StringBuilder();
		for (int count = 0; count < input.toCharArray().length; count++){
			String m = Character.toString(input.toCharArray()[count]);
			if (count+1 < input.length()){
				String after = Character.toString(input.charAt(count+1));
				if (after.equals("'")){
					if (CAPS.contains(m)){
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
					if (CAPS.contains(m)){
						output.append(m);
					} else {
						return null;
					}
				}
			} else {
				if (CAPS.contains(m)){
					output.append(m);
				} else {
					return null;
				}
			}
		}
		return output.toString();
	}
	
	public static void genericMove(Wrapper wrapper, Cube cube){
		if (animating) return;
		animating = true;
		switch (wrapper.axis){
			case 0:
				cube.rotateX(wrapper.pos, wrapper.dir, ANIMATION);
				break;
			case 1:
				cube.rotateY(wrapper.pos, wrapper.dir, ANIMATION);
				break;
			case 2:
				cube.rotateZ(wrapper.pos, wrapper.dir, ANIMATION);
				break;
		}
	}
	
	public static Wrapper randomMove(Random random, Cube cube){
		Wrapper w = new Wrapper(random.nextInt(3), Cube.SIZE == 3 ? (random.nextBoolean() ? 0 : 2) : random.nextInt(Cube.SIZE), random.nextInt(2) == 0 ? 1 : -1);
		genericMove(w, cube);
		return w;
	}
	
	public static void applyMove(String move, Cube cube){
		currentMove = move;
		move = getRotationDirection(move, cube.psY, Rotate.Y_AXIS);

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
			case "M":
				MIDDLE_CLOCKWISE(cube);
				break;
			case "m":
				MIDDLE_COUNTERCLOCKWISE(cube);
				break;
			case "E":
				EQUATOR_CLOCKWISE(cube);
				break;
			case "e":
				EQUATOR_COUNTERCLOCKWISE(cube);
				break;
			case "S":
				STANDING_CLOCKWISE(cube);
				break;
			case "s":
				STANDING_COUNTERCLOCKWISE(cube);
				break;
			default:
				System.out.println("Invalid move: "+move);
		}
	}
	
	/**
	 * Get the cube move when it gets rotated.
	 * @param move Cube move to transform, such as F or l. @link{Move.moves}
	 * @param amount how much the cube gets rotated
	 * @param axis Rotation axis
	 * @return The rotated move or <code>null</code> if the axis is invalid
	 */
	public static String getRotationDirection(String move, int amount, Point3D axis){
		String[] xMoves, yMoves, zMoves;
		switch (move.toUpperCase()){
			case "F":
				xMoves = new String[]{"U", "B", "D"};
				yMoves = new String[]{"R", "B", "L"};
				zMoves = new String[]{"F", "F", "F"};
				break;
			case "R":
				xMoves = new String[]{"R", "R", "R"};
				yMoves = new String[]{"B", "L", "F"};
				zMoves = new String[]{"D", "L", "U"};
				break;
			case "U":
				xMoves = new String[]{"F", "D", "B"};
				yMoves = new String[]{"U", "U", "U"};
				zMoves = new String[]{"R", "D", "L"};
				break;
			case "L":
				xMoves = new String[]{"L", "L", "L"};
				yMoves = new String[]{"F", "R", "B"};
				zMoves = new String[]{"U", "R", "D"};
				break;
			case "B":
				xMoves = new String[]{"U", "F", "D"};
				yMoves = new String[]{"L", "F", "R"};
				zMoves = new String[]{"B", "B", "B"};
				break;
			case "D":
				xMoves = new String[]{"B", "U", "F"};
				yMoves = new String[]{"D", "D", "D"};
				zMoves = new String[]{"L", "U", "R"};
				break;
			case "M":
				xMoves = new String[]{"M", "M", "M"};
				yMoves = new String[]{"S", "m", "s"};
				zMoves = new String[]{"e", "m", "E"};
				break;
			case "E":
				xMoves = new String[]{"s", "e", "S"};
				yMoves = new String[]{"E", "E", "E"};
				zMoves = new String[]{"M", "d", "m"};
				break;
			case "S":
				xMoves = new String[]{"E", "s", "e"};
				yMoves = new String[]{"M", "s", "m"};
				zMoves = new String[]{"S", "S", "S"};
				break;
			default:
				return null;
		}
		
		if (amount > 3){
			amount %= 4;
		}
		
		if (amount == 0) return move;
		
		boolean upper = CAPS.contains(move);
		
		if (Math.abs(axis.getX()) == Rotate.X_AXIS.getX()){
			return upper ? xMoves[amount-1] : (CAPS.contains(xMoves[amount-1]) ? xMoves[amount-1].toLowerCase() : xMoves[amount-1].toUpperCase());
		} else if (Math.abs(axis.getY()) == Rotate.Y_AXIS.getY()){
			return upper ? yMoves[amount-1] : (CAPS.contains(yMoves[amount-1]) ? yMoves[amount-1].toLowerCase() : yMoves[amount-1].toUpperCase());
		} else if (Math.abs(axis.getZ()) == Rotate.Z_AXIS.getZ()){
			return upper ? zMoves[amount-1] : (CAPS.contains(zMoves[amount-1]) ? zMoves[amount-1].toLowerCase() : zMoves[amount-1].toUpperCase());
		} else {
			return null;
		}
	}
	
	public static void FRONT_CLOCKWISE(Cube cube){		
		if (animating) return;
		animating = true;
		cube.rotateZ(0, 1, ANIMATION);
	}
	
	public static void RIGHT_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(Cube.SIZE-1, 1, ANIMATION);
	}
	
	public static void UP_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(0, -1, ANIMATION);
	}
	
	public static void LEFT_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(0, -1, ANIMATION);
	}
	
	public static void BACK_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(Cube.SIZE-1, -1, ANIMATION);
	}
	
	public static void DOWN_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(Cube.SIZE-1, 1, ANIMATION);
	}
	
	public static void MIDDLE_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(Cube.SIZE/2, -1, ANIMATION);
	}
	
	public static void EQUATOR_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(Cube.SIZE/2, 1, ANIMATION);
	}
	
	public static void STANDING_CLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(Cube.SIZE/2, 1, ANIMATION);
	}
	
	public static void FRONT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(0, -1, ANIMATION);
	}
	
	public static void RIGHT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(Cube.SIZE-1, -1, ANIMATION);
	}
	
	public static void UP_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(0, 1, ANIMATION);
	}
	
	public static void LEFT_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(0, 1, ANIMATION);
	}
	
	public static void BACK_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(Cube.SIZE-1, 1, ANIMATION);
	}
	
	public static void DOWN_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(Cube.SIZE-1, -1, ANIMATION);
	}
	
	public static void MIDDLE_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateX(Cube.SIZE/2, 1, ANIMATION);
	}
	
	public static void EQUATOR_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateY(Cube.SIZE/2, -1, ANIMATION);
	}
	
	public static void STANDING_COUNTERCLOCKWISE(Cube cube){
		if (animating) return;
		animating = true;
		cube.rotateZ(Cube.SIZE/2, -1, ANIMATION);
	}
}
