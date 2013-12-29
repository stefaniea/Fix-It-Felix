import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Brick extends GameObj {
	
	public static final int SIZE = 20;
	public static final int INIT_VEL_X = 3;
	public static final int INIT_VEL_Y = 0;

	boolean falling;
	private static BufferedImage brick;
	private int offset;
	Ralph r;
	
	public Brick(int court_width, int court_height, int offset, Ralph r) {
		super(INIT_VEL_X, INIT_VEL_Y, r.getPosX(), r.getPosY()+70, 25, 10, court_width, court_height);
		this.offset = offset;
		this.r = r;
		
		try {
			if (brick == null) {
				brick = ImageIO.read(new File("brick.png"));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
	}
	
	public void changeVelocity(Ralph r) {
		if (falling) return; //if the brick is already falling, this should do nothing
		//if not, make the brick fall at a random velocity with some offset position
		v_y = (int) (2*Math.random() + 5.2); 
		pos_x+=offset;
		falling = true;
	}
	
	//if a brick is falling, it should move down.  If not, it should move left to right
	//with ralph.
	public void move() {
		if (falling) {
			v_x = 0;
			pos_x += v_x;
			pos_y += v_y;

			clip();
		}
		else {
			pos_x += v_x;
			pos_y += v_y;
			clip();
		}
	}
	
	//call this when a brick hits felix or when it's out of the screen.  
	//It move the brick back up to where Ralph is
	//and makes it move with Ralph.
	public void reset(){
		pos_y = r.getPosY()+90;
		pos_x = r.getPosX()+offset;
		v_y = 0;
		falling = false;
	}
	
	//clip resets the brick when it hits the end of the screen.
	public void clip(){
		if (pos_y >= max_y) {
			reset();
		}
		falling = false; //it should no longer be falling.
	}
	//depending on state, draws bricks
	public void draw(Graphics g) {
		g.drawImage(brick, pos_x, pos_y, width, height+5, null); 
	}
}

