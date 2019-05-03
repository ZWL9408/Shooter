package com.practice.shoot;
import com.practice.shoot.ShootGame;
/**
 * 敌飞机：是飞行物，也是敌人
 */
public class Airplane extends FlyingObject implements Enemy{
	private int speed = 2;
	
	/**初始化数据*/
	public Airplane(){
		this.image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		x = (int)(Math.random()*(ShootGame.WIDTH - width));
//		y = 100;
//		x = 100;
	}
	public int getScore(){
		return 5;
	}
	public void step(){
		y += speed;
	}
	public boolean outOfBounds(){
		return y>ShootGame.HEIGHT;
	}
}
