package nf33.realtime.apirtdroid;

import java.util.ArrayList;

import android.util.Log;
import nf33.realtime.apirtdroid.Tools;

/**
 * @author Seg_fault_
 * 
 */
public class RTMainThread extends Thread
{

	
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

		long beginTimeExe = 0; 								//stock le temps du début de l'execution  (en nano)
		long endTimeExe = 0;								//stock le temps de la fin de l'execution  (en nano)
		long dateCap = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long needSleep =0; 									//temps de sleep  (en milli)
		long thisPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		
		

		if(_logActived)
		{
			_log.threaded_write("Debut initialisation : " + endTimeExe + " durée max capteur : " + _maxDurationCap + " durée max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
			_log.affiche_log("Debut initialisation : " + endTimeExe +  " durée max capteur : " + _maxDurationCap + " durée max execution : " + _maxDurationExe + "Precision nano : " + _nanoAccuracy);
		}
		
		ArrayList<CapteurValue> capteursValues = new ArrayList<CapteurValue>();
		ArrayList<Capteur> capteurUtilise = _capteurManager.getListeCapteurUtilise();
		
		for (Capteur capteur : capteurUtilise)
		{
			capteursValues.add(new CapteurValue());
		}
		
		_capteurManager.startCaptureCapteur();
		
		endTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
		
		//debut de l'execution du code utulisateur
		while(true)
		{
			//Attente de la fin de la periode
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(Tools.toMilli(_maxDurationCap),Tools.remainderNano(_maxDurationCap));	//attend la fin de la fenetre de capture des données capteurs
				}
				else
				{
					Thread.sleep(Tools.toMilli(_maxDurationCap));	//attend la fin de la fenetre de capture des données capteurs
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
				_log.threaded_write("Date capteur : " +Tools.timeToString(dateCap) );
				_log.affiche_log("Date capteur : " +Tools.timeToString(dateCap) );
			}
			//recuperation des données capteurs
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
				_log.threaded_write("Periode : " +Tools.timeToString(thisPeriode) + " precision : "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
				_log.affiche_log("Periode : " +Tools.timeToString(thisPeriode) + " precision "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
			}
			
			//Appel de la methode à executer 
			_runnable.periodicEvent(thisPeriode, capteursValues);	
			endTimeExe = System.nanoTime();					//recupere le temps en nanoseconde de la fin de lexecution
			

			needSleep = _maxDurationExe-(endTimeExe - beginTimeExe) ; //calcul du temps de sleep necessaire pour compenser le gigue
			if(needSleep<0)//erreur, execution plus long que prévu
			{
				if(_logActived)
				{
					_log.threaded_write("Erreur, execution plus long que prévu : " + Tools.timeToString(endTimeExe - beginTimeExe));
					_log.affiche_log("Erreur, execution plus long que prévu" + Tools.timeToString(endTimeExe - beginTimeExe));
				}
				needSleep = 0;
			}
			
			//Attente de la fin de la periode d'execution
			try
			{
				if(_nanoAccuracy)
				{
					Thread.sleep(Tools.toMilli(needSleep),Tools.remainderNano(needSleep));	//attend la fin de la fenetre de capture des données capteurs
				}
				else
				{
					Thread.sleep(Tools.toMilli(needSleep));					//attend jusqu'a la fin de la durée max d'exe
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
	
	//Simulation du code API, retourne le temps necessaire
	public long voidRun()
	{
		long beginTimeExe = 0; 								//stock le temps du début de l'execution  (en nano)
		long endTimeExe = 0;								//stock le temps de la fin de l'execution  (en nano)
		long gettime = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long needSleep =0; 									//temps de sleep  (en milli)
		long thisPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		
		ArrayList<CapteurValue> capteursValues = new ArrayList<CapteurValue>();
		ArrayList<Capteur> capteurUtilise = _capteurManager.getListeCapteurUtilise();
		
		for (Capteur capteur : capteurUtilise)
		{
			capteursValues.add(new CapteurValue());
		}
		
		_capteurManager.startCaptureCapteur();
		try
		{
			Thread.sleep(Tools.toMilli(_maxDurationCap));
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //attend qu'il y ai au moins une valeur
		
		//recupere temps de début de la fonction à simuler
		beginTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
		
		//Simulation  d'execution
		if(true)
		{
			
			if(_nanoAccuracy)
			{
				Tools.toMilli(_maxDurationCap);
				Tools.remainderNano(_maxDurationCap);
			}
			else
			{
				Tools.toMilli(_maxDurationCap);
			}

			gettime = System.nanoTime(); 					//recupere le temps en nanoseconde
			if(_logActived)
			{
				_log.threaded_write("Calculfonc - Date capteur : " +Tools.timeToString(gettime) );
				_log.affiche_log("Calculfonc - Date capteur : " +Tools.timeToString(gettime) );
			}
			//Simulation  : recuperation des données capteurs
			for (int i = 0; i < capteurUtilise.size(); i++)
			{
				if (capteurUtilise.get(i).getLastSensorEvent() == null)
				{
					Log.d("DADU", "Calculfonc - 4.5 : " + i + " DONNE NON TROUVE !");
				}
				capteursValues.get(i).setTimestampCaptureAnd(capteurUtilise.get(i).getLastSensorEvent().timestamp);
				capteursValues.get(i).setTimestampCaptureApi(gettime);
				capteursValues.get(i).setValues(capteurUtilise.get(i).getLastSensorEvent().values);
				capteursValues.get(i).setType(capteurUtilise.get(i).getSensor().getType());
			}
			
			gettime = System.nanoTime(); 				//recupere le temps en nanoseconde
			thisPeriode = gettime - beginTimeExe; 			//calcul de la periode exacte 
			if(_logActived)
			{
				_log.threaded_write("Calculfonc - Periode : " +Tools.timeToString(thisPeriode) + " precision : "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
				_log.affiche_log("Calculfonc - Periode : " +Tools.timeToString(thisPeriode) + " precision "+ ((double)thisPeriode/(double)(_maxDurationCap+_maxDurationExe)));
			}
			
				
			endTimeExe = System.nanoTime();					//Simulation  : recupere le temps en nanoseconde de la fin de lexecution
			

			needSleep = _maxDurationExe-(endTimeExe - gettime) ; //Simulation  : calcul du temps de sleep necessaire pour compenser le gigue
			if(needSleep<0)//Simulation  : erreur, execution plus long que prévu
			{
				if(_logActived)
				{
					
					_log.threaded_write("Calculfonc - Erreur, execution plus long que prévu : " + Tools.timeToString(endTimeExe - gettime));
					_log.affiche_log("Calculfonc - Erreur, execution plus long que prévu" + Tools.timeToString(endTimeExe - gettime));
				}
				needSleep = 0;
			}
			
			//Simulation  : Attente de la fin de la periode d'execution

				if(_nanoAccuracy)
				{
					Tools.toMilli(needSleep);
					Tools.remainderNano(needSleep);
				}
				else
				{
					Tools.toMilli(needSleep);
				}

		}
		//recuperation du temps de la fin de la fonction a simuler
		endTimeExe = System.nanoTime();
		_capteurManager.stopCapteurCapteur();
		return endTimeExe-beginTimeExe;
	}
}
