import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Window {

	public static final int height = 25;
	public static final int width = 25;

	boolean pie; // whether or not the window has a pie in it
	int broken_state; // 0 is not broken, 1 is half broken, 2 is fully broken

	// positions
	int px;
	int py;

	// connections to other windows
	Window up;
	Window down;
	Window left;
	Window right;

	public Window(int broken_state, boolean pie, int px, int py) {

		this.broken_state = broken_state;
		this.pie = pie;
		this.px = px;
		this.py = py;

	}

	// Connects a window to the current window depending on which direction is passed in.
	// If a window is passed in, the method connects those two windows.
	// If no window is passed in, the method creates a window in the direction specified
	// and connects the two windows
	public Window addWindow(Direction d, Window w) {
		//randomly assign a broken state to a window
		int broke = (int) (Math.random() * 2 + .5); 
		
		//if no window was passed in, check the direction, create a window, connect windows
		if (w == null) {
			if (d.equals(Direction.UP)) {
				up = new Window(broke, false, px, py - 110);
				up.down = this;
				return up;
			}
			if (d.equals(Direction.DOWN)) {
				down = new Window(broke, false, px, py + 110);
				down.up = this;
				return down;
			}
			if (d.equals(Direction.LEFT)) {
				left = new Window(broke, false, px - 50, py);
				left.right = this;
				return left;
			}
			if (d.equals(Direction.RIGHT)) {
				right = new Window(broke, false, px + 53, py);
				right.left = this;
				return right;
			}
			//if a window was passed in, connect the two windows
		} else {
			if (d.equals(Direction.UP)) {
				up = w;
				w.down = this;
				return w;
			}
			if (d.equals(Direction.DOWN)) {
				down = w;
				w.up = this;
				return w;
			}
			if (d.equals(Direction.LEFT)) {
				left = w;
				w.right = this;
				return w;
			}
			if (d.equals(Direction.RIGHT)) {
				right = w;
				w.left = this;
				return w;
			}
		}
		return null;
	}

	// Checks if a window exists in the direction specified. Return true if it
	// does.
	// Use this when moving Felix to see if there is a window for him to jump
	// to.
	public boolean hasWindow(Direction d) {
		if ((d.equals(Direction.UP)) && (this.up != null))
			return true;
		if ((d.equals(Direction.DOWN)) && (this.down != null))
			return true;
		if ((d.equals(Direction.LEFT)) && (this.left != null))
			return true;
		if ((d.equals(Direction.RIGHT)) && (this.right != null))
			return true;
		return false;
	}

	// Everytime fixWindow is called the window's state will decrease by 1
	// (unless it is already 0)
	// Returns true is the window was fixed
	public boolean fixWindow() {
		if (broken_state == 0)
			return false;
		else {
			broken_state--;
			return true;
		}
	}

	//get position
	public int getX() {
		return px;

	}
	//get position
	public int getY() {
		return py;
	}
}