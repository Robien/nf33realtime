package nf33.realtime.testcapteur;

import android.app.Activity;
import nf33.realtime.apirtdroid.RTDroid;
import nf33.realtime.apirtdroid.Runnable;

public class ProgrammeUtilisateur implements Runnable
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

}
