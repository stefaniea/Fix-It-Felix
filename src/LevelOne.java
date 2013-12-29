import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//Level one creates a 2D array of windows. It also draws windows based on how broken they
//are and if there is a pie in it (though there are no pies in level one.)

public class LevelOne {
	Window[][] windowlist;
	int height = 55;
	int width = 25;
	String level;
	
	//Images for each window state
	private BufferedImage fixed, broken1, broken2, background, pie;
	
	//Create all the windows and add them to the hashmap
	public LevelOne(BufferedImage background){
		//Read in images
		this.background = background;
		level = "one";
		try {
			if (fixed == null) {
				fixed = ImageIO.read(new File("fixed_window.png"));
			}
			if (pie == null) {
				pie = ImageIO.read(new File("pie.png"));
			}
			if (broken1 == null) {
				broken1 = ImageIO.read(new File("broken1_window.png"));
			}
			if (broken2 == null) {
				broken2 = ImageIO.read(new File("broken2_window.png"));
			}
		}

		catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}
		windowlist = new Window[3][5]; //create a window array
		Window start = new Window(2, false, 335, 360); //create the first
		windowlist[0][0] = start; //set the initial window to 0,0
		makeWindows();
	}
	
	public Window getStart() {
		return windowlist[0][0]; //return starting window
	}
	
	//attach windows up and down
	public void attachWindows() {
		for(int i = 0; i < windowlist.length-1; i++) {
			for (int j = 0; j < windowlist[0].length; j++) {
				if (windowlist[i+1][j] == null || windowlist[i][j] == null) continue;
				windowlist[i+1][j] = windowlist[i][j].addWindow(Direction.UP, windowlist[i+1][j]);
			}
		}
	}
	public void makeWindows() {
		//first, makeWindows() creates one window in every level to account for an uneven 
		//placing of windows in the y direction.  
		//Also makes it easier because we only have to make windows in one direction
		windowlist[1][0] = new Window(1, false, 335, 265);
		windowlist[2][0] = new Window(0, false, 335, 155);
		
		//loop creates windows to the left of the 3 initial windows
		//and attaches them left to right
	for(int i = 0; i < windowlist.length; i++) {
			for (int j = 1; j < windowlist[0].length; j++) {
				windowlist[i][j] = windowlist[i][j-1].addWindow(Direction.LEFT, null);
			}
	}
	
	attachWindows(); //attach windows together
			
//0,2 and 1,2 are special windows in level one that should not have to be fixed; make sure
//these two windows are not broken.
	if (level.equals("one")) {
		windowlist[0][2].broken_state = 0;
		windowlist[1][2].broken_state = 0;
	}
	}
			
	public void putPies(Graphics g) {
		//This puts pies in some of the windows, but there are no pies in level 1
	}
	
	//checks if all the windows in level one are fixed.  This is used
	//when checking to see if game has ended or if player should move to level 2
	public boolean fixedYet() {
		boolean fixed = true;
		for (int i=0; i < windowlist.length; i++) {
			for (int j = 0; j < windowlist[0].length; j++) {
				if(windowlist[i][j] == null) continue; //don't count any null windows
			if (!(windowlist[i][j].broken_state == 0)) fixed = false;
		}
}
		if (fixed == true) return true;
		else return false;
	}
	
	public String toString() {
		return level;
	}
	
	//draw all the windows
	public void draw(Graphics g) {
		//Draw Building
		g.drawImage(background, -10, 125, 525, 325, null);
		
		//Draw windows
		for (int i = 0; i < windowlist.length; i++) {
			for (int j = 0; j < windowlist[0].length; j++) {
				if (windowlist[i][j] == null) continue;
				else {
					Window curr = windowlist[i][j];
					
					//if the window is a special one from level 1, don't draw it.
					 if((level.equals("one")) && 
					 (curr == windowlist[0][2] || 
					 curr == windowlist[1][2])) continue;

					if (curr.broken_state == 2) {
						g.drawImage(broken2, curr.px, curr.py, width, height,
								null);
					} else if (curr.broken_state == 1) {
						g.drawImage(broken1, curr.px, curr.py, width, height,
								null);
					} else {
						g.drawImage(fixed, curr.px, curr.py, width, height,
								null);
					}
					if (curr.pie) g.drawImage(pie, windowlist[i][j].px, windowlist[i][j].py+20,
							25, 25, null);
				}
			}
		}
		putPies(g); //put pies into the windows
	}
}
