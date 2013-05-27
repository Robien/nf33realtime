package nf33.realtime.apirtdroid;

import android.util.Log;

/**
 * @author Seg_fault_
 * 
 */
public class RTMainThread extends Thread
{
	//Conversion de Nanoseconde a Milliseconde
	final long convertNanoToMilli = 1000000;
	
	//durée maximal de recuperation des capteurs (en nano)
	private long _maxDurationCap;
	//durée maximal d'execution de la fonction utilisateur  (en nano)
	private long _maxDurationExe;
	//Classe de calcul utilisateur à executer
	private RTRunnable _runnable;
		
	public RTMainThread(RTRunnable _runnable)
	{
		super();
		this._runnable = _runnable;
	}


	public void run()
	{

		long beginTimeExe = 0; 								//stock le temps du début de l'execution  (en nano)
		long endTimeExe = 0;								//stock le temps de la fin de l'execution  (en nano)
		long dateCap = 0;									//stock le temps de recuperation des capteurs  (en nano)
		long needSleep =0; 									//temps de sleep  (en milli)
		long thisPeriode = 0; 								//temps entre les deux dernier execution  (en nano)
		
		endTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
		Log.d("DADU", "1");
		while(true)
		{
			Log.d("DADU", "2");
			//Attente de la fin de la periode
			try
			{
				Thread.sleep(_maxDurationCap/convertNanoToMilli);	//attend la fin de la fenetre de capture des données capteurs
			}
			catch (InterruptedException e)
			{
				Log.e("DADU", "execption sleep capteur" );
				e.printStackTrace();
			}
			
			Log.d("DADU", "3");
			dateCap = System.nanoTime(); 					//recupere le temps en nanoseconde
			//recuperation des données capteurs
			//TODO

			
			beginTimeExe = System.nanoTime(); 				//recupere le temps en nanoseconde
			thisPeriode = beginTimeExe - endTimeExe; 			//calcul de la periode exacte 
			Log.d("DADU", "periode : " +thisPeriode + "ns = " + (thisPeriode/1000000000) + "s");
			//Appel de la methode à executer 
			_runnable.periodicEvent(thisPeriode);	
			endTimeExe = System.nanoTime();					//recupere le temps en nanoseconde 
			
			Log.d("DADU", "4");
			needSleep = (long) ((_maxDurationExe-((endTimeExe - beginTimeExe))/((float)convertNanoToMilli))); //calcul du temps d'execution réel de la methode
			Log.d("DADU", "5 : " + needSleep);
			if(needSleep<0)
			{
				//erreur, execution plus long que prévu
			}
			
			//Attente de la fin de la periode d'execution
			try
			{
				Thread.sleep(needSleep);					//attend jusqu'a la fin de la durée max d'exe
			}
			catch (InterruptedException e)
			{
				Log.e("DADU", "execption sleep execution" );
				e.printStackTrace();
			}
			Log.d("DADU", "6");
		
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
