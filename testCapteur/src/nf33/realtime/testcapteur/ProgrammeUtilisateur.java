package nf33.realtime.testcapteur;

import android.app.Activity;
import nf33.realtime.apirtdroid.RTDroid;
import nf33.realtime.apirtdroid.RTRunnable;

public class ProgrammeUtilisateur implements RTRunnable
{

	private RTDroid rtdroid;
	
	ProgrammeUtilisateur(RTDroid rtdroid)
	{
		this.rtdroid = rtdroid;
	}
	



	@Override
	public void endConfiguration(Boolean isRunable, Long frequence, Long wcet)
	{
		// TODO Auto-generated method stub
		
	}




	@Override
	public void periodicEvent(long timeSinceLast)
	{
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep((long) (Math.random()*90), 0);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
