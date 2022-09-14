package com.orangomango.rubik.model;

import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;

import java.util.*;

public class FaceSystem{
	private Color topFace = Face.Faces.TOP.getColor();
	private Color bottomFace = Face.Faces.BOTTOM.getColor();
	private Color frontFace = Face.Faces.FRONT.getColor();
	private Color backFace = Face.Faces.BACK.getColor();
	private Color rightFace = Face.Faces.RIGHT.getColor();
	private Color leftFace = Face.Faces.LEFT.getColor();
	
	public void rotate(int amount, Point3D axis){
		if (Math.abs(axis.getX()) == Rotate.X_AXIS.getX()){
			Color[] faces = shiftArray(new Color[]{frontFace, topFace, backFace, bottomFace}, amount*(int)axis.getX());
			frontFace = faces[0];
			topFace = faces[1];
			backFace = faces[2];
			bottomFace = faces[3];
		} else if (Math.abs(axis.getY()) == Rotate.Y_AXIS.getY()){
			Color[] faces = shiftArray(new Color[]{frontFace, rightFace, backFace, leftFace}, amount*(int)axis.getY());
			frontFace = faces[0];
			rightFace = faces[1];
			backFace = faces[2];
			leftFace = faces[3];
		} else if (Math.abs(axis.getZ()) == Rotate.Z_AXIS.getZ()){
			Color[] faces = shiftArray(new Color[]{topFace, rightFace, bottomFace, leftFace}, amount*(int)axis.getZ());
			topFace = faces[0];
			rightFace = faces[1];
			bottomFace = faces[2];
			leftFace = faces[3];
		} else {
			throw new IllegalArgumentException("Invalid axis: "+axis);
		}
	}
	
	private static Color[] shiftArray(Color[] array, int amount){
		List<Color> temp = Arrays.asList(array);
		Collections.rotate(temp, amount);
		return temp.toArray(new Color[array.length]);
		
	}
	
	public Color getTopFace(){
		return this.topFace;
	}
	
	public Color getBottomFace(){
		return this.bottomFace;
	}
	
	public Color getFrontFace(){
		return this.frontFace;
	}
	
	public Color getBackFace(){
		return this.backFace;
	}
	
	public Color getRightFace(){
		return this.rightFace;
	}
	
	public Color getLeftFace(){
		return this.leftFace;
	}
	
	public static String printColor(Color color){
		if (color == Color.WHITE)
			return "WHITE";
		if (color == Color.ORANGE)
			return "ORANGE";
		if (color == Color.RED)
			return "RED";
		if (color == Color.GREEN)
			return "GREEN";
		if (color == Color.YELLOW)
			return "YELLOW";
		if (color == Color.BLUE)
			return "BLUE";
		else
			return null;
	}
	
	@Override
	public String toString(){
		return String.format("top: %s bottom: %s front: %s right: %s back: %s left: %s", printColor(topFace), printColor(bottomFace), printColor(frontFace), printColor(rightFace), printColor(backFace), printColor(leftFace));
	}
}
