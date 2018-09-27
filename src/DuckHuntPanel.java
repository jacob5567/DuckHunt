import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Jacob Faulk
 */

@SuppressWarnings("serial")
public class DuckHuntPanel extends JPanel implements Action, KeyListener, MouseListener
{
	private Timer timer;

	private static int width;
	private static int height;

	private BufferedImage reticle;
	private Point mouseLoc;
	AudioInputStream audioIn;
	static ArrayList<AudioInputStream> cachedSounds = new ArrayList<AudioInputStream>();

	private Board board;

	private final int NUM_DUCKS_PER_ROUND = 3;

	private BufferedImage spriteSheet;
	private BufferedImage bulletHole;
	private BufferedImage cloud;

	private long frameNum;
	private int cloudX;
	private int cloudY = 20;
	private int cloudDx = 3;

	private boolean end;
	private boolean sound;
	private boolean showScoreboard = false;
	private int cooldown;
	private final int COOLDOWN = 50;
	private int duckEscapeTime = 1100;
	private final int numRounds = 13;
	private int currentRound;
	private ArrayList<Point> shots = new ArrayList<Point>();
	private int shotsNum;
	private int hitsNum;
	private DecimalFormat formatter = new DecimalFormat("###.##%");

	private ArrayList<ScoreEntry> scoreboard = new ArrayList<ScoreEntry>();

	private StringBuffer name = new StringBuffer();
	private boolean scoreFirst = true;
	private boolean entered = false;
	private boolean startScreen = true;
	private boolean loadScreen = false;
	private BufferedImage logo = null;
	private int loadTimer = 50;

