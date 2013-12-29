import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

//level two is an extension of level one.  It is the same except there are no special
//windows, there are pies, it's name is "two" instead of "one", and it's windows
//are at slightly different locations.  There is also a little ledge making it impossible
//for felix to jump from one of the windows to another.

public class LevelThree extends LevelTwo {
	private BufferedImage pie;

	public LevelThree(BufferedImage background) {
		super(background);

		try {
			if (background == null) {
				background = ImageIO.read(new File("background3.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		level = "three";
		windowlist = new Window[3][5];
		Window start = new Window(2, false, 335, 380);
		windowlist[0][0] = start;
		makeWindows();

	}

	@Override
	public String toString() {
		return level;
	}

	// randomly puts pies in windows (using math.random, with a 1/10000
	// probability)
	@Override
	public void putPies(Graphics g) {
		for (int i = 0; i < windowlist.length; i++) {
			for (int j = 0; j < windowlist.length; j++) {
				int random = (int) (Math.random() * 10000);
				if (random == 1) {
					windowlist[i][j].pie = true;
				}
			}
		}
	}

	@Override
	public void attachWindows() {
		for (int i = 0; i < windowlist.length - 1; i++) {
			for (int j = 0; j < windowlist[0].length; j++) {
				if (windowlist[i + 1][j] == null || windowlist[i][j] == null)
					continue;

				// something blocking the way from jumping here
				if ((i == 1 && j == 2) || (i == 0 && j == 3))
					continue;

				windowlist[i + 1][j] = windowlist[i][j].addWindow(Direction.UP,
						windowlist[i + 1][j]);
			}
		}
	}
}
