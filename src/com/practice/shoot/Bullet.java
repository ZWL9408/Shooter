package com.practice.shoot;
/**
 * �ӵ��࣬�Ƿ�����
 */
public class Bullet extends FlyingObject{
private int speed = 3; //�ƶ����ٶ�
public Bullet(int x,int y){
	this.x = x;
	this.y = y;
	this.image = ShootGame.bullet;
}
public void step(){
	y -= speed;
}
public boolean outOfBounds(){
	return y < -height;
}
}
