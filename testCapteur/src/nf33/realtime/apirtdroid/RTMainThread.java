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
	//frequence attendu
	private long frequenceAttendu;
	//Classe de calcul utilisateur à executer
	private RTRunnable _runnable;
	//sauvegarde des log activé
	private boolean _logActived;
	//permet de bloquer l'attente de compensation
	private boolean _noWait;
	//fichier de log
	private Logs _log;

	
	private CapteurManager _capteurManager;
		
	//constructeur
	public RTMainThread(RTRunnable _runnable, CapteurManager capteurManager)
	{
		super();
		this._runnable = _runnable;
		_logActived = false;
		_noWait = false;

		_log = null;
		_capteurManager = capteurManager;
	}

	//constructeur
	public RTMainThread(RTRunnable _runnable, CapteurManager capteurManager, boolean activelog)
	{
		super();
		this._runnable = _runnable;
		_logActived = activelog;

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

	//fonction principal du thread
	public void run()
	{
		//initialisation
		long debutExeUtil = 0; 								//stock le temps du début de l'execution  (en nano)
		long finExeUtil = 0;								//stock le temps de la fin de l'execution  (en nano)
		long dateCap = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long lastPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		long debutPeriode = 0;
		long tempsCompensation = 0;

		if(_logActived)
		{
			_log.threaded_write("-----------Fichier log RTDroid-----------\n--Date début" + System.nanoTime() + "--\n--type d'attente" + Tools.typeWaitToString() + "--\n Attente compensation : " + !_noWait + "--\n");
			_log.threaded_write(" durée max capteur : " + _maxDurationCap + "\nfréquence execution : " + frequenceAttendu);
			_log.affiche_log(" durée max capteur : " + _maxDurationCap + " fréquence execution : " + frequenceAttendu);
		}
		
		//Liste de capteur
		ArrayList<CapteurValue> capteursValues = new ArrayList<CapteurValue>();
		ArrayList<Capteur> capteurUtilise = _capteurManager.getListeCapteurUtilise();
		
		//ajout des capteurs dans la liste
		for (Capteur capteur : capteurUtilise)
		{
			capteursValues.add(new CapteurValue());
		}
		
		//Début de capture des capteurs
		_capteurManager.startCaptureCapteur();
		
		//initialisation du temps de debut de periode
		debutPeriode = System.nanoTime(); 				//recupere le temps en nanoseconde
		//debut de l'execution du code utulisateur
		while(true)
		{
			//Attente de la fin de la periode de capture
			try
			{
				Tools.waitTime(_maxDurationCap);//attend la fin de la fenetre de capture des données capteurs
			}
			catch (InterruptedException e)
			{
				break; //stop the Thread
			}
			
			//date de capture des capteurs
			dateCap = System.nanoTime(); 					//recupere le temps en nanoseconde
			if(_logActived) //log
			{
				_log.threaded_write("Date capteur : " +Tools.timeToString(dateCap) );
				_log.affiche_log("Date capteur : " +Tools.timeToString(dateCap) );
			}
			
			//recuperation des données capteurs
			for (int i = 0; i < capteurUtilise.size(); i++)
			{
				if(capteurUtilise.get(i).getLastSensorEvent() == null && _logActived) //log
				{
					_log.threaded_write("Aucune données capteur : "+ capteurUtilise.get(i).getName() );
					_log.affiche_log("Aucune données capteur : "+ capteurUtilise.get(i).getName() );
				}
				capteursValues.get(i).setTimestampCaptureAnd(capteurUtilise.get(i).getLastSensorEvent().timestamp);
				capteursValues.get(i).setTimestampCaptureApi(dateCap);
				capteursValues.get(i).setValues(capteurUtilise.get(i).getLastSensorEvent().values);
				capteursValues.get(i).setType(capteurUtilise.get(i).getSensor().getType());
			}
			
			if(_logActived)//log
			{
				_log.threaded_write("Periode : " +Tools.timeToString(lastPeriode) + " precision : "+ ((double)lastPeriode/(double)(frequenceAttendu)));
				_log.affiche_log("Periode : " +Tools.timeToString(lastPeriode) + " precision "+ ((double)lastPeriode/(double)(frequenceAttendu)));
			}
			
			//debut de l'exécution du code utilisateur
			debutExeUtil = System.nanoTime();	
			//Appel de la methode à exécuter : parametre : dernier periode, valeurs des capteurs
			_runnable.periodicEvent(lastPeriode, capteursValues);

			//fin de l'execution du code utilisateur
			finExeUtil = System.nanoTime();					
			if(!_noWait)
			{
				//calcul du temps necessaire pour finir la periode
				tempsCompensation = frequenceAttendu - (finExeUtil - debutPeriode); 
				
				if(tempsCompensation<0)//erreur, execution plus long que prévu
				{
					if(_logActived)
					{
						_log.threaded_write("Erreur, exécution code util. trop long : " + Tools.timeToString(finExeUtil - debutExeUtil) + "au lieu de : "+ Tools.timeToString(frequenceAttendu-_maxDurationCap));
						_log.affiche_log("Erreur, exécution code util. trop long :" + Tools.timeToString(finExeUtil - debutExeUtil)+ "au lieu de : "+ Tools.timeToString(frequenceAttendu-_maxDurationCap));
					}
					tempsCompensation  = 0;
				}
				
				//Attente de la fin de la periode d'execution
				try
				{
					 Tools.waitTime(tempsCompensation);
				}
				catch (InterruptedException e)
				{
					break; //stop the thread
				}
			}
			//calcul de la periode reel de la dernier boucle
			lastPeriode = System.nanoTime() - debutPeriode;
			//debut de la nouvelle periode
			debutPeriode = System.nanoTime(); 				//recupere le temps en nanoseconde
		
		}
		
		if(_logActived)
		{
			_log.threaded_write("End execution, "+  System.nanoTime());
			_log.affiche_log("End execution"+  System.nanoTime());
			_log.closeLog();
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
	public synchronized void setFrequenceAttendu(long frequenceAttendu)
	{
		this.frequenceAttendu = frequenceAttendu;
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

	public boolean isNoWait()
	{
		return _noWait;
	}

	public synchronized void setNoWait(boolean noWait)
	{
		this._noWait = noWait;
	}

	// Simulation du code API, retourne le temps necessaire (WCET-API)
	public long voidRun(long maxCapteurwait)
	{
		// initialisation des variable necessaire à la fonction
		long debutExeUtil = 0; // stock le temps du début de l'execution (en nano)
		long finExeUtil = 0; // stock le temps de la fin de l'execution (en nano)
		long dateCap = 0; // stock le temps de recuperation des capteurs (en nano)
		long lastPeriode = 0; // temps entre les deux dernier execution (en nano)
		long debutPeriode = 0; // debut de la nouvelle periode (en nano)
		long tempsCompensation = 0; // variable pour contenir le temps de sleep necessaire (en nano)
		// initialisation variable pour le calcul du temps d'exectution
		long endTimeExe = 0;
		long beginTimeExe = 0;
		// Liste de capteur
		ArrayList<CapteurValue> capteursValues = new ArrayList<CapteurValue>();
		ArrayList<Capteur> capteurUtilise = _capteurManager.getListeCapteurUtilise();

		// ajout des capteurs dans la liste
		for (Capteur capteur : capteurUtilise)
		{
			capteursValues.add(new CapteurValue());
		}

		// Début de capture des capteurs
		_capteurManager.startCaptureCapteur();

		try
		{
			// attent qu'une valeur soit recuperé
			Tools.waitTime(maxCapteurwait);
		}
		catch (InterruptedException e)
		{
			Log.d("DADU", "voidRun Erreur de sleep");
			e.printStackTrace();
		} // attend qu'il y ai au moins une valeur

		// initialisation du temps de debut de periode
		debutPeriode = System.nanoTime();
		beginTimeExe = System.nanoTime();
		// Simulation d'execution
		if (true)
		{
			// Attente de la fin de la periode de capture
			Tools.simulewaitTime(maxCapteurwait);

			// date de capture des capteurs
			dateCap = System.nanoTime(); // recupere le temps en nanoseconde
			if (_logActived) // log
			{
				_log.threaded_write("/INIT/Date capteur : " + Tools.timeToString(dateCap));
				_log.affiche_log("/INIT/Date capteur : " + Tools.timeToString(dateCap));
			}

			// recuperation des données capteurs
			for (int i = 0; i < capteurUtilise.size(); i++)
			{
				if (capteurUtilise.get(i).getLastSensorEvent() == null && _logActived) // log
				{
					_log.threaded_write("/INIT/Aucune données capteur : " + capteurUtilise.get(i).getName());
					_log.affiche_log("/INIT/Aucune données capteur : " + capteurUtilise.get(i).getName());
				}
				else
				{
					capteursValues.get(i).setTimestampCaptureAnd(capteurUtilise.get(i).getLastSensorEvent().timestamp);
					capteursValues.get(i).setTimestampCaptureApi(dateCap);
					capteursValues.get(i).setValues(capteurUtilise.get(i).getLastSensorEvent().values);
					capteursValues.get(i).setType(capteurUtilise.get(i).getSensor().getType());
				}
			}

			if (_logActived)// log
			{
				_log.threaded_write("/INIT/Periode : " + Tools.timeToString(lastPeriode) + " precision : "
						+ ((double) lastPeriode / (double) (frequenceAttendu)));
				_log.affiche_log("/INIT/Periode : " + Tools.timeToString(lastPeriode) + " precision "
						+ ((double) lastPeriode / (double) (frequenceAttendu)));
			}

			// debut de l'exécution du code utilisateur
			debutExeUtil = System.nanoTime();
			/*
			 * Code ne devant pas être executé //Appel de la methode à exécuter
			 * : parametre : dernier periode, valeurs des capteurs
			 * _runnable.periodicEvent(lastPeriode, capteursValues);
			 */

			// fin de l'execution du code utilisateur
			finExeUtil = System.nanoTime();
			if (!_noWait)
			{
				// calcul du temps necessaire pour finir la periode
				tempsCompensation = frequenceAttendu - (finExeUtil - debutPeriode);

				if (tempsCompensation < 0)// erreur, execution plus long que
											// prévu
				{
					if (_logActived)
					{
						_log.threaded_write("/INIT/");
						_log.affiche_log("/INIT/");
					}
					tempsCompensation = 0;
				}

				// Attente de la fin de la periode d'execution
				//Tools.simulewaitTime(tempsCompensation);

			}
			// calcul de la periode reel de la dernier boucle
			lastPeriode = System.nanoTime() - debutPeriode;
			// debut de la nouvelle periode
			debutPeriode = System.nanoTime(); 

		}
		// recuperation du temps de la fin de la fonction a simuler
		endTimeExe = System.nanoTime();
		_capteurManager.stopCapteurCapteur();
		return endTimeExe - beginTimeExe;

	}
}
