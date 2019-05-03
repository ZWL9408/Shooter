package com.practice.shoot;
import java.util.Random;
/**蜜蜂*/
public class Bee extends FlyingObject implements Award {

	private int xSpeed = 1;	//x坐标移动速度
	private int ySpeed = 1; //y坐标移动速度
	private int awardType; //奖励类型
	public Bee(){
		this.image = ShootGame.bee;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH - width);
		// x = 100;
		// y = 100;
		awardType = rand.nextInt(2);
	}
	public int getType(){
		return awardType;
	}
	public void step(){	//可斜飞
		x += xSpeed;
		y += ySpeed;
		if(x > ShootGame.WIDTH - width){
			xSpeed = 1;
		}
	}
	public boolean outOfBounds(){
		return y > ShootGame.HEIGHT;
	}
}
