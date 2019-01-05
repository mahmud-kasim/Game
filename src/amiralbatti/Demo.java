package AmiralBatti;

/**
 * Demo.java:   Oyunculara Alan seçme imkanı Sunar
 *  Author : Mahmud Kasım
 */

class Demo extends Game implements Runnable
{
	Demo()
	{
		super("Amiral Batti Demo");
		new Thread(this, "Demo").start();
	}

	public void run()
	{
		if (AmiralBatti.soundOn()) Sound.start.play();
		myField.placeShips();
		while (demoRunning)
		{
			thePoint = myField.getPoint();
			result = myField.getHit(thePoint);		
			if (result>0) myField.setResult(thePoint, result);
			try	{	Thread.sleep(10);	}
			catch	(InterruptedException ie)	{	ie.printStackTrace();	}
		}
	}
}
