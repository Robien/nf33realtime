package nf33.realtime.apirtdroid;

import android.util.Log;

public class ThreadCapteur extends Thread
{

	private CapteurManager capteurManager;
	private RTRunnable programmeUtilisateur;
	private RTDroid rtdroid;
	private Long periodeDemande;
	private RTMainThread mainThread;

	public ThreadCapteur(CapteurManager mgr, RTRunnable programmeUtilisateur, RTDroid rtdroid, Long periodeDemande, RTMainThread mainThread)
	{
		capteurManager = mgr;
		this.programmeUtilisateur = programmeUtilisateur;
		this.rtdroid = rtdroid;
		this.periodeDemande = periodeDemande; 
		this.mainThread = mainThread;

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
		
		Log.d("DADU", "Début du calcul du WCET");
		Long total = 0l;
		for (int i = 0; i < 10; ++i)
		{
			Long debut =  System.nanoTime();
			programmeUtilisateur.periodicEvent(0, null);
			Long tmp = System.nanoTime() - debut;
			if (total < tmp)
			{
				total = tmp;
			}
		}
		Long wcetUtilisateur = total;
		Long wcetAPI = 0l;
		for (int i = 0; i < 10; ++i)
		{
			Long tmp = mainThread.voidRun();
			if (wcetAPI < tmp)
			{
				wcetAPI = tmp;
			}
		}
		
		Log.d("DADU", "WCET API : " + wcetAPI);
		total += wcetAPI;
		
		Log.d("DADU", "periode demandé : " + periodeDemande + " max capteur : " + max + " wcet total " + total + " wcet + capteurs " + (max + total));
		Log.d("DADU", "wcetUtilisateur : " + (total-wcetAPI) + " - " + wcetUtilisateur);
		if (periodeDemande >=  max + total)
		{
			rtdroid.endConfiguration(true, periodeDemande, wcetAPI, periodeDemande-max-wcetAPI, max);
			programmeUtilisateur.endConfiguration(true, periodeDemande, total);
		}
		else if (periodeDemande == 0)
		{
			rtdroid.endConfiguration(true, max+total, wcetAPI, total-wcetAPI, max);
			programmeUtilisateur.endConfiguration(true, max + total, total);
		}
		else
		{
			rtdroid.endConfiguration(false, 0l, 0l, 0l, 0l);
			programmeUtilisateur.endConfiguration(false, 0l, total);
		}
	}
}
