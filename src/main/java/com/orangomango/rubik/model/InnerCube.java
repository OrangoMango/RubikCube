package com.orangomango.rubik.model;

import javafx.scene.shape.Box;
import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.geometry.Point3D;

import java.util.*;

public class InnerCube {
	private static int identity = 0;
	
	public static class ViewCoords {
		private Cube cube;
		private double viewX, viewY, viewZ;
		
		public ViewCoords(Cube cube, double x, double y, double z){
			this.cube = cube;
			this.viewX = x;
			this.viewY = y;
			this.viewZ = z;
		}
		
		public void setup(){
			//System.out.println("Starting from: "+cube.psX+" "+cube.psY+" "+cube.psZ);
			
			double rx = Math.toRadians(0);
			double ry = Math.toRadians(cube.psY*90);
			double rz = Math.toRadians(0);
			
			viewX -= 1;
			viewY -= 1;
			viewZ -= 1;
			
			// Rotate along x axis
			double x1, y1, z1;
			x1 = viewX;
			y1 = viewY*Math.cos(rx)-viewZ*Math.sin(rx);
			z1 = viewY*Math.sin(rx)+viewZ*Math.cos(rx);
			
			// Rotate along y axis
			double x2, y2, z2;
			x2 = x1*Math.cos(ry)+z1*Math.sin(ry);
			y2 = y1;
			z2 = z1*Math.cos(ry)-x1*Math.sin(ry);
			
			// Rotate along z axis
			double x3, y3, z3;
			x3 = x2*Math.cos(rz)-y2*Math.sin(rz);
			y3 = x2*Math.sin(rz)+y2*Math.cos(rz);
			z3 = z2;
			
			this.viewX = x3+1;
			this.viewY = y3+1;
			this.viewZ = z3+1;
		}
		
		public int getX(){
			return (int)Math.round(this.viewX);
		}
		
		public int getY(){
			return (int)Math.round(this.viewY);
		}
		
		public int getZ(){
			return (int)Math.round(this.viewZ);
		}
		
		@Override
		public String toString(){
			return "View coords: "+getX()+" "+getY()+" "+getZ();
		}
	}
	
	public int x, y, z;
	private int startX, startY, startZ;
	public Double relX, relY;
	private Face[] faces = new Face[6];
	private List<Face.Faces> invisibleFaces = new ArrayList<>();
	private Group model;
	private int id;
	public Point3D xAxis = new Point3D(1, 0, 0);
	public Point3D yAxis = new Point3D(0, 1, 0);
	public Point3D zAxis = new Point3D(0, 0, 1);
	private FaceSystem faceSystem = new FaceSystem();
	
	public InnerCube(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.startX = x*100;
		this.startY = y*100;
		this.startZ = z*100;
		this.id = InnerCube.identity++;
		for (int i = 0; i < 6; i++){
			this.faces[i] = new Face(Face.Faces.values()[i]);
		}
	}
	
	public int getStartX(){
		return this.startX;
	}
	
	public int getStartY(){
		return this.startY;
	}
	
	public int getStartZ(){
		return this.startZ;
	}
	
	public FaceSystem getFaceSystem(){
		return this.faceSystem;
	}
	
	public ViewCoords getViewCoords(Cube cube){
		ViewCoords vc = new ViewCoords(cube, this.x, this.y, this.z);
		vc.setup();
		return vc;
	}
	
	public List<Face.Faces> getVisibleFaces(){
		List<Face.Faces> visible = new ArrayList<>();
		for (Face.Faces f : Face.Faces.values()){
			visible.add(f);
		}
		for (Face.Faces f : this.invisibleFaces){
			visible.remove(f);
		}
		return visible;
	}
	
	public List<Color> getVisibleColors(){
		List<Color> colors = new ArrayList<>();
		for (Face.Faces f : getVisibleFaces()){
			colors.add(f.getColor());
		}
		return colors;
	}
	
	public boolean hasColor(Color color){
		return getVisibleColors().contains(color);
	}
	
