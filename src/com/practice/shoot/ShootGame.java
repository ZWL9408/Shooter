package com.practice.shoot;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class ShootGame extends JPanel{

	public static final int WIDTH = 400;	//面板宽
	public static final int HEIGHT = 654;	//面板高
	
	/** 游戏的当前状态: START RUNNING PAUSE GAME OVER */
	private int state;
	public  static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	
	private int score = 0;	//得分
	private Timer timer;	//定时器
	private int intervel = 1000 / 100;	//时间间隔（毫秒）
	
	public static BufferedImage background;
	public static BufferedImage start;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	
	private FlyingObject[] flyings = {};	//敌机数组
	private Bullet[] bullets = {};	//子弹数组
	private Hero hero = new Hero();	//英雄机
	
	public ShootGame() {
//		初始化一直蜜蜂一架飞机
//		flyings = new FlyingObject[2];
//		flyings[0] = new Airplane();
//		flyings[1] = new Bee();
//		初始化一颗子弹
//		bullets = new Bullet[1];
//		bullets[0] = new Bullet(200,350);
	}
	static {	//静态代码
		try{
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);	//画背景图
		paintHero(g);	//画英雄机
		paintBullets(g);	//画子弹
		paintFlyingObjects(g);	//画飞行物
		paintScore(g);	
		paintState(g);	//画游戏状态
	}
	
	/** 画英雄机 */
	public void paintHero(Graphics g){
		g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
	}
	
	/** 画子弹 */
	public void paintBullets(Graphics g){
		for(int i = 0; i < bullets.length; i++) {
			FlyingObject b = bullets[i];
			g.drawImage(b.getImage(), b.getX(), b.getY(), null);
		}
	}
	
	/** 画飞行物 */
	public void paintFlyingObjects(Graphics g){
		for(int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			g.drawImage(f.getImage(), f.getX(), f.getY(), null);
		}
	}
	
	/** 画分数 */
	public void paintScore(Graphics g){
		int x = 10;
		int y = 25;
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
		g.setColor(new Color(0x3A3B3B));
		g.setFont(font);
		g.drawString("SCORE:" + score, x, y);
		y += 20;
		g.drawString("LIFE:" + hero.getLife(), x, y);
	}
	
	/** 画游戏状态 */
	public void paintState(Graphics g){
		switch (state){
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
		}
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");
		ShootGame game = new ShootGame();	//面板大小
		frame.add(game);	//将面板添加到JFrame中
		frame.setSize(WIDTH, HEIGHT);	//大小
		frame.setAlwaysOnTop(true);	//其总在最上
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//默认关闭操作
		frame.setLocationRelativeTo(null);	//设置窗体初始位置
		frame.setVisible(true);	//尽快调用paint
		game.action();	//启动执行
	}
	
	public void action(){	//启动执行代码
		//鼠标监听事件
		MouseAdapter l = new MouseAdapter(){
			public void mouseMoved(MouseEvent e){	//鼠标移动
				if (state == RUNNING){	//运行时移动英雄机
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x, y);
				}
			}
			public void mouseEntered(MouseEvent e){	//鼠标进入
				if (state == PAUSE){	//暂停时运行
					state = RUNNING;
				}
			}
			public void mouseExited(MouseEvent e){	//鼠标退出
				if(state != GAME_OVER){	
					state = PAUSE;	//游戏未结束，则设置其为暂停
				}
			}
			public void mouseClicked(MouseEvent e){	//鼠标点击
				switch (state){
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:	//游戏结束，清理现场
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					hero = new Hero();
					score = 0;
					state = START;
					break;
				}
			}
		};
		this.addMouseListener(l);	//处理鼠标点击操作
		this.addMouseMotionListener(l);	//处理鼠标滑动操作
		
		timer = new Timer();	//主流程控制
		timer.schedule(new TimerTask(){
			public void run(){
				if (state == RUNNING){
					enterAction();	//飞行物入场
					stepAction();	//走一步
					shootAction();	//射击
					bangAction();	//子弹打飞行物
					outOfBoundsAction();	//删除越界飞行物
					checkGameOverAction();	//检查游戏结束
				}
				repaint();	//重绘，调用paint（）方法
			}
		},intervel,intervel);
	}
	
	/** 
	 *随机生成飞行物
	 *@return 飞行物对象 
	 */
	public static FlyingObject nextOne() {
		Random random = new Random();
		int type = random.nextInt(20);
		if (type == 0){
			return new Bee();
		}else {
			return new Airplane();
		}
	}
	
	int flyEnteredIndex = 0; //飞行物入场计数
	/** 飞行物入场 */
	public void enterAction() {
		flyEnteredIndex++;
		if (flyEnteredIndex % 40 == 0) {
			FlyingObject obj = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length + 1);
			flyings[flyings.length - 1] = obj;
		}
	}
	
	public void stepAction() {
		/** 飞行物走一步 */
		for(int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			f.step();
		}
		
		/** 子弹走一步 */
		for(int i = 0;i < bullets.length; i++) {
			Bullet b = bullets[i];
			b.step();
		}
		hero.step();
	}
	
	int shootIndex = 0;	//射击计数
	
	/** 射击 */
	public void shootAction(){
		shootIndex++;
		if (shootIndex % 30 == 0) {
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
			
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
		}
	}
	
	/** 子弹与飞行物碰撞检测 */
	public void bangAction() {
		for ( int i = 0; i < bullets.length; i++){
			Bullet b =bullets[i];
			bang(b);
		}
	}
	
	/** 子弹和飞行物之间的碰撞检测 */
	public void bang(Bullet bullet){
		int index = -1;	//击中的飞行物索引
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject obj = flyings[i];
			if (obj.shootBy(bullet)){	//判断是否击中
				index = i;	//记录被击中的飞行物的索引
				break;
			}
		}
		if (index != -1){	//有击中飞行物
			FlyingObject one = flyings[index];	//记录被击中的飞行物
			FlyingObject temp = flyings[index];	//被击中的飞行物与最后一个飞行物交换
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = temp;
			flyings = Arrays.copyOf(flyings, flyings.length - 1);	//删除最后一个飞行物（即被击中）
			
			if(one instanceof Enemy) {	//检查类型，是敌人，则加分
				Enemy e = (Enemy) one;	//强制类型转换
				score += e.getScore();	//加分
			}
			if(one instanceof Award) {	//若为奖励，设置奖励
				Award a = (Award) one;
				int type = a.getType();	//获取奖励类型
				switch (type){
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();	//设置双倍火力
					break;
				case Award.LIFE:
					hero.addLife();	//设置加命
					break;
				}
			}	
		}
	}
	
	/** 删除越界飞行物及子弹 */
	public void outOfBoundsAction() {
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];	//活着的飞行物
		for(int i = 0; i < flyings.length; i++){
			FlyingObject f = flyings[i];
			if(!f.outOfBounds()){
				flyingLives[index++] = f;	//不越界的留着
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);	//将不越界的飞行物都留着
		
		index = 0;	//重置为0
		Bullet[] bulletLives = new Bullet[bullets.length];
		for(int i = 0; i < bullets.length; i++){
			Bullet b = bullets[i];
			if(!b.outOfBounds()){
				bulletLives[index++] = b;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index);	//将不越界的子弹留着
	}
	
	/** 检查游戏结束 */
	public void checkGameOverAction() {
		if (isGameOver()){
			state = GAME_OVER;	//改变状态
		}
	}
	
	/** 检查游戏是否结束 */
	public boolean isGameOver(){
		for(int i = 0; i <flyings.length; i++){
			int index = -1;
			FlyingObject obj = flyings[i];
			if(hero.hit(obj)){	//检查英雄机与飞行物是否碰撞
				hero.substractLife();
				hero.setDoubleFire(0);
				index = i;
			}
			if (index != -1){
				FlyingObject t = flyings[index];
				flyings[index] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = t;
				flyings = Arrays.copyOf(flyings, flyings.length - 1);
			}
		}
		return hero.getLife() <= 0;
	}
}
