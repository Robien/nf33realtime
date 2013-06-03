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

	private Long maxDurationCapteurEtExe;
	private Long maxDurationExecution;

	private Boolean isPossible;
	
	public RTDroid(Activity activity)
	{
		capteurManager = new CapteurManager(activity);
		//capteursUtilise = new ArrayList<Capteur>();
		this.activity = activity;
		configurationEnCours = 0;
		maxDurationCapteurEtExe = 0l;
		maxDurationExecution = 100000000l;
	}

	public Boolean declare(RTRunnable runnable, List<Capteur> listCapteurs, Long periodeDemande)
	{

		this.runnable = runnable;
		//a changer pour ne pas mettre 2 fois le capteur dans le tableau
//		for (Capteur capteur : listCapteurs)
//		{
//			capteursUtilise.add(capteur);
//		}
		for (Capteur capteur : listCapteurs)
		{
			capteur.setIsUsed(true);
		}
		
		mutexConfigurationEnCours = new ReentrantLock();
		
		mutexConfigurationEnCours.lock();
		configurationEnCours++;
		mutexConfigurationEnCours.unlock();
		threadCapteur = new ThreadCapteur(capteurManager, runnable, this, periodeDemande);
		threadCapteur.start();
		return null;
	}

	public Boolean launch()
	{
		mutexConfigurationEnCours.lock();
		if (configurationEnCours > 0 || !isPossible)
		{
			mutexConfigurationEnCours.unlock();
			return false;
		}
		mutexConfigurationEnCours.unlock();
		
		 // creation du thread principal RTMainThread
		_threadPrincipal = new RTMainThread(runnable, capteurManager, true);
		 // configuration du temps de capteur max
		_threadPrincipal.set_maxDurationCapteur(maxDurationCapteurEtExe - maxDurationExecution);
		 // configuration du temps de exe max
		_threadPrincipal.set_maxDurationExe(maxDurationExecution);
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
	
	public void endConfiguration(Boolean isPossible, Long periode, Long wcet)
	{
		mutexConfigurationEnCours.lock();
		configurationEnCours--;
		mutexConfigurationEnCours.unlock();
		this.isPossible = isPossible;
		Log.d("DADU", "valeur de la période : " + (periode));
		maxDurationCapteurEtExe = periode;
		maxDurationExecution = wcet;
		Log.d("DADU", "WCET = " + wcet);
	}
	
	
	
}
