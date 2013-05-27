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
		do
		{
			Log.d("DADU", "test du capteur n°" + capteurManager.getCurrentCapteur().getId() + " : " + capteurManager.getCurrentCapteur().getName());
			while (capteurManager.getCurrentCapteur().getMaxPeriode() == 0l)
			{
				Log.d("DAD", "aucune donnée, on continu");
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		while (capteurManager.nextCapteur());
		capteurManager.stopMesure();
		programmeUtilisateur.endConfiguration(true, capteurManager.getPeriodeMax());
		rtdroid.enConfiguration();
	}
}