	@SuppressWarnings("unchecked")
	public DuckHuntPanel()
	{
		if(new File("scoreboard.txt").exists())
		{
			ObjectInputStream ois;
			try
			{
				ois = new ObjectInputStream(new FileInputStream("scoreboard.txt"));
				scoreboard = (ArrayList<ScoreEntry>) ois.readObject();
			} catch(IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		timer = new Timer(16, this);
		addKeyListener(this);
		addMouseListener(this);

		this.setBackground(new Color(84, 172, 255));

		this.setFocusable(true);
		requestFocus();
		this.setFocusTraversalKeysEnabled(false);

		width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setPreferredSize(new Dimension(width, height));

		cooldown = COOLDOWN;

		try
		{
			reticle = ImageIO.read(new File("reticle_simple.png"));
		} catch(IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			spriteSheet = ImageIO.read(new File("Duckhunt_various_sheet.png"));
			bulletHole = ImageIO.read(new File("bullet_hole.png"));
			cloud = ImageIO.read(new File("cloud.png"));
			logo = ImageIO.read(new File("Duck_Hunt_Logo.png"));
		} catch(IOException e)
		{
			e.printStackTrace();
		}

		BufferedImage duckImg1 = spriteSheet.getSubimage(130, 119, 33, 25);
		BufferedImage duckImg2 = spriteSheet.getSubimage(170, 119, 33, 25);
		BufferedImage duckImg3 = spriteSheet.getSubimage(211, 119, 33, 25);
		BufferedImage duckShot = spriteSheet.getSubimage(131, 238, 33, 28);
		BufferedImage deadDuck = spriteSheet.getSubimage(173, 238, 33, 29);

		BufferedImage duck2Img1 = spriteSheet.getSubimage(0, 119, 33, 25);
		BufferedImage duck2Img2 = spriteSheet.getSubimage(40, 119, 33, 25);
		BufferedImage duck2Img3 = spriteSheet.getSubimage(81, 119, 33, 25);
		BufferedImage duck2Shot = spriteSheet.getSubimage(1, 238, 33, 28);
		BufferedImage deadDuck2 = spriteSheet.getSubimage(43, 238, 33, 29);

		BufferedImage duck3Img1 = spriteSheet.getSubimage(260, 119, 33, 25);
		BufferedImage duck3Img2 = spriteSheet.getSubimage(300, 119, 33, 25);
		BufferedImage duck3Img3 = spriteSheet.getSubimage(341, 119, 33, 25);
		BufferedImage duck3Shot = spriteSheet.getSubimage(261, 238, 33, 28);
		BufferedImage deadDuck3 = spriteSheet.getSubimage(303, 238, 33, 29);

		BufferedImage[] flyAwayImgs1 = { spriteSheet.getSubimage(130, 157, 31, 30),
				spriteSheet.getSubimage(170, 157, 31, 30), spriteSheet.getSubimage(211, 157, 31, 30) };
		BufferedImage[] flyAwayImgs2 = { spriteSheet.getSubimage(0, 157, 31, 30),
				spriteSheet.getSubimage(40, 157, 31, 30), spriteSheet.getSubimage(81, 157, 31, 30) };
		BufferedImage[] flyAwayImgs3 = { spriteSheet.getSubimage(260, 157, 31, 30),
				spriteSheet.getSubimage(300, 157, 31, 30), spriteSheet.getSubimage(341, 157, 31, 30) };

		board = new Board(duckImg1, duckImg2, duckImg3, duckShot, deadDuck, duck2Img1, duck2Img2, duck2Img3, duck2Shot,
				deadDuck2, duck3Img1, duck3Img2, duck3Img3, duck3Shot, deadDuck3, flyAwayImgs1, flyAwayImgs2, flyAwayImgs3);

		Cursor reticleCur = Toolkit.getDefaultToolkit().createCustomCursor(reticle, new Point(16, 16), "reticle");

		setCursor(reticleCur);

		/*
		 * textField.addActionListener(this); textField.addKeyListener(this);
		 * c.gridwidth = GridBagConstraints.REMAINDER;
		 * 
		 * c.fill = GridBagConstraints.HORIZONTAL;
		 * 
		 * add(textField, c);
		 */

		/*
		 * 
		 * try { audioIn = AudioSystem.getAudioInputStream(new
		 * File("266105_marregheriti_shotgun_mp3cut_net_.wav")); shotSound =
		 * AudioSystem.getClip(); shotSound.open(audioIn); }
		 * catch(UnsupportedAudioFileException | IOException |
		 * LineUnavailableException e) { e.printStackTrace(); }
		 * 
		 */

		AudioInputStream shotSound, quack, flap;
		try
		{
			shotSound = createReusableAudioInputStream(new File("266105_marregheriti_shotgun_mp3cut_net_.wav"));
			quack = createReusableAudioInputStream(new File("99-quack-sfx-.wav"));
			flap = createReusableAudioInputStream(new File("bats_trimmed.wav"));
			cachedSounds.add(shotSound);
			cachedSounds.add(quack);
			cachedSounds.add(flap);
		} catch(IOException | UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}

		timer.start();
	}

	public void drawFrame(Graphics g, long frame, int width, int height)
	{
		if(startScreen)
		{
			setBackground(Color.BLACK);
			g.drawImage(logo, width / 4, height / 7, width / 2, (int) (height / 5 * 2.5), null);
			g.setFont(new Font(Font.SERIF, Font.BOLD, 50));
			g.setColor(Color.RED);
			g.drawString("Press ENTER to start", width / 2 - "Press ENTER to start".length() * 12, height / 4 * 3);
			g.setFont(new Font(Font.SERIF, Font.BOLD, 20));
			g.drawString("Created by Jacob Faulk", width - 220, 30);
		}
		else if(loadScreen)
		{
			if(loadTimer == 50)
				try
				{
					playCachedSound(0, true);
				} catch(IOException | LineUnavailableException e)
				{
					e.printStackTrace();
				}
			g.setFont(new Font(Font.SERIF, Font.BOLD, 50));
			g.setColor(Color.WHITE);
			g.drawString("Loading...", width / 2 - "Loading...".length() * 12, height / 2);
			if(loadTimer < 0)
			{
				loadScreen = false;
				this.setBackground(new Color(84, 172, 255));
			}
			loadTimer--;
		}
		else if(showScoreboard)
		{
			if(scoreFirst)
			{
				setBackground(Color.BLACK);
				scoreFirst = false;
			}
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SERIF, 1, 50));
			g.drawString("SCORES", (int) (width / 2) - 80, 60);
			g.setFont(new Font(Font.SERIF, 1, 20));
			if(!entered)
			{
				if(frame % 50 < 25)
					g.drawString("Enter name: " + name.toString() + "|",
							(int) (width / 2 - ((name.length() + "Enter name: ".length()) * 4.75)), 100);
				else
					g.drawString("Enter name: " + name.toString(),
							(int) (width / 2 - ((name.length() + "Enter name: ".length()) * 4.75)), 100);
			}
			if(!scoreboard.isEmpty())
				for(int i = 0; i < scoreboard.size(); i++)
				{
					g.drawString(scoreboard.get(i).getName(), width / 3 + 20, 120 + i * 40);
					g.drawString(scoreboard.get(i).getScore() + "pts  - " + scoreboard.get(i).getDate(),
							(int) (width / 3 * 1.5) + 20, 120 + i * 40);
				}
		}
		else
		{
			if(currentRound > numRounds)
				showScoreboard = true;

			mouseLoc = MouseInfo.getPointerInfo().getLocation();

			cloudX += cloudDx;

			if(cloudX > width)
				cloudX = -2000;

			if(board.flyAway && frame % 15 == 0)
				try
				{
					playCachedSound(2, false);
				} catch(IOException | LineUnavailableException e1)
				{
					e1.printStackTrace();
				}

			if(cooldown == COOLDOWN && sound)
			{

				try
				{
					playCachedSound(0, false);
				} catch(IOException | LineUnavailableException e)
				{
					e.printStackTrace();
				}

				/*
				 * try { audioIn.reset(); } catch(IOException e) {
				 * e.printStackTrace(); } shotSound.start();
				 */
				shotsNum++;
				shots.add(mouseLoc);
			}

			for(Point p : shots)
				g.drawImage(bulletHole, p.x - 5, p.y - 5, 10, 10, null);

			g.drawImage(cloud, cloudX, cloudY, 2000, 200, null);

			g.setFont(new Font(Font.SERIF, 1, 20));
			g.drawString("Round " + currentRound + " Score: " + Duck.score + "  Accuracy: "
					+ formatter.format((double) hitsNum / (double) shotsNum) + "  Kills: " + Duck.killCount + "  Headshots: "
					+ Duck.headshots, 10, 30);

			board.move(width, height, frameNum);
			board.draw(g, frame, width, height);

			if(board.hit(mouseLoc, (cooldown == COOLDOWN) && sound))
			{
				try
				{
					playCachedSound(1, false);
				} catch(IOException | LineUnavailableException e)
				{
					e.printStackTrace();
				}
				hitsNum++;
			}

			/*
			 * if(board.isEmpty()) { for(int i = 0; i < numDuckAdd; i++) {
			 * board.addDuck(width, height); } numDuckAdd++;// numDuckAdd = (int)
			 * Math.pow(numDuckAdd, 2); }
			 */

			/*
			 * if(rand.nextInt(100) == 0) board.addDuck(width, height,
			 * Integer.MAX_VALUE);
			 */

			if(board.isEmpty())
			{
				for(int i = 0; i < NUM_DUCKS_PER_ROUND; i++)
					board.addDuck(width, height, duckEscapeTime);
				if(duckEscapeTime > 300)
					duckEscapeTime -= 100;
				currentRound++;
			}

			if(sound)
			{
				cooldown--;
				if(cooldown <= 0)
				{
					cooldown = COOLDOWN;
					sound = false;
				}
			}

			g.setColor(Color.BLACK);
			g.drawRect(mouseLoc.x - 15, mouseLoc.y + 20, 30, 10);
			g.setColor(Color.GREEN);
			g.fillRect(mouseLoc.x - 14, mouseLoc.y + 21, cooldown / 3 * 2 - 3, 9);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		frameNum++;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(end)
		{
			try
			{
				if(!new File("scoreboard.txt").exists())
				{
					@SuppressWarnings("unused")
					File board = new File("scoreboard.txt");
				}
				FileOutputStream fos = new FileOutputStream("scoreboard.txt");
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(scoreboard);
				oos.close();
			} catch(IOException e)
			{

			}
			setVisible(false);
			System.exit(0);
		}
		drawFrame(g, frameNum, width, height);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			end = true;
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE && name.length() != 0)
			name.deleteCharAt(name.length() - 1);
		if(e.getKeyCode() == KeyEvent.VK_ENTER && name.length() != 0 && !entered)
		{
			scoreboard
					.add(new ScoreEntry(name.toString(), (int) ((Duck.score * 2) * ((double) hitsNum / (double) shotsNum))));
			Collections.sort(scoreboard);
			if(scoreboard.size() > 15)
				scoreboard.remove(scoreboard.size() - 1);
			entered = true;
		}

		if(startScreen && e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			startScreen = false;
			loadScreen = true;
		}

		if(showScoreboard && name.length() <= 10)
		{
			if(e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_ESCAPE
					&& e.getKeyCode() != KeyEvent.VK_SHIFT && e.getKeyCode() != KeyEvent.VK_CAPS_LOCK
					&& e.getKeyCode() != KeyEvent.VK_TAB && e.getKeyCode() != KeyEvent.VK_CONTROL
					&& e.getKeyCode() != KeyEvent.VK_ALT && e.getKeyCode() != KeyEvent.VK_UP
					&& e.getKeyCode() != KeyEvent.VK_DOWN && e.getKeyCode() != KeyEvent.VK_RIGHT
					&& e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_F1
					&& e.getKeyCode() != KeyEvent.VK_F2 && e.getKeyCode() != KeyEvent.VK_F3
					&& e.getKeyCode() != KeyEvent.VK_F4 && e.getKeyCode() != KeyEvent.VK_F5
					&& e.getKeyCode() != KeyEvent.VK_F6 && e.getKeyCode() != KeyEvent.VK_F7
					&& e.getKeyCode() != KeyEvent.VK_F8 && e.getKeyCode() != KeyEvent.VK_F9
					&& e.getKeyCode() != KeyEvent.VK_F10 && e.getKeyCode() != KeyEvent.VK_F11
					&& e.getKeyCode() != KeyEvent.VK_F12 && e.getKeyCode() != KeyEvent.VK_INSERT
					&& e.getKeyCode() != KeyEvent.VK_DELETE && e.getKeyCode() != KeyEvent.VK_PRINTSCREEN
					&& e.getKeyCode() != KeyEvent.VK_WINDOWS && e.getKeyCode() != KeyEvent.VK_PAGE_DOWN
					&& e.getKeyCode() != KeyEvent.VK_PAGE_UP && e.getKeyCode() != KeyEvent.VK_HOME
					&& e.getKeyCode() != KeyEvent.VK_END && e.getKeyCode() != KeyEvent.SHIFT_DOWN_MASK
					&& e.getKeyCode() != KeyEvent.VK_BACK_SPACE)
				name.append(e.getKeyChar());
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public Object getValue(String arg0)
	{
		return null;
	}

	@Override
	public void putValue(String key, Object value)
	{
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		requestFocusInWindow();
		if(e.getButton() == MouseEvent.BUTTON1)
			sound = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	public static void playCachedSound(int i, boolean load) throws IOException, LineUnavailableException
	{
		AudioInputStream stream = cachedSounds.get(i);
		stream.reset();
		Clip clip = AudioSystem.getClip();
		clip.open(stream);
		if(load)
		{
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-80.0f);

		}
		else if(i == 0)
		{
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-17.0f);
		}
		else if(i == 1)
		{
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(6.0206f);
		}
		clip.start();
	}

	private static AudioInputStream createReusableAudioInputStream(File file)
			throws IOException, UnsupportedAudioFileException
	{
		AudioInputStream ais = null;
		try
		{
			ais = AudioSystem.getAudioInputStream(file);
			byte[] buffer = new byte[1024 * 32];
			int read = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream(buffer.length);
			while((read = ais.read(buffer, 0, buffer.length)) != -1)
			{
				baos.write(buffer, 0, read);
			}
			AudioInputStream reusableAis = new AudioInputStream(new ByteArrayInputStream(baos.toByteArray()),
					ais.getFormat(), AudioSystem.NOT_SPECIFIED);
			return reusableAis;
		} finally
		{
			if(ais != null)
			{
				ais.close();
			}
		}
	}
}