package com.orangomango.rubik.model;

import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

public class Face {
	private Faces face;
	
	public static enum Faces {
		TOP(Color.WHITE),
		BOTTOM(Color.YELLOW),
		RIGHT(Color.RED),
		LEFT(Color.ORANGE),
		FRONT(Color.GREEN),
		BACK(Color.BLUE);
		
		private Color color;
		
		private Faces(Color color){
			this.color = color;
		}
		
		public Color getColor(){
			return this.color;
		}
	}
	
	public Face(Faces face){
		this.face = face;
	}
	
	public Faces getFace(){
		return this.face;
	}
	
	public Box getModel(Color color){
		Box box = null;
		switch (this.face){
			case TOP:
				box = new Box(Cube.INNER_CUBE_WIDTH, 0, Cube.INNER_CUBE_WIDTH);
				box.setTranslateY(-Cube.INNER_CUBE_WIDTH/2);
				break;
			case BOTTOM:
				box = new Box(Cube.INNER_CUBE_WIDTH, 0, Cube.INNER_CUBE_WIDTH);
				box.setTranslateY(Cube.INNER_CUBE_WIDTH/2);
				break;
			case RIGHT:
				box = new Box(0, Cube.INNER_CUBE_WIDTH, Cube.INNER_CUBE_WIDTH);
				box.setTranslateX(Cube.INNER_CUBE_WIDTH/2);
				break;
			case LEFT:
				box = new Box(0, Cube.INNER_CUBE_WIDTH, Cube.INNER_CUBE_WIDTH);
				box.setTranslateX(-Cube.INNER_CUBE_WIDTH/2);
				break;
			case FRONT:
				box = new Box(Cube.INNER_CUBE_WIDTH, Cube.INNER_CUBE_WIDTH, 0);
				box.setTranslateZ(-Cube.INNER_CUBE_WIDTH/2);
				break;
			case BACK:
				box = new Box(Cube.INNER_CUBE_WIDTH, Cube.INNER_CUBE_WIDTH, 0);
				box.setTranslateZ(Cube.INNER_CUBE_WIDTH/2);
				break;
			default:
				return box;
		}
		box.setMaterial(new PhongMaterial(color == null ? this.face.getColor() : color));
		return box;
	}
}
