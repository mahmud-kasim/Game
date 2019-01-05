package AmiralBatti;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;

/**
 * 
 *
 *Author : Mahmud Kasım
 */

public class HelpBrowser extends JFrame
{
	private JEditorPane contents;

	public HelpBrowser()
	{
		super("AmiralBatti Yardim");

		Container c = getContentPane();

		contents = new JEditorPane();

		contents.setEditable(false);
		contents.addHyperlinkListener( new HyperlinkListener()
		{
			public void hyperlinkUpdate( HyperlinkEvent e )
			{
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				   getPage( e.getURL().toString() );
			}
		});
		c.add( new JScrollPane(contents), BorderLayout.CENTER);
		setSize(600,480);
		setVisible(true);
		try
		{
			getPage( new File("Yardim.html").toURL().toString() );
		}
		catch(MalformedURLException mue){}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void getPage(String location)
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR ));

		try
		{
			contents.setPage(location);
		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(this, "Hata", "Kotu Dosya ",
				JOptionPane.ERROR_MESSAGE );
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));
	}
}