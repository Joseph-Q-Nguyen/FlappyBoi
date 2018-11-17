import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FlappyBoi extends JPanel implements KeyListener {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	public static final int PIPE_VERTICAL_SPACE = 150;
	public static final int PIPE_WIDTH = 100;
	public static final int LAND_HEIGHT = 100;
	public static final int GRASS_HEIGHT = 20;
	public static final int BIRD_SIZE = 30;
	public static final int JUMP_SPEED = 10;

	private BufferedImage boi;
	private Rectangle bird;
	private ArrayList<Rectangle> columns;
	private Random topColumnHeight;
	private boolean isJumping;
	private int score;
	private boolean gameOver;

	public class ThreadAdd extends Thread {
		@Override
		public void run() {
			while (true) {
				addColumn();
				try {
					ThreadAdd.sleep(1500);
				} catch (InterruptedException e) {}
			}
		}
	}

	public FlappyBoi() {
		bird = new Rectangle(WIDTH/2 - BIRD_SIZE/2, HEIGHT/2 - BIRD_SIZE/2, BIRD_SIZE, BIRD_SIZE);
		columns = new ArrayList<>(5);
		topColumnHeight = new Random();
		isJumping = false;
		score = 0;
		gameOver = false;
//		try {
//			boi = ImageIO.read(new File("images\\Capture.PNG"));
//		} catch (IOException e){e.printStackTrace();}
//		
//		boi = resize(boi, bird.width, bird.height);
	}
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) { // Not created by me
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  

	public ArrayList<Rectangle> getColumns() {
		return columns;
	}

	public void setColumn() {
		for (Rectangle r : columns)
			r.x -= 1;
	}

	public void gravity() {
		if (bird.y < 800 - 140)
			bird.y += 1;
	}

	@Override
	public void paint(Graphics g) {
		final int SCORE_TEXT_SIZE = 50;
		final int OUTLINE_THICKNESS = 5;
		final int GAME_OVER_SIZE = 100;
		final String FLAPPY_BIRD_FONT = "C:\\Users\\arkbull\\Documents\\Fonts\\04B_19__.TTF";
		
		g.setColor(Color.cyan);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - LAND_HEIGHT, WIDTH, LAND_HEIGHT);

		g.setColor(new Color(0, 150, 0));
		g.fillRect(0, HEIGHT - LAND_HEIGHT - GRASS_HEIGHT, WIDTH, GRASS_HEIGHT);
		


		try {
			for (Rectangle r : columns) {
				g.setColor(Color.GREEN);
				g.fillRect(r.x, r.y, r.width, r.height);
			}	
		} catch (ConcurrentModificationException x) {System.out.println(x.getMessage());}
		
		g.setColor(Color.RED);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		g.drawImage(boi, bird.x, bird.y, null);
		
		if (!gameOver()) {
			try
			{
				paintOutline(g,FLAPPY_BIRD_FONT,"" + (score / 2), WIDTH / 2,100,SCORE_TEXT_SIZE,Color.BLACK,Color.WHITE);
			} catch (FontFormatException | IOException e1) {e1.printStackTrace();}
		}
		else {
			try {
				paintOutline(g,FLAPPY_BIRD_FONT,"GAME OVER", WIDTH/2,HEIGHT/2,GAME_OVER_SIZE,Color.BLACK,Color.WHITE);
				Font o = newFont(FLAPPY_BIRD_FONT).deriveFont(1, 50);
				g.setFont(o);
				FontMetrics fm = g.getFontMetrics();
				paintOutline(g,FLAPPY_BIRD_FONT,"SCORE: " + (score / 2), WIDTH/2,HEIGHT/2+fm.getAscent(),SCORE_TEXT_SIZE,Color.BLACK,Color.WHITE);
			} catch (FontFormatException | IOException e) {e.printStackTrace();}
		}
		
	}

	private void paintOutline(Graphics g, String font, String phrase, int xPos, int yPos, int thickness, Color outLine, Color fontColor) throws FontFormatException, IOException {
		g.setColor(outLine);
		Font o = newFont(font).deriveFont(1, thickness);
		g.setFont(o);
		FontMetrics fm = g.getFontMetrics();
		int centeredX = xPos - (fm.stringWidth(phrase) / 2);
		int centeredY = yPos - (fm.getAscent() / 2);
		g.drawString(phrase, centeredX - 5, centeredY - 5);
		g.drawString(phrase, centeredX + 5, centeredY - 5);
		g.drawString(phrase, centeredX - 5, centeredY + 5);
		g.drawString(phrase, centeredX + 5, centeredY + 5);
		
		g.setColor(fontColor);
		g.drawString(phrase, centeredX, centeredY);
	}
	
	private Font newFont(String path) throws FontFormatException, IOException {
		Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File(path)));
		return font;
	}
	
	public Rectangle makeColumn() {
		Rectangle col = new Rectangle(WIDTH, 0, PIPE_WIDTH, topColumnHeight.nextInt(400));
		return col;
	}

	public void addColumn() {
		Rectangle col = makeColumn();
		columns.add(col);
		columns.add(new Rectangle(WIDTH, col.height + PIPE_VERTICAL_SPACE, col.width, 680 - col.height - PIPE_VERTICAL_SPACE));

	}

	public void moveColumns() {
		try {
			for (Rectangle r : columns) 
				r.x -= 1;
		}  catch (ConcurrentModificationException x) {System.out.println(x.getMessage());}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent e)  {
		int click = e.getKeyCode();
		if (click == KeyEvent.VK_SPACE && isJumping) {
				System.out.println("CAN'T JUMP"); 
				e.consume();
		}
		else if (click == KeyEvent.VK_SPACE) {
				jump();	
				isJumping = false;	
		}
	}
	
	public void jump() {
		if (bird.y > 10)
			for (int i = 0; i < 20; i++) {
				isJumping = true;
				if (!gameOver()) {
					bird.y = bird.y - JUMP_SPEED;
					paintImmediately(0, 0, 800, 800);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {}
			}
	}

	public boolean gameOver() {
		try {
			for (Rectangle r : columns) {
				if (bird.y >= r.y && bird.y <= r.y + r.height && bird.x >= r.x && bird.x <= r.x + r.width)
					return true;
			}
			return false;	
		}
		catch (ConcurrentModificationException x) {System.out.println(x.getMessage()); return true;}
	}
	
	public void score() {
		try {
			for (Rectangle r: columns)
				if (bird.x == r.x + PIPE_WIDTH)
					score++;
		} catch (ConcurrentModificationException x) {System.out.println(x.getMessage());}	
	}

	public int getScore() {
		return score;
	}
	
	public boolean getJump() {
		return isJumping;
	}
	
//	public void resetGame(FlappyBoi flap, JFrame frame) throws InterruptedException {
//		flap = new FlappyBoi();
//		
//		frame.setSize(WIDTH, HEIGHT);
//		frame.setTitle("FlappyBoi");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.add(flap);
//		frame.setVisible(true);
//		frame.addKeyListener(flap);
//		frame.setFocusable(true);
//		frame.requestFocus();
//		
//		ThreadAdd t =  flap.new ThreadAdd();
//		t.start();
//
//		while (true) {
//			if (!flap.gameOver()) {
//				flap.gravity();
//				flap.moveColumns();
//				flap.score();
//				flap.repaint();
//				Thread.sleep(3);
//			}
//			else
//				break;
//		}
//		System.out.println("SCORE: " + (flap.getScore() / 2));     
//	}
	
	public static void main(String[] args) throws InterruptedException {
		FlappyBoi flap = new FlappyBoi();
		JFrame frame = new JFrame();
//		while (!flap.gameOver()) {
//			flap.resetGame(flap, frame);
//		}
		frame.setSize(WIDTH, HEIGHT);
		frame.setTitle("FlappyBoi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(flap);
		frame.setVisible(true);
		frame.addKeyListener(flap);
		frame.setFocusable(true);
		frame.requestFocus();
		
		ThreadAdd t =  flap.new ThreadAdd();
		t.start();

		while (true) {
			if (!flap.gameOver()) {
				flap.gravity();
				flap.moveColumns();
				flap.score();
				flap.repaint();
				Thread.sleep(3);
			}
			else
				break;
		}
		System.out.println("SCORE: " + (flap.getScore() / 2));     

	}	
}





