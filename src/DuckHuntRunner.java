import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Jacob Faulk
 */

public class DuckHuntRunner
{

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				createAndShowGUI();
			}
		});
	}

	public static void createAndShowGUI()
	{
		JFrame frame = new JFrame("Duck Hunt");
		DuckHuntPanel panel = new DuckHuntPanel();
		frame.setContentPane(panel);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.pack();
		frame.setResizable(false);

		frame.setVisible(true);
	}

}