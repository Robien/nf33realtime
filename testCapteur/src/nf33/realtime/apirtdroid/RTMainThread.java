package nf33.realtime.apirtdroid;

/**
 * @author Seg_fault_
 * 
 */
public class RTMainThread extends Thread
{
	final long convertNanoToMilli = 1000000;
	
	//dur�e maximal de recuperation des capteurs
	private long _maxDurationCap;
	//dur�e maximal d'execution de la fonction utilisateur
	private long _maxDurationExe;
	//Classe de calcul utilisateur � executer
	private RTRunnable _runnable;
		
	public RTMainThread(RTRunnable _runnable)
	{
		super();
		this._runnable = _runnable;
	}


	public void run()
	{

		long beginTimeExe = 0; 								//stock le temps du d�but de l'execution
		long endTimeExe = 0;								//stock le temps de la fin de l'execution
		long dateCap = 0;									//stock le temps de recuperation des capteurs
		long needSleep =0; 									//temps de sleep
		long thisPeriode = 0; 								//temps entre les deux dernier execution
		
		while(true)
		{
			//Attente de la fin de la periode
			try
			{
				Thread.sleep(_maxDurationCap);				//attend la fin de la fenetre de capture des donn�es capteurs
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			dateCap = System.nanoTime(); 					//recupere le temps en nanoseconde
			//recuperation des donn�es capteurs
			//TODO

			
			beginTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
			thisPeriode = endTimeExe-beginTimeExe; 			//calcul de la periode exacte 
			//Appel de la methode � executer 
			_runnable.periodicEvent(thisPeriode);	
			endTimeExe = System.nanoTime();					//recupere le temps en nanoseconde 
			
			needSleep = _maxDurationExe-((beginTimeExe-endTimeExe)*convertNanoToMilli); //calcul du temps d'execution r�el de la methode
			if(needSleep<0)
			{
				//erreur, execution plus long que pr�vu
			}
			
			//Attente de la fin de la periode d'execution
			try
			{
				Thread.sleep(needSleep);					//attend jusqu'a la fin de la dur�e max d'exe
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		
		}
	}
	
	
	public long get_maxDurationCapteur()
	{
		return _maxDurationCap;
	}

	public synchronized void set_maxDurationCapteur(long _maxDurationCapteur)
	{
		this._maxDurationCap = _maxDurationCapteur;
	}

	public long get_maxDurationExe()
	{
		return _maxDurationExe;
	}

	public synchronized void set_maxDurationExe(long _maxDurationExe)
	{
		this._maxDurationExe = _maxDurationExe;
	}
	

}
