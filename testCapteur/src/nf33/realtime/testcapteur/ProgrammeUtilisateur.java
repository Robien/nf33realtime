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
	public void periodicEvent()
	{
		// TODO Auto-generated method stub

	}


	@Override
	public void endConfiguration(Boolean isRunable, float frequence)
	{
		// TODO Auto-generated method stub
		
	}

}
