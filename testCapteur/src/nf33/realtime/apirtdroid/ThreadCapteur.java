package nf33.realtime.apirtdroid;

import android.util.Log;

public class ThreadCapteur extends Thread
{

	private CapteurManager capteurManager;
	private RTRunnable programmeUtilisateur;
	private RTDroid rtdroid;

	public ThreadCapteur(CapteurManager mgr, RTRunnable programmeUtilisateur, RTDroid rtdroid)
	{
		capteurManager = mgr;
		this.programmeUtilisateur = programmeUtilisateur;
		this.rtdroid = rtdroid;

	}

	public void run()
	{

		capteurManager.startMesure();
		while (capteurManager.nextCapteur())
		{
			try
			{
				Log.d("DADU", "début attente");
				Thread.sleep(10000);
				Log.d("DADU", "fin attente");
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		programmeUtilisateur.endConfiguration(true, capteurManager.getPeriodeMax());
		rtdroid.enConfiguration();
	}
}
