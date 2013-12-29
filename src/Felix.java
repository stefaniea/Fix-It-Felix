import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Felix extends GameObj {

	public static final int SIZE = 50;
	// public static final int INIT_X = 250;
	// public static final int INIT_Y = 250;
	public static final int INIT_VEL_X = 0;
	public static final int INIT_VEL_Y = 0;

	private Window location;
	private int lives, score, timer;
	private boolean jump, fix, pie;


	private static BufferedImage standing, jumping, fixing, lives1,
	lives2, lives3, felixpie;

	public Felix(int court_width, int court_height, Window location, int lives) {
		super(INIT_VEL_X, INIT_VEL_Y, location.getX() - 25,
				location.getY() + 10, SIZE, SIZE, court_width, court_height);

		// initialize variables
		this.location = location;
		this.lives = lives;
		score = 0;
		jump = false;
		fix = false;
		pos_x = location.getX() - 25; //-25 and +10 account for the images not lining up
		pos_y = location.getY() + 10;

		// Read in all of the images for felix
		try {
			if (standing == null) {
				standing = ImageIO.read(new File("felix.png"));
			}
			if (jumping == null) {
				jumping = ImageIO.read(new File("felix_jump.png"));
			}
			if (fixing == null) {
				fixing = ImageIO.read(new File("felix_fixing.png"));
			}
			if (lives1 == null) {
				lives1 = ImageIO.read(new File("one_life.png"));
			}
			if (lives2 == null) {
				lives2 = ImageIO.read(new File("two_life.png"));
			}
			if (lives3 == null) {
				lives3 = ImageIO.read(new File("three_lives.png"));
			}
			if (felixpie == null) {
				felixpie = ImageIO.read(new File("felixwhite.png"));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}

	// checks if there is a window to move to and changes position
	// Also changes state so that draw will make felix jump
	public void jump(Direction d) {
		jump = true;
		StdAudio.play("bwoop.wav"); //sound effect for jumping
		if (location.hasWindow(d)) {
			if (d.equals(Direction.UP)) {
				pos_y = location.up.getY();
				location = location.up;
			}
			if (d.equals(Direction.DOWN)) {
				pos_x = location.down.getX() - 25;
				pos_y = location.down.getY();
				location = location.down;
			}
			if (d.equals(Direction.LEFT)) {
				pos_x = location.left.getX() - 25;
				pos_y = location.left.getY();
				location = location.left;
			}
			if (d.equals(Direction.RIGHT)) {
				pos_x = location.right.getX() - 25;
				pos_y = location.right.getY();
				location = location.right;
			}
			if (location.pie) {
				location.pie = false;
				this.pie = true;
				StdAudio.play("piesound.wav");
			}
		}
	}

	// should not be able to use move with felix
	@Override
	public void move() {
		throw new UnsupportedOperationException();
	}
	
	//change felix's location.  Call this when changing levels
	public void changeLocation(Window w) {
		this.location = w;
		pos_x = w.px-25;
		pos_y = w.py+10;
	}

	// if the window is broken, fixes it by 1. If successful, gains 300 points
	// Also changes state so that draw will make felix "fix"
	public void fix() {
		fix = true;
		StdAudio.play("fixsound.wav");
		if (location.fixWindow())
			score += 300;
		// draw and change state back?
	}

	// decreases lives by 1
	public void loseLife() {
		lives--;
	}

	// gets the number of lives
	public int getLives() {
		return lives;
	}

	public boolean hasPie() {
		return pie;
	}

	//resets felix to a standing position
	public void toStanding() {
		jump = false;
		fix = false;
	}

	// checks Felix's state. Draw him normally if not jumping or fixing.
	// Draw him jumping or fixing if either of those states are true.
	// Also draws Felix's remaining lives in the corner of the screen
	//Since draw is called at every tick, this is where the pie (invincibility) timer is
	public void draw(Graphics g) {
		g.drawString("Score: \n" + score, 12, 12); //draw current score
		
		if (pie) {
			timer++; //increment timer
			if (timer % 5 == 0) //every 5 calls, beep so the player knows they are invincible
				StdAudio.play("beep.wav"); 
		}
		if (timer == 100) { //after 100 calls, the player is no longer invincible
			pie = false;
			timer = 0; //reset timer
			StdAudio.play("powerdown.wav"); //sound effect for losing invincibility
		}
		if (lives == 3) {
			g.drawImage(lives3, 400, 0, width + 30, height - 15, null);
		}
		if (lives == 2) {
			g.drawImage(lives2, 400, 0, width, height - 15, null);
		}
		if (lives == 1) {
			g.drawImage(lives1, 400, 0, width - 15, height - 15, null);
		}
		if (lives == 0) {
		}

		if (fix) {
			g.drawImage(fixing, pos_x, pos_y, width, height + 10, null);
		} else if (jump) {
			g.drawImage(jumping, pos_x, pos_y, width - 10, height + 10, null);
		} 
		else if(pie) {
			g.drawImage(felixpie, pos_x, pos_y, width -20, height + 10, null);
		}
			else {
		
			g.drawImage(standing, pos_x, pos_y, width - 20, height + 10, null);
		}
	}
}