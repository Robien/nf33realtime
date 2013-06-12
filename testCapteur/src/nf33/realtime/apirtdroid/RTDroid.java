/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.MutableContextWrapper;
import android.util.Log;
import android.widget.Toast;


/**
 * @author Seg_fault_
 * 
 */
public final class RTDroid
{

	private Activity activity;
	private CapteurManager capteurManager;
	
	private RTRunnable runnable; // a changer par un tableau pour pouvoir appeller
								// plusieurs méthodes
	//private ArrayList<Capteur> capteursUtilise; // a changer pour un tableau de
												// tableau pour gérer des
												// capteurs pour chaque runable
	private Integer configurationEnCours;
	private ReentrantLock mutexConfigurationEnCours;
	private ThreadCapteur threadCapteur;
	private RTMainThread _threadPrincipal; //Thread de gestion de l'execution

	private Long periode;
	private  Long wcetAPI;
	private Long wcetUtilisateur;
	private Long maxCapteur;
	

	private Boolean isPossible;
	
	public RTDroid(Activity activity)
	{
		this.activity = activity;
		capteurManager = new CapteurManager(activity);
		//capteursUtilise = new ArrayList<Capteur>();
		configurationEnCours = 0;
	}
	

	public void declare(RTRunnable runnable, List<Capteur> listCapteurs, Long periodeDemande)
	{

		this.runnable = runnable;
		//a changer pour ne pas mettre 2 fois le capteur dans le tableau
//		for (Capteur capteur : listCapteurs)
//		{
//			capteursUtilise.add(capteur);
//		}
		for (Capteur capteur : capteurManager.getListeCapteurs())
		{
			capteur.setIsUsed(false);
		}
		Log.d("DADU", "size liste capteur " + listCapteurs.size());
		for (Capteur capteur : listCapteurs)
		{
			capteur.setIsUsed(true);
			//Log.d("DADU", "boucle capteur : " + capteur.getSensor().getType() + " id : " + capteur.getId());
		}
		
		mutexConfigurationEnCours = new ReentrantLock();
		mutexConfigurationEnCours.lock();
		configurationEnCours++;
		mutexConfigurationEnCours.unlock();
		 // creation du thread principal RTMainThread
		_threadPrincipal = new RTMainThread(runnable, capteurManager, true);
		Tools.type_wait = Tools.WAIT_ACTIVE;
		threadCapteur = new ThreadCapteur(capteurManager, runnable, this, periodeDemande, _threadPrincipal);
		threadCapteur.start();
		
	}

	public Boolean launch()
	{
		mutexConfigurationEnCours.lock();
		if (configurationEnCours > 0 || !isPossible)
		{
			mutexConfigurationEnCours.unlock();
			Log.d("DADU", "Impossible de lancer, is possib : "+ isPossible + " config : " + configurationEnCours);
			return false;
		}
		mutexConfigurationEnCours.unlock();
		
		 // configuration du temps de capteur max
		_threadPrincipal.set_maxDurationCapteur(maxCapteur);
		 // configuration du temps de exe max
		_threadPrincipal.set_maxDurationExe(wcetUtilisateur);
		_threadPrincipal.setFrequenceAttendu(periode);
		 // Lancement du thread principal RTMainThread
		_threadPrincipal.start();
 
		 
		
		
		return true;
	}

	public Boolean stop()
	{
		_threadPrincipal.interrupt();
		return false;
	}

	public CapteurManager getCapteurManager()
	{
		return this.capteurManager;
	}
	
	public void endConfiguration(Boolean isPossible, Long periode, Long wcetAPI, Long wcetUtilisateur, Long maxCapteur)
	{
		Log.d("DADU", "endconfig rtdroid");
		mutexConfigurationEnCours.lock();
		configurationEnCours--;
		mutexConfigurationEnCours.unlock();
		this.isPossible = isPossible;
		Log.d("DADU", "valeur de la période : " + (periode));
		 this.periode = periode;
		 this.wcetAPI = wcetAPI;
		 this.wcetUtilisateur = wcetUtilisateur;
		 this.maxCapteur = maxCapteur;
		 
	}
	
	
	
}
