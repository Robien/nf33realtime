package nf33.realtime.apirtdroid;

import android.util.Log;

/**
 * @author Seg_fault_
 * 
 */
public class RTMainThread extends Thread
{
	//Conversion de Nanoseconde a Milliseconde
	static final float convertNanoToMilli = 1000000;
	
	//durée maximal de recuperation des capteurs (en nano)
	private long _maxDurationCap;
	//durée maximal d'execution de la fonction utilisateur  (en nano)
	private long _maxDurationExe;
	//Classe de calcul utilisateur à executer
	private RTRunnable _runnable;
	//sauvegarde des log activé
	private boolean _logActived;
	//sauvegarde des log activé
	private Logs _log;
	//type de precision
	private boolean _nanoAccuracy;
		
	public RTMainThread(RTRunnable _runnable)
	{
		super();
		this._runnable = _runnable;
		_logActived = false;
		_nanoAccuracy = true;
	}

	public RTMainThread(RTRunnable _runnable, boolean activelog)
	{
		super();
		this._runnable = _runnable;
		_logActived = activelog;
		_nanoAccuracy = true;
		if(_logActived)
		{
			_log = new Logs();
		}
	}

	public void run()
	{

		long beginTimeExe = 0; 								//stock le temps du début de l'execution  (en nano)
		long endTimeExe = 0;								//stock le temps de la fin de l'execution  (en nano)
		long dateCap = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long needSleep =0; 									//temps de sleep  (en milli)
		long thisPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		
		
		endTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde

		if(_logActived)
		{
			_log.threaded_write("Debut initialisation : " + endTimeExe + " durée max capteur : " + _maxDurationCap + " durée max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
			_log.affiche_log("Debut initialisation : " + endTimeExe +  " durée max capteur : " + _maxDurationCap + " durée max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
		}
		
		
		//debut de l'execution du code utulisateur
		while(true)
		{
			//Attente de la fin de la periode
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(toMilli(_maxDurationCap),remainderNano(_maxDurationCap));	//attend la fin de la fenetre de capture des données capteurs
				}
				else
				{
					Thread.sleep(toMilli(_maxDurationCap));	//attend la fin de la fenetre de capture des données capteurs
				}
			}
			catch (InterruptedException e)
			{
				if(_logActived)
				{
					_log.threaded_write("End execution");
					_log.affiche_log("End execution");
				}
				_log.closeLog();
				break; //stop the Thread
			}
			

			dateCap = System.nanoTime(); 					//recupere le temps en nanoseconde
			if(_logActived)
			{
				_log.threaded_write("Date capteur : " +dateCap + " ns");
				_log.affiche_log("Date capteur : " +dateCap + " ns");
			}
			//recuperation des données capteurs
			//TODO

			
			beginTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
			thisPeriode = beginTimeExe - endTimeExe; 			//calcul de la periode exacte 
			if(_logActived)
			{
				_log.threaded_write("Periode : " +thisPeriode + " ns");
				_log.affiche_log("Periode : " +thisPeriode + "ns");
			}
			
			//Appel de la methode à executer 
			_runnable.periodicEvent(thisPeriode);	
			endTimeExe = System.nanoTime();					//recupere le temps en nanoseconde de la fin de lexecution
			

			needSleep = _maxDurationExe-(endTimeExe - beginTimeExe) ; //calcul du temps de sleep necessaire pour compenser le gigue
			if(needSleep<0)//erreur, execution plus long que prévu
			{
				if(_logActived)
				{
					_log.threaded_write("Erreur, execution plus long que prévu : " + (endTimeExe - beginTimeExe));
					_log.affiche_log("Erreur, execution plus long que prévu" + (endTimeExe - beginTimeExe));
				}
				needSleep = 0;
			}
			
			//Attente de la fin de la periode d'execution
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(toMilli(needSleep),remainderNano(needSleep));	//attend la fin de la fenetre de capture des données capteurs
				}
				else
				{
					Thread.sleep(toMilli(needSleep));					//attend jusqu'a la fin de la durée max d'exe
				}
				
			}
			catch (InterruptedException e)
			{
				if(_logActived)
				{
					_log.threaded_write("End execution");
					_log.threaded_write("End execution");
				}
				_log.closeLog();
				
				break; //stop the thread
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
	
	static private long toMilli(long nano)
	{
		return (long)(nano/convertNanoToMilli);
	}
	
	//donne le reste de nanosecond sans les milli
	static private int remainderNano(long nano)
	{
		return (int)(nano%convertNanoToMilli);
	}
	
	public synchronized void set_logActived(boolean activelog)
	{
		_logActived = activelog;
		if(_logActived)
		{
			_log = new Logs();
		}
	}
	
	public boolean get_logActived()
	{
		return _logActived;
	}

	public boolean is_nanoAccuracy()
	{
		return _nanoAccuracy;
	}

	public synchronized void set_nanoAccuracy(boolean nanoAccuracy)
	{
		this._nanoAccuracy = nanoAccuracy;
	}
	
}