	public boolean isInCorrectPlace(Cube cube){
		return getFaceSystem().getTopFace().equals(cube.getFaces().getTopFace()) &&
				getFaceSystem().getRightFace().equals(cube.getFaces().getRightFace()) &&
				getFaceSystem().getLeftFace().equals(cube.getFaces().getLeftFace()) &&
				getFaceSystem().getFrontFace().equals(cube.getFaces().getFrontFace()) &&
				getFaceSystem().getBackFace().equals(cube.getFaces().getBackFace()) &&
				getFaceSystem().getBottomFace().equals(cube.getFaces().getBottomFace());
	}
	
	public Group getModel(){
		if (model != null) return model;
		Box[] boxes = new Box[6+12];
		for (int i = 0; i < 6; i++){
			Color color = null;
			switch (this.faces[i].getFace()){
				case TOP:
					if (this.y != 0){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.TOP);
					}
					break;
				case BOTTOM:
					if (this.y != Cube.SIZE-1){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.BOTTOM);
					}
					break;
				case RIGHT:
					if (this.x != Cube.SIZE-1){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.RIGHT);
					}
					break;
				case LEFT:
					if (this.x != 0){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.LEFT);
					}
					break;
				case FRONT:
					if (this.z != 0){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.FRONT);
					}
					break;
				case BACK:
					if (this.z != Cube.SIZE-1){
						color = color.BLACK;
						invisibleFaces.add(Face.Faces.BACK);
					}
					break;
			}
			boxes[i] = this.faces[i].getModel(color);
		}
		for (int i = 0; i < 12; i++){
			boxes[6+i] = getBorders()[i];
		}
		Group group = new Group(boxes);
		group.setTranslateX(this.x*Cube.INNER_CUBE_WIDTH);
		group.setTranslateY(this.y*Cube.INNER_CUBE_WIDTH);
		group.setTranslateZ(this.z*Cube.INNER_CUBE_WIDTH);
		group.getTransforms().add(new Rotate(0));
		this.model = group;
		return group;
	}
	
	private Box[] getBorders(){
		Box[] borders = new Box[12];
		int counter = 0;
		for (int i = 0; i < 4; i++){
			Box border = new Box(Cube.INNER_CUBE_WIDTH, 2, 2);
			int y = -Cube.INNER_CUBE_WIDTH/2;
			if (i >= 2) y = Cube.INNER_CUBE_WIDTH/2;
			border.setTranslateY(y);
			border.setTranslateZ(i % 2 == 0 ? -Cube.INNER_CUBE_WIDTH/2 : Cube.INNER_CUBE_WIDTH/2);
			border.setMaterial(new PhongMaterial(Color.BLACK));
			borders[counter] = border;
			counter++;
		}
		for (int i = 0; i < 4; i++){
			Box border = new Box(2, Cube.INNER_CUBE_WIDTH, 2);
			int z = -Cube.INNER_CUBE_WIDTH/2;
			if (i >= 2) z = Cube.INNER_CUBE_WIDTH/2;
			border.setTranslateX(i % 2 == 0 ? -Cube.INNER_CUBE_WIDTH/2 : Cube.INNER_CUBE_WIDTH/2);
			border.setTranslateZ(z);
			border.setMaterial(new PhongMaterial(Color.BLACK));
			borders[counter] = border;
			counter++;
		}
		for (int i = 0; i < 4; i++){
			Box border = new Box(2, 2, Cube.INNER_CUBE_WIDTH);
			int x = -Cube.INNER_CUBE_WIDTH/2;
			if (i >= 2) x = Cube.INNER_CUBE_WIDTH/2;
			border.setTranslateX(x);
			border.setTranslateY(i % 2 == 0 ? -Cube.INNER_CUBE_WIDTH/2 : Cube.INNER_CUBE_WIDTH/2);
			border.setMaterial(new PhongMaterial(Color.BLACK));
			borders[counter] = border;
			counter++;
		}
		return borders;
	}
	
	public int getID(){
		return this.id;
	}
	
	@Override
	public String toString(){
		return "Cube "+this.id+" at "+String.format("%s %s %s", this.x, this.y, this.z)+" with "+getVisibleFaces().size()+" faces";
	}
}
