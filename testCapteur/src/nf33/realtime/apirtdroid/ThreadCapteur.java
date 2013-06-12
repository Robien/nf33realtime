package nf33.realtime.apirtdroid;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
		Long maxCapteurWait = capteurManager.getPeriodeMax();
		
		Log.d("DADU", "Début du calcul du WCET !");
		ArrayList<Capteur> listeCapteurUtilise = capteurManager.getListeCapteurUtilise();
		Log.d("DADU", "1");
		ArrayList<CapteurValue> listeCapteurValue = new ArrayList<CapteurValue>();
		Log.d("DADU", "2");
		for (int i = 0; i < listeCapteurUtilise.size();++i)
		{
			Log.d("DADU", "3");
			listeCapteurValue.add(listeCapteurUtilise.get(i).getCapteurValue());
		}
		Log.d("DADU", "4");
		Long total = 0l;
		int progression = -1;
		programmeUtilisateur.progressConfiguration(1, 0);
		Log.d("DADU", "Début du calcul du WCET boucle");
		for (int i = 0; i < Tools.nbIterationWCETUtilisateur; ++i)
		{
			for (int j = 0; j < listeCapteurUtilise.size(); j++)
			{
				listeCapteurUtilise.get(j).setRandomValues();
			}
			Long debut =  System.nanoTime();
			programmeUtilisateur.periodicEvent(periodeDemande, listeCapteurValue);
			Long tmp = System.nanoTime() - debut;
			if (total < tmp)
			{
				total = tmp;
			}
			if ((int)((i*100)/ Tools.nbIterationWCETUtilisateur) != progression)
			{
				progression = (int)((i*100)/ Tools.nbIterationWCETUtilisateur);
				programmeUtilisateur.progressConfiguration(1, progression);
			}
		}
		Long wcetUtilisateur = total;
		
		Long wcetAPI = 0l;
		Log.d("DADU", "Debut calcul WCETAPI");
		progression = -1;
		programmeUtilisateur.progressConfiguration(2, 0);
		for (int i = 0; i < Tools.nbIterationWCETAPI; ++i)
		{
			Long tmp = mainThread.voidRun(maxCapteurWait);
			if (wcetAPI < tmp)
			{
				wcetAPI = tmp;
			}
			if ((int)((i*100)/ Tools.nbIterationWCETAPI) != progression)
			{
				progression = (int)((i*100)/ Tools.nbIterationWCETAPI);
				programmeUtilisateur.progressConfiguration(2, progression);
			}
		}
		programmeUtilisateur.progressConfiguration(2, 100);
		
		Log.d("DADU", "WCET API : " + wcetAPI);
		total += wcetAPI;
		
		Log.d("DADU", "periode demandé : " + periodeDemande + " max capteur : " + maxCapteurWait + " wcet total " + total + " wcet + capteurs " + (maxCapteurWait + total));
		Log.d("DADU", "wcetUtilisateur : " + (total-wcetAPI) + " - " + wcetUtilisateur);
		if (periodeDemande >=  maxCapteurWait + total)
		{
			rtdroid.endConfiguration(true, periodeDemande, wcetAPI, periodeDemande-maxCapteurWait-wcetAPI, maxCapteurWait);
			programmeUtilisateur.endConfiguration(true, periodeDemande, total);
		}
		else if (periodeDemande == 0)
		{
			rtdroid.endConfiguration(true, maxCapteurWait+total, wcetAPI, total-wcetAPI, maxCapteurWait);
			programmeUtilisateur.endConfiguration(true, maxCapteurWait + total, total);
		}
		else
		{
			rtdroid.endConfiguration(false, 0l, 0l, 0l, 0l);
			programmeUtilisateur.endConfiguration(false, 0l, total);
		}
	}
}
