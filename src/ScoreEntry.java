import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Jacob Faulk
 */

@SuppressWarnings("serial")
public class ScoreEntry implements Comparable<ScoreEntry>, Serializable
{
	private String name;
	private int score;
	private Calendar cal;

	public ScoreEntry(String name, int score)
	{
		this.name = name;
		this.score = score;
		cal = Calendar.getInstance();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	@Override
	public int compareTo(ScoreEntry e)
	{
		return -1 * new Integer(score).compareTo(new Integer(e.getScore()));
	}

	public String getDate()
	{
		String day = "";
		int dayNum = cal.get(Calendar.MINUTE);
		if(dayNum < 10)
			day = "0" + dayNum;
		else
			day = "" + dayNum;
		int hour = 0;
		if(cal.get(Calendar.HOUR) == 0)
			hour = 12;
		else
			hour = cal.get(Calendar.HOUR);
		String amPm = "";
		if(cal.get(Calendar.AM_PM) == 0)
			amPm = "am";
		else
			amPm = "pm";
		return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR) + " "
				+ hour + ":" + day + amPm;
	}
}
