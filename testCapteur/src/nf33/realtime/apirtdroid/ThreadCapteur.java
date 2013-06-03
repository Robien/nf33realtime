package nf33.realtime.apirtdroid;

import android.util.Log;

public class ThreadCapteur extends Thread
{

	private CapteurManager capteurManager;
	private RTRunnable programmeUtilisateur;
	private RTDroid rtdroid;
	private Long periodeDemande;

	public ThreadCapteur(CapteurManager mgr, RTRunnable programmeUtilisateur, RTDroid rtdroid, Long periodeDemande)
	{
		capteurManager = mgr;
		this.programmeUtilisateur = programmeUtilisateur;
		this.rtdroid = rtdroid;
		this.periodeDemande = periodeDemande;  

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
					break;
				}
			}
		}
		while (capteurManager.nextCapteur());
//		capteurManager.stopMesure();
		Long max = capteurManager.getPeriodeMax();
		if (periodeDemande >=  max)
		{
			rtdroid.endConfiguration(true, periodeDemande);
			programmeUtilisateur.endConfiguration(true, periodeDemande);
		}
		else if (periodeDemande == 0)
		{
			rtdroid.endConfiguration(true, max);
			programmeUtilisateur.endConfiguration(true, max);
		}
		else
		{
			rtdroid.endConfiguration(true, 0l);
			programmeUtilisateur.endConfiguration(false, 0l);
		}
	}
}
