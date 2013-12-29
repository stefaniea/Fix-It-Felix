/**
 * CIS 120 HW10
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * GameCourt
 * 
 * This class holds the primary game logic of how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

	private Felix felix; // player's character controlled by keyboard
	private Ralph ralph;
	private Brick[] bricks; // brick array - to be thrown by ralph
	private BufferedImage background1, background2, background3, start,
			backgroundtop, gameover, fixedit; // images

	private LevelOne level;

	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)
	private boolean startscreen = true; // whether to draw instructions or not

	// Game constants
	public static final int COURT_WIDTH = 500;
	public static final int COURT_HEIGHT = 450;
	public static final int SQUARE_VELOCITY = 4;
	// Update interval for timer in milliseconds
	public static final int INTERVAL = 35;

	public GameCourt(JLabel status) {
		// Read in all images
		try {
			if (start == null) {
				start = ImageIO.read(new File("startscreen.png"));
			}
			if (background1 == null) {
				background1 = ImageIO.read(new File("background.png"));
			}
			if (background2 == null) {
				background2 = ImageIO.read(new File("background2.png"));
			}
			if (background3 == null) {
				background3 = ImageIO.read(new File("background3.png"));
			}
			if (backgroundtop == null) {
				backgroundtop = ImageIO.read(new File("background_top.png"));
			}
			if (gameover == null) {
				gameover = ImageIO.read(new File("gameover.png"));
			}
			if (fixedit == null) {
				fixedit = ImageIO.read(new File("fixedit.png"));
			}
		} catch (IOException e) {
			System.out.println("Internal Error:" + e.getMessage());
		}

		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// The timer is an object which triggers an action periodically
		// with the given INTERVAL. One registers an ActionListener with
		// this timer, whose actionPerformed() method will be called
		// each time the timer triggers. We define a helper method
		// called tick() that actually does everything that should
		// be done in a single timestep.
		Timer timer = new Timer(INTERVAL, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		timer.start(); // MAKE SURE TO START THE TIMER!

		StdAudio.loop("bgmusic.wav"); // play some background music

		// Enable keyboard focus on the court area
		// When this component has the keyboard focus, key
		// events will be handled by its key listener.
		setFocusable(true);

		// this key listener allows felix to move to an adjacent window.
		// if the spacebar is pressed, he fixes a window
		// also, the spacebar also takes the player from the start screen to the
		// actual game.
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (playing) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT)
						felix.jump(Direction.LEFT);
					else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
						felix.jump(Direction.RIGHT);
					else if (e.getKeyCode() == KeyEvent.VK_DOWN)
						felix.jump(Direction.DOWN);
					else if (e.getKeyCode() == KeyEvent.VK_UP)
						felix.jump(Direction.UP);
					else if (e.getKeyCode() == KeyEvent.VK_SPACE)
						felix.fix(); // fix a window
					startscreen = false; // if the user was on the startscreen
					// then get out of the start screen and start the game
				}
			}

			// after felix moves or fixes, he should return to a standing
			// position
			public void keyReleased(KeyEvent e) {
				felix.toStanding();
			}
		});

		this.status = status;
	}

	/**
	 * (Re-)set the state of the game to its initial state.
	 */

	public void reset() {
		startscreen = true; // go to the startscreen

		// Create level
		level = new LevelOne(background1);

		bricks = new Brick[3];
		felix = new Felix(COURT_WIDTH, COURT_HEIGHT, level.getStart(), 3);
		ralph = new Ralph(COURT_WIDTH, COURT_HEIGHT, bricks);

		bricks[0] = new Brick(COURT_WIDTH, COURT_HEIGHT, -40, ralph);
		bricks[1] = new Brick(COURT_WIDTH, COURT_HEIGHT, 0, ralph);
		bricks[2] = new Brick(COURT_WIDTH, COURT_HEIGHT, 40, ralph);

		playing = true;
		status.setText("Running...");

		// Make sure that this component has the keyboard focus
		requestFocusInWindow();
	}

	/**
	 * This method is called every time the timer defined in the constructor
	 * triggers.
	 */
	void tick() {
		if (startscreen) { // nothing should happen at the start screen other
							// than
			repaint(); // painting the instructions
		} else if (playing) {

			// move ralph and bricks
			ralph.move();
			for (int i = 0; i < bricks.length; i++) {
				bricks[i].move();
			}

			// make ralph bounce off walls
			ralph.bounce(ralph.hitWall());

			// check if Felix should lose a life or if game should end
			// if felix does not have a pie, then he should lose a life when a
			// brick hits him
			if (!felix.hasPie()) { // only if felix is not invincible at the
									// moment
				for (int i = 0; i < bricks.length; i++) {
					if (felix.intersects(bricks[i])) {
						StdAudio.play("loselifesound.wav"); // sound effect for
															// losing a life
						if (felix.getLives() == 1) { // if that was his last
														// life
							felix.loseLife(); // lives are now 0
							playing = false; // end the game
							status.setText("Game Over");
						} else {
							felix.loseLife();
							bricks[i].reset(); // bring the brick back up to
												// ralph
						}
					}
				}
			}

			// check if all windows are fixed and either end the game
			// or go to the next level
			// if you're on level 3 and all windows are fixed, you win
			if (level.fixedYet() && (level.toString().equals("three"))) {
				playing = false;
				status.setText("You fixed it!");
				// if you're on level two and all windows are fixed, move to
				// level 3
			} else if (level.toString().equals("two") && level.fixedYet()) {
				// change level and felix's location
				level = new LevelThree(background3);
				felix.changeLocation(level.getStart());
			}
			// if you're on level one and all windows are fixed, move to level 2
			else if (level.toString().equals("one") && level.fixedYet()) {
				level = new LevelTwo(background2);
				felix.changeLocation(level.getStart());

			}
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white); // set color to white

		// if the player is on the start screen, draw the instructions
		// the spacebar will start the game
		if (startscreen) {
			g.drawImage(start, 0, 0, COURT_WIDTH, COURT_HEIGHT, null);
			g.drawString("Press Spacebar to Start!", 175, 120);

			// if the game is over and it's because the player died,
			// draw the game over screen
		} else if (!playing && felix.getLives() == 0) {
			g.drawImage(gameover, 150, 150, 250, 100, null);
			g.drawString("GAME OVER", 225, 200);
		} else if (!playing) {
			g.drawImage(fixedit, 140, 150, 250, 75, null);
		}

		else {
			// set font and background color
			super.setBackground(Color.BLACK);

			// Draw the building top
			g.drawImage(backgroundtop, 25, 0, COURT_WIDTH - 80, 125, null);

			// Draw the level with all the windows
			level.draw(g);

			// Draw felix, bricks, and ralph
			felix.draw(g);
			for (int i = 0; i < bricks.length; i++) {
				bricks[i].draw(g);
			}
			ralph.draw(g);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURT_WIDTH, COURT_HEIGHT);
	}
}
