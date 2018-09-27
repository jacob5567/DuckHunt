import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Jacob Faulk
 */

public class Board
{
	private List<Duck> ducks;

	private Random rand = new Random();

	private BufferedImage img1;
	private BufferedImage img2;
	private BufferedImage img3;
	private BufferedImage shotImg;
	private BufferedImage deadImg;

	private BufferedImage img21;
	private BufferedImage img22;
	private BufferedImage img23;
	private BufferedImage shotImg2;
	private BufferedImage deadImg2;

	private BufferedImage img31;
	private BufferedImage img32;
	private BufferedImage img33;
	private BufferedImage shotImg3;
	private BufferedImage deadImg3;

	private BufferedImage[] flyAwayImgs1;
	private BufferedImage[] flyAwayImgs2;
	private BufferedImage[] flyAwayImgs3;

	private final int FLY_AWAY_LOSS_TIME = 50;
	private int flyAwayLoss = FLY_AWAY_LOSS_TIME;
	public boolean flyAway;
	private boolean firstEscape = true;

	public Board(BufferedImage img1, BufferedImage img2, BufferedImage img3, BufferedImage shotImg,
			BufferedImage deadImg, BufferedImage img21, BufferedImage img22, BufferedImage img23, BufferedImage shotImg2,
			BufferedImage deadImg2, BufferedImage img31, BufferedImage img32, BufferedImage img33, BufferedImage shotImg3,
			BufferedImage deadImg3, BufferedImage[] flyAwayImgs1, BufferedImage[] flyAwayImgs2,
			BufferedImage[] flyAwayImgs3)
	{

		this.img1 = img1;
		this.img2 = img2;
		this.img3 = img3;
		this.shotImg = shotImg;
		this.deadImg = deadImg;

		this.img21 = img21;
		this.img22 = img22;
		this.img23 = img23;
		this.shotImg2 = shotImg2;
		this.deadImg2 = deadImg2;

		this.img31 = img31;
		this.img32 = img32;
		this.img33 = img33;
		this.shotImg3 = shotImg3;
		this.deadImg3 = deadImg3;

		this.flyAwayImgs1 = flyAwayImgs1;
		this.flyAwayImgs2 = flyAwayImgs2;
		this.flyAwayImgs3 = flyAwayImgs3;

		ducks = new ArrayList<Duck>();
	}

	public void addDuck(int screenWidth, int screenHeight, int duckEscapeTime)
	{
		switch(rand.nextInt(3))
		{
		case 0:
			switch(rand.nextInt(2))
			{
			case 0:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img1, img2, img3, shotImg, deadImg, flyAwayImgs1[0], flyAwayImgs1[1],
							flyAwayImgs1[2], true, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img1, img2, img3, shotImg, deadImg, flyAwayImgs1[0], flyAwayImgs1[1],
							flyAwayImgs1[2], true, false, duckEscapeTime));
					break;
				}
				break;
			case 1:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img1, img2, img3, shotImg, deadImg, flyAwayImgs1[0], flyAwayImgs1[1],
							flyAwayImgs1[2], false, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img1, img2, img3, shotImg, deadImg, flyAwayImgs1[0], flyAwayImgs1[1],
							flyAwayImgs1[2], false, false, duckEscapeTime));
					break;
				}
				break;
			}
			break;
		case 1:
			switch(rand.nextInt(2))
			{
			case 0:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img21, img22, img23, shotImg2, deadImg2, flyAwayImgs2[0], flyAwayImgs2[1],
							flyAwayImgs2[2], true, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img21, img22, img23, shotImg2, deadImg2, flyAwayImgs2[0], flyAwayImgs2[1],
							flyAwayImgs2[2], true, false, duckEscapeTime));
					break;
				}
				break;
			case 1:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img21, img22, img23, shotImg2, deadImg2, flyAwayImgs2[0], flyAwayImgs2[1],
							flyAwayImgs2[2], false, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img21, img22, img23, shotImg2, deadImg2, flyAwayImgs2[0], flyAwayImgs2[1],
							flyAwayImgs2[2], false, false, duckEscapeTime));
					break;
				}
				break;
			}
			break;
		case 2:
			switch(rand.nextInt(2))
			{
			case 0:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img31, img32, img33, shotImg3, deadImg3, flyAwayImgs3[0], flyAwayImgs3[1],
							flyAwayImgs3[2], true, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img31, img32, img33, shotImg3, deadImg3, flyAwayImgs3[0], flyAwayImgs3[1],
							flyAwayImgs3[2], true, false, duckEscapeTime));
					break;
				}
				break;
			case 1:
				switch(rand.nextInt(2))
				{
				case 0:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img31, img32, img33, shotImg3, deadImg3, flyAwayImgs3[0], flyAwayImgs3[1],
							flyAwayImgs3[2], false, true, duckEscapeTime));
					break;
				case 1:
					ducks.add(new Duck(new Point(rand.nextInt(screenWidth), rand.nextInt(screenHeight - 50)),
							new Dimension(80, 50), img31, img32, img33, shotImg3, deadImg3, flyAwayImgs3[0], flyAwayImgs3[1],
							flyAwayImgs3[2], false, false, duckEscapeTime));
					break;
				}
				break;
			}
			break;
		}
	}

	public void move(int screenWidth, int screenHeight, long frameNum)
	{
		for(int i = ducks.size() - 1; i >= 0; i--)
		{
			if(ducks.get(i).getEscapeTime() < 0)
			{
				if(firstEscape)
				{
					Duck.score -= 150;
					firstEscape = false;
				}
				flyAway = true;
				if(ducks.get(i).move(screenWidth, screenHeight, frameNum, flyAway))
				{
					firstEscape = true;
					ducks.remove(i);
				}
			}
			else if(ducks.get(i).isOffScreen(screenHeight))
				ducks.remove(i);
			else
				ducks.get(i).move(screenWidth, screenHeight, frameNum, false);
		}
	}

	public void draw(Graphics g, long frameNum, int screenWidth, int screenHeight)
	{
		if(flyAway)
		{
			g.setFont(new Font(Font.DIALOG, 1, 50));
			g.setColor(Color.RED);
			g.drawString("-150", 20, 100);
			g.setFont(new Font(Font.DIALOG, 1, 100));
			g.drawString("DO NOT SHOOT", screenWidth / 2 - ("DO NOT SHOOT".length() * 32), screenHeight / 2);
			flyAwayLoss--;
			if(flyAwayLoss <= 0)
			{
				flyAway = false;
				flyAwayLoss = FLY_AWAY_LOSS_TIME;
			}
		}
		for(Duck d : ducks)
			d.draw(g, frameNum, screenWidth, screenHeight);
	}

	public boolean hit(Point p, boolean click)
	{
		if(flyAway)
			return false;
		for(Duck d : ducks)
		{
			if(d.hit(p, click))
				return true;
		}
		return false;
	}

	public boolean isEmpty()
	{
		return ducks.isEmpty();
	}
}