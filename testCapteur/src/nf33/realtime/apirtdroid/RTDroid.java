/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.MutableContextWrapper;


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
	ThreadCapteur threadCapteur;

	public RTDroid(Activity activity)
	{
		capteurManager = new CapteurManager(activity);
		//capteursUtilise = new ArrayList<Capteur>();
		this.activity = activity;
		configurationEnCours = 0;
	}

	public Boolean declare(RTRunnable runnable, List<Capteur> listCapteurs)
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
		threadCapteur = new ThreadCapteur(capteurManager, runnable);
		threadCapteur.start();
		return null;
	}

	public Boolean run()
	{
		mutexConfigurationEnCours.lock();
		if (configurationEnCours > 0)
		{
			mutexConfigurationEnCours.unlock();
			return false;
		}
		mutexConfigurationEnCours.unlock();
		
		//mettre ici le thread principal
		
		return true;
	}

	public Boolean stop()
	{

		return false;
	}

	public CapteurManager getCapteurManager()
	{
		return this.capteurManager;
	}
	
	public void enConfiguration()
	{
		mutexConfigurationEnCours.lock();
		configurationEnCours--;
		mutexConfigurationEnCours.unlock();
	}
	
}
