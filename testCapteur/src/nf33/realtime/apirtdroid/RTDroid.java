/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;


/**
 * @author Seg_fault_
 * 
 */
public final class RTDroid
{

	
	private Activity activity;
	private CapteurManager capteurManager;
	
	private Runnable runable; // a changer par un tableau pour pouvoir appeller
								// plusieurs méthodes
	//private ArrayList<Capteur> capteursUtilise; // a changer pour un tableau de
												// tableau pour gérer des
												// capteurs pour chaque runable

	public RTDroid(Activity activity)
	{
		capteurManager = new CapteurManager(activity);
		//capteursUtilise = new ArrayList<Capteur>();
		this.activity = activity;
	}

	public Boolean declare(Runnable runnable, List<Capteur> listCapteurs)
	{

		this.runable = runnable;
		//a changer pour ne pas mettre 2 fois le capteur dans le tableau
//		for (Capteur capteur : listCapteurs)
//		{
//			capteursUtilise.add(capteur);
//		}
		for (Capteur capteur : listCapteurs)
		{
			capteur.setIsUsed(true);
		}
		
		
		
		
		return null;
	}

	public Boolean run()
	{

		return false;
	}

	public Boolean stop()
	{

		return false;
	}

	public CapteurManager getCapteurManager()
	{
		return this.capteurManager;
	}
	
}
