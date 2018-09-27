import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Jacob Faulk
 */

public class Duck
{
	private static final boolean DEBUG = false;

	public static int score = 0;
	public static int killCount = 0;
	public static int headshots = 0;
	public static final int HIT_POINTS = 100;
	public static final int HEADSHOT_POINTS = 300;

	private Point loc;
	private Dimension size;
	private int dx = 8;
	private double dy = 3;
	private double imageChangeModifier = 4;

	private Rectangle2D.Double boundingBox;
	private Rectangle2D.Double headBox;

	private BufferedImage img1;
	private BufferedImage img2;
	private BufferedImage img3;
	private BufferedImage shotImg;
	private BufferedImage deadImg;

	private BufferedImage img1Reg;
	private BufferedImage img2Reg;
	private BufferedImage img3Reg;
	private BufferedImage shotImgReg;
	private BufferedImage deadImgReg;
	private BufferedImage flyAway1Reg;
	private BufferedImage flyAway2Reg;
	private BufferedImage flyAway3Reg;

	private BufferedImage img1Rev;// http://stackoverflow.com/questions/9558981/flip-image-with-graphics2d
	private BufferedImage img2Rev;
	private BufferedImage img3Rev;
	private BufferedImage shotImgRev;
	private BufferedImage deadImgRev;
	private BufferedImage flyAway1Rev;
	private BufferedImage flyAway2Rev;
	private BufferedImage flyAway3Rev;

	private boolean shot;
	private boolean shotOnce;
	private int countdown;
	private boolean headshot;
	private boolean rev;
	private int escapeTime;

	private Point center;
	private boolean linearMove = false;
	private int frameIncrease;
	private static Random rand = new Random();

