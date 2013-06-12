package nf33.realtime.apirtdroid;

import java.util.ArrayList;

import android.util.Log;

//cette classe permet de tester les capteurs pour trouver la fréquence maximale à laquelle ils peuvent fonctionner.
//Le calcul du WCET du programme utilisateur et du WCET de la partie API est calculé ici.
//Lorsque les calculs sont terminés, RTdroid et le programme utilisateur sont mis au courant avec les résultats des calculs et la faisabilitée de l'exécution temps réél de l'appli
public class ThreadCapteur extends Thread
{

	//permet l'utilisation simplifié des capteurs
	private CapteurManager capteurManager;
	//le programme utilisateur (pour le calcul du WCET et pour le prévenir une fois les calculs terminé)
	private RTRunnable programmeUtilisateur;
	//permet de prévenir rtdroid une fois les calculs terminés
	private RTDroid rtdroid;
	//la période demand par l'utilisateur (0 si vitesse maximal)
	private Long periodeDemande;
	//le thread où l'application va être lancé, permet le calcul du WCET coté API
	private RTMainThread mainThread;
	
	//la progression pour envoyer le pourcentage d'avancement à la couche graphique
	private int progression;

	//constructeur
	public ThreadCapteur(CapteurManager mgr, RTRunnable programmeUtilisateur, RTDroid rtdroid, Long periodeDemande, RTMainThread mainThread)
	{
		capteurManager = mgr;
		this.programmeUtilisateur = programmeUtilisateur;
		this.rtdroid = rtdroid;
		this.periodeDemande = periodeDemande; 
		this.mainThread = mainThread;

	}

	//fonction lancé dans un thread
	public void run()
	{

		//on test les capteurs
		Long maxCapteurWait = testCapteur();
		//on calcul le wcet du programme utilisateur
		Long wcetUtilisateur = getWcetUtilisateur();
		//on calcul le wcet de l'api
		Long wcetAPI = getWcetApi(maxCapteurWait);
	
		Long total = wcetAPI + wcetUtilisateur;
	
		
		//maintenant que l'on a toutes les données, on les dispache à rtdroid et au programme utilisateur
		
		//si la periode demandé est possible
		if (periodeDemande >=  maxCapteurWait + total)
		{
			rtdroid.endConfiguration(true, periodeDemande, wcetAPI, periodeDemande-maxCapteurWait-wcetAPI, maxCapteurWait);
			programmeUtilisateur.endConfiguration(true, periodeDemande, total);
		}
		else if (periodeDemande == 0) // si l'utilisateur demande la vitesse maximale
		{
			rtdroid.endConfiguration(true, maxCapteurWait+total, wcetAPI, total-wcetAPI, maxCapteurWait);
			programmeUtilisateur.endConfiguration(true, maxCapteurWait + total, total);
		}
		else // configuration impossible !
		{
			rtdroid.endConfiguration(false, 0l, 0l, 0l, 0l);
			programmeUtilisateur.endConfiguration(false, 0l, total);
		}
	}
	
	//calcul et retourne le plus grand temps de réponse des capteurs utilisés
	private Long testCapteur()
	{
		//on commence par dire qu'on va tester les capteurs à la couche graphique
		programmeUtilisateur.progressConfiguration(0, 0);
		
		//on se met en mode mesure
		capteurManager.startMesure();
		//pour chaque capteur
		do
		{
			Log.d("TEST_CAPTEUR", "test du capteur n°" + capteurManager.getCurrentCapteur().getId() + " : " + capteurManager.getCurrentCapteur().getName());
			
			//tant que le capteur n'as pas donné de valeur à l'API
			while (capteurManager.getCurrentCapteur().getMaxPeriode() == 0l)
			{
				Log.d("TEST_CAPTEUR", "aucune donnée, on continu");
				//on attend un certain temps
				try
				{
					Thread.sleep(Tools.MAX_WAIT_WCET_CAPTEUR);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					break;
				}
			}
		}
		while (capteurManager.nextCapteur());
		
		//on retourne la periode la plus grande qui va nous permettre de calculer la fréquance maximale
		return capteurManager.getPeriodeMax();
	}
	
	//calcul et retourne le WCET du programme utilisateur
	public Long getWcetUtilisateur()
	{
		
		//on prépare les valeurs à envoyer au programme utilisateur
		ArrayList<Capteur> listeCapteurUtilise = capteurManager.getListeCapteurUtilise();
		ArrayList<CapteurValue> listeCapteurValue = new ArrayList<CapteurValue>();
		for (int i = 0; i < listeCapteurUtilise.size();++i)
		{
			listeCapteurValue.add(listeCapteurUtilise.get(i).getCapteurValue());
		}
		Long total = 0l;
		progression = -1;
		programmeUtilisateur.progressConfiguration(1, 0);
		//on test plusieurs fois pour trouver le temps maximale.
		for (int i = 0; i < Tools.NB_ITER_WCET_UTIL; ++i)
		{
			//on génère des valeurs aléatoires pour envoyer au programme utilisateur 
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
			//on envoie si besoin le nouveau pourcentage à la couche graphique
			if ((int)((i*100)/ Tools.NB_ITER_WCET_UTIL) != progression)
			{
				progression = (int)((i*100)/ Tools.NB_ITER_WCET_UTIL);
				programmeUtilisateur.progressConfiguration(1, progression);
			}
		}
		return total;
	}
	
	//calcul et retourne le WCET de la partie API
	//paramètre maxCapteurWait est le temps qu'il faut pour être sûr que tout les capteurs ont répondu au moins une fois
	public Long getWcetApi(Long maxCapteurWait)
	{
		Long wcetAPI = 0l;
		progression = -1;
		programmeUtilisateur.progressConfiguration(2, 0);
		//on fait la mesure du temps plusieurs fois et on garde le maximum
		for (int i = 0; i < Tools.NB_ITER_WCET_API; ++i)
		{
			Long tmp = mainThread.voidRun(maxCapteurWait);
			if (wcetAPI < tmp)
			{
				wcetAPI = tmp;
			}
			//on envoie si besoin le nouveau pourcentage à la couche graphique
			if ((int)((i*100)/ Tools.NB_ITER_WCET_API) != progression)
			{
				progression = (int)((i*100)/ Tools.NB_ITER_WCET_API);
				programmeUtilisateur.progressConfiguration(2, progression);
			}
		}
		//on a tout fini on affiche 100%
		programmeUtilisateur.progressConfiguration(2, 100);
	
		
		return wcetAPI;
	}
	
}
