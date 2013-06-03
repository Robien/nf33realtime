package nf33.realtime.apirtdroid;

import java.util.ArrayList;

import android.util.Log;

/**
 * @author Seg_fault_
 * 
 */
public class RTMainThread extends Thread
{
	//Conversion de Nanoseconde a Milliseconde
	static final float convertNanoToMilli = 1000000;
	
	//dur�e maximal de recuperation des capteurs (en nano)
	private long _maxDurationCap;
	//dur�e maximal d'execution de la fonction utilisateur  (en nano)
	private long _maxDurationExe;
	//Classe de calcul utilisateur � executer
	private RTRunnable _runnable;
	//sauvegarde des log activ�
	private boolean _logActived;
	//sauvegarde des log activ�
	private Logs _log;
	//type de precision
	private boolean _nanoAccuracy;
	
	private CapteurManager _capteurManager;
		
	public RTMainThread(RTRunnable _runnable, CapteurManager capteurManager)
	{
		super();
		this._runnable = _runnable;
		_logActived = false;
		_nanoAccuracy = true;
		_log = null;
		_capteurManager = capteurManager;
	}

	public RTMainThread(RTRunnable _runnable, CapteurManager capteurManager, boolean activelog)
	{
		super();
		this._runnable = _runnable;
		_logActived = activelog;
		_nanoAccuracy = true;
		if(_logActived)
		{
			_log = new Logs();
		}
		else
		{
			_log = null;
		}
		_capteurManager = capteurManager;
	}

	public void run()
	{

		long beginTimeExe = 0; 								//stock le temps du d�but de l'execution  (en nano)
		long endTimeExe = 0;								//stock le temps de la fin de l'execution  (en nano)
		long dateCap = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long needSleep =0; 									//temps de sleep  (en milli)
		long thisPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		
		
		endTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde

		if(_logActived)
		{
			_log.threaded_write("Debut initialisation : " + endTimeExe + " dur�e max capteur : " + _maxDurationCap + " dur�e max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
			_log.affiche_log("Debut initialisation : " + endTimeExe +  " dur�e max capteur : " + _maxDurationCap + " dur�e max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
		}
		
		ArrayList<CapteurValue> capteursValues = new ArrayList<CapteurValue>();
		ArrayList<Capteur> capteurUtilise = _capteurManager.getListeCapteurUtilise();
		
		for (Capteur capteur : capteurUtilise)
		{
			capteursValues.add(new CapteurValue());
		}
		
		_capteurManager.startCaptureCapteur();
		
		//debut de l'execution du code utulisateur
		while(true)
		{
			//Attente de la fin de la periode
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(toMilli(_maxDurationCap),remainderNano(_maxDurationCap));	//attend la fin de la fenetre de capture des donn�es capteurs
				}
				else
				{
					Thread.sleep(toMilli(_maxDurationCap));	//attend la fin de la fenetre de capture des donn�es capteurs
				}
			}
			catch (InterruptedException e)
			{
				if(_logActived)
				{
					_log.threaded_write("End execution, "+  System.nanoTime());
					_log.affiche_log("End execution"+  System.nanoTime());
					_log.closeLog();
				}
				
				break; //stop the Thread
			}
			

			dateCap = System.nanoTime(); 					//recupere le temps en nanoseconde
			if(_logActived)
			{
				_log.threaded_write("Date capteur : " +timeToString(dateCap) );
				_log.affiche_log("Date capteur : " +timeToString(dateCap) );
			}
			//recuperation des donn�es capteurs
			for (int i = 0; i < capteurUtilise.size(); i++)
			{
				if (capteurUtilise.get(i).getLastSensorEvent() == null)
				{
					Log.d("DADU", "4.5 : " + i + " DONNE NON TROUVE !");
					
				}
				capteursValues.get(i).setTimestampCaptureAnd(capteurUtilise.get(i).getLastSensorEvent().timestamp);
				capteursValues.get(i).setTimestampCaptureApi(dateCap);
				capteursValues.get(i).setValues(capteurUtilise.get(i).getLastSensorEvent().values);
			}
			
			beginTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
			thisPeriode = beginTimeExe - endTimeExe; 			//calcul de la periode exacte 
			if(_logActived)
			{
				_log.threaded_write("Periode : " +timeToString(thisPeriode) + " precision : "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
				_log.affiche_log("Periode : " +timeToString(thisPeriode) + " precision "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
			}
			
			//Appel de la methode � executer 
			_runnable.periodicEvent(thisPeriode, capteursValues);	
			endTimeExe = System.nanoTime();					//recupere le temps en nanoseconde de la fin de lexecution
			

			needSleep = _maxDurationExe-(endTimeExe - beginTimeExe) ; //calcul du temps de sleep necessaire pour compenser le gigue
			if(needSleep<0)//erreur, execution plus long que pr�vu
			{
				if(_logActived)
				{
					_log.threaded_write("Erreur, execution plus long que pr�vu : " + timeToString(endTimeExe - beginTimeExe));
					_log.affiche_log("Erreur, execution plus long que pr�vu" + timeToString(endTimeExe - beginTimeExe));
				}
				needSleep = 0;
			}
			
			//Attente de la fin de la periode d'execution
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(toMilli(needSleep),remainderNano(needSleep));	//attend la fin de la fenetre de capture des donn�es capteurs
				}
				else
				{
					Thread.sleep(toMilli(needSleep));					//attend jusqu'a la fin de la dur�e max d'exe
				}
				
			}
			catch (InterruptedException e)
			{
				if(_logActived)
				{
					_log.threaded_write("End execution"+  System.nanoTime());
					_log.threaded_write("End execution"+  System.nanoTime());
					_log.closeLog();
				}
				
				
				break; //stop the thread
			}
		
		}
		_capteurManager.stopCapteurCapteur();
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
	
	static private String timeToString(long nano)
	{
		return "(" + toMilli(nano) + "ms" + remainderNano(nano) + "ns)";
	}
	
	public synchronized void set_logActived(boolean activelog)
	{
		_logActived = activelog;
		if(_logActived)
		{
			_log = new Logs();
		}
		else if(_log != null)
		{
			_log.closeLog();
			_log = null;
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