	public Duck(Point loc, Dimension size, BufferedImage img1, BufferedImage img2, BufferedImage img3,
			BufferedImage shotImg, BufferedImage deadImg, BufferedImage flyAway1, BufferedImage flyAway2,
			BufferedImage flyAway3, boolean facingLeft, boolean facingUp, int escapeTime)
	{
		this.loc = loc;
		this.size = size;

		this.escapeTime = escapeTime;

		this.img1Reg = img1;
		this.img2Reg = img2;
		this.img3Reg = img3;
		this.shotImgReg = shotImg;
		this.deadImgReg = deadImg;
		this.flyAway1Reg = flyAway1;
		this.flyAway2Reg = flyAway2;
		this.flyAway3Reg = flyAway3;

		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img1.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img1Rev = op.filter(img1, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img2.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img2Rev = op.filter(img2, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-img3.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		img3Rev = op.filter(img3, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-shotImg.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		shotImgRev = op.filter(shotImg, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-deadImg.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		deadImgRev = op.filter(deadImg, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-flyAway1.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		flyAway1Rev = op.filter(flyAway1, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-flyAway2.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		flyAway2Rev = op.filter(flyAway2, null);

		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-flyAway3.getWidth(null), 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		flyAway3Rev = op.filter(flyAway3, null);

		if(!facingLeft)
		{
			this.img1 = img1Reg;
			this.img2 = img2Reg;
			this.img3 = img3Reg;
			this.shotImg = shotImgReg;
			this.deadImg = deadImgReg;
			rev = false;
			this.headBox = new Rectangle2D.Double(loc.x * (2 / 3), loc.y + 5, size.width / 3, size.height / 2);
		}
		else
		{
			dx = -1 * Math.abs(dx);
			this.img1 = img1Rev;
			this.img2 = img2Rev;
			this.img3 = img3Rev;
			this.shotImg = shotImgRev;
			this.deadImg = deadImgRev;
			rev = true;
			this.headBox = new Rectangle2D.Double(loc.x, loc.y + 5, size.width / 3, size.height / 2);
		}
		if(facingUp)
			dy *= -1;

		boundingBox = new Rectangle2D.Double(loc.getX(), loc.getY(), size.getWidth(), size.getHeight());

		center = loc.getLocation();
		frameIncrease = rand.nextInt(360);
	}

	public Point getLoc()
	{
		return loc;
	}

	public Dimension getSize()
	{
		return size;
	}

	public boolean hit(Point p, boolean click)
	{
		if(headBox.contains(p) && click && !shotOnce)
		{
			shot = true;
			size.setSize(size.getWidth(), size.getHeight() + 9);
			dx = 0;
			countdown = 20;
			shotOnce = true;
			score += HIT_POINTS * 3;
			headshot = true;
			headshots++;
			killCount++;

			linearMove = true;

			return true;
		}
		else if(boundingBox.contains(p) && click && !shotOnce)
		{
			shot = true;
			size.setSize(size.getWidth(), size.getHeight() + 9);
			dx = 0;
			countdown = 20;
			shotOnce = true;
			score += HIT_POINTS;
			killCount++;

			linearMove = true;

			return true;
		}
		return false;
	}

	public boolean move(int screenWidth, int screenHeight, long frameNum, boolean flyAway)
	{
		if(flyAway)
		{
			imageChangeModifier = 10;
			size.height = 60;
			size.width = 70;
			dy = -5;
			if(loc.x < size.width)
				rev = false;
			if(loc.x + size.width > screenWidth - size.width)

				rev = true;
			if(!rev)
			{
				dx = 2;
				img1 = flyAway1Reg;
				img2 = flyAway2Reg;
				img3 = flyAway3Reg;
				shotImg = shotImgReg;
				deadImg = deadImgReg;
			}
			else
			{
				dx = -2;
				img1 = flyAway1Rev;
				img2 = flyAway2Rev;
				img3 = flyAway3Rev;
				shotImg = shotImgRev;
				deadImg = deadImgRev;
			}
			loc.translate(dx, (int) dy);
			if(rev)
				headBox.setFrame(loc.x, loc.y + 5, size.width / 3, size.height / 2);
			else
				headBox.setFrame(loc.x + size.width * 2 / 3, loc.y + 5, size.width / 3, size.height / 2);
			boundingBox.setFrame(loc, size);
			if(loc.y + size.height < 0)
				return true;
			return false;
		}
		else
		{
			loc.translate(dx, (int) dy);
			center = loc.getLocation();
			if(!shot)
				escapeTime--;
			if(loc.x + size.width > screenWidth - size.width)
			{
				dx = -1 * Math.abs(dx);
				img1 = img1Rev;
				img2 = img2Rev;
				img3 = img3Rev;
				shotImg = shotImgRev;
				deadImg = deadImgRev;
				rev = true;
			}
			if(loc.x < size.width)
			{
				dx = Math.abs(dx);
				img1 = img1Reg;
				img2 = img2Reg;
				img3 = img3Reg;
				shotImg = shotImgReg;
				deadImg = deadImgReg;
				rev = false;
			}
			if(loc.y + size.height > screenHeight - size.height && !shot)
			{
				dy = -1 * Math.abs(dy);
			}
			if(loc.y < size.height && !shot)
			{
				dy = Math.abs(dy);
			}
			if(shot)
			{
				countdown--;
				if(countdown == 0)
				{
					size.setSize(size.getWidth(), size.getHeight() + 10);
					dy = 7;
				}
				else if(countdown < 0)
				{
					dy += .3;
				}
				else
				{
					dy = 0;
				}
			}
			if(rev)
				headBox.setFrame(loc.x, loc.y + 5, size.width / 3, size.height / 2);
			else
				headBox.setFrame(loc.x + size.width * 2 / 3, loc.y + 5, size.width / 3, size.height / 2);
			boundingBox.setFrame(loc, size);
			if(!linearMove)
			{
				loc.x = (int) (center.x + Math.cos(Math.toRadians(frameNum + frameIncrease)) * 5);
				loc.y = (int) (center.y + Math.sin(Math.toRadians(frameNum + frameIncrease)) * 3);
			}
			return false;
		}
	}

	public void draw(Graphics g, long frameNum, int screenWidth, int screenHeight)
	{
		if(DEBUG)
		{
			g.setColor(Color.BLACK);
			g.drawRect((int) headBox.x, (int) headBox.y, (int) headBox.width, (int) headBox.height);
			g.drawRect(loc.x, loc.y, size.width, size.height);
			g.setFont(new Font(Font.SERIF, 1, 20));
			if(!shot)
				g.drawString("" + escapeTime, 20, 50);
			// g.setColor(new Color(img1Reg.getRGB(5, 17)));
			g.drawLine(screenWidth / 2, screenHeight / 2,
					(int) ((screenWidth / 2) + Math.cos(Math.toRadians(frameNum + frameIncrease)) * 150),
					(int) ((screenHeight / 2) + Math.sin(Math.toRadians(frameNum + frameIncrease)) * 90));
		}
		if(escapeTime == 0)
		{
			g.setFont(new Font(Font.DIALOG, 1, 50));
			g.setColor(Color.RED);
			g.drawString("-500", 20, 100);
		}
		if(img1 == null)
			g.fillRect((int) loc.getX(), (int) loc.getY(), (int) size.getWidth(), (int) size.getHeight());
		else if(!shot)
		{
			if(frameNum % (Math.abs(dx) * imageChangeModifier) < (Math.abs(dx) * (imageChangeModifier / 4)))
				g.drawImage(img1, loc.x, loc.y, size.width, size.height, null);
			else if(frameNum % (Math.abs(dx) * imageChangeModifier) < (Math.abs(dx) * ((imageChangeModifier / 4) * 2)))
				g.drawImage(img2, loc.x, loc.y, size.width, size.height, null);
			else if(frameNum % (Math.abs(dx) * imageChangeModifier) < (Math.abs(dx) * (imageChangeModifier / 4) * 3))
				g.drawImage(img3, loc.x, loc.y, size.width, size.height, null);
			else
				g.drawImage(img2, loc.x, loc.y, size.width, size.height, null);
		}
		else if(shot && countdown > 0)
		{
			g.drawImage(shotImg, loc.x, loc.y, size.width, size.height, null);
			g.setFont(new Font(Font.DIALOG, 1, 20));
			g.setColor(Color.RED);
			if(!headshot)
				g.drawString("+" + HIT_POINTS, loc.x + size.width / 5, loc.y - size.height / 3);
			else
				g.drawString("+" + HEADSHOT_POINTS, loc.x + size.width / 5, loc.y - size.height / 3);
		}
		else
			g.drawImage(deadImg, loc.x, loc.y, size.width, size.height, null);
	}

	public boolean isOffScreen(int screenHeight)
	{
		return loc.y > screenHeight;
	}

	public int getEscapeTime()
	{
		return escapeTime;
	}

}