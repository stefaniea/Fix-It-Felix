import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ralph extends GameObj {
	public static final int SIZE = 20;
	public static final int INIT_X = 50;
	public static final int INIT_Y = 20;
	public static final int INIT_VEL_X = 3;
	public static final int INIT_VEL_Y = 0;

	public boolean smash;
	private Brick[] bricks;
	int timer;
	
	private static BufferedImage smashing, standing;

	public Ralph(int court_width, int court_height, Brick[] bricks) {
		super(INIT_VEL_X, INIT_VEL_Y, INIT_X, INIT_Y, 100, 100, court_width,
				court_height);
		//initialize bricks and timer
		this.bricks = bricks;
		timer = 50; //start the timer at 50.  When it reaches 80, ralph will 
		//smash.  After 20 more ticks he will stop smashing.  This simulates
		//the very rough "animation", as well as times when bricks will fall
		
		//read in the images of ralph standing and punching the ground
		try {
			if (standing == null) {
				standing = ImageIO.read(new File("ralph.png"));
			}
			if (smashing == null) {
				smashing = ImageIO.read(new File("Ralph_smash.png"));
			}

		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	// arbitrarily moves from side to side
	// public void move() {}

	// changes the state of bricks
	public void smash() {
		smash = true; //change ralph's state to smashing so that the draw() will
					 //animate him
		StdAudio.play("smashsound.wav"); //sound effect for smashing
		
		//make the bricks start falling
		bricks[0].changeVelocity(this);
		bricks[1].changeVelocity(this);
		bricks[2].changeVelocity(this);

	}

	// clip so ralph only moves back and forth on top of building (not floating
	//in the sky).
	@Override
	public Direction hitWall() {
		if (pos_x + v_x < 100)
			return Direction.LEFT;
		else if (pos_x + v_x > max_x - 100)
			return Direction.RIGHT;
		if (pos_y + v_y < 0)
			return Direction.UP;
		else if (pos_y + v_y > max_y)
			return Direction.DOWN;
		else
			return null;
	}
	
	//get position
	public int getPosX() {
		return pos_x;
	}
	//get position
	public int getPosY() {
		return pos_y;
	}

	//draw checks ralph's state and draws him either standing or smashing
	//draw is called every tick, so this is where the timer is
	public void draw(Graphics g) {
		timer++; //increment timer
		if (timer == 80) {
			smash(); //smash
			timer = 0; //reset timer
		}
		if (smash) {
			if (timer == 20)
				smash = false;
			g.drawImage(smashing, pos_x, pos_y + 10, width, height, null);
		} 
		else {
			g.drawImage(standing, pos_x, pos_y + 10, width, height, null);
		}
	}
}