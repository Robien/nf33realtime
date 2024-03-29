package nf33.realtime.apirtdroid;
import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;


/**
 * @author Arselle
 *
 */
public class Logs
{
	//Fichier journalLog
	private Fichier journalLog;
	private ThreadLog LastThread = null;
	
	//constructeur
	Logs() 
	{
		//cr�ation du ficher d'ecriture de log
		journalLog = new Fichier("Journal Logs", true);
		//ouverture du fichier
		journalLog.openFile();
	}
	
	
	//Afficher des logs
	public void affiche_log(String texte)
	{
		//affichage de logCat
		Log.d("DetDroid",texte);
	}
	
	public void threaded_write(String log)
	{
		//ecriture dans le fichier dans un thread
		LastThread = new ThreadLog(log+ "\n");
		LastThread.start();
	}
	
	public void write(String log)
	{
		//ecriture dans le fichier 
		journalLog.write(Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + " - " + log + "\n");
	}
	
	public void closeLog()
	{
		try
		{
			LastThread.join();
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			Log.d("DetDroid","Erreur join Thread ecriture");
			e.printStackTrace();
		}
		journalLog.close();
	}
	
	
	//Enregistrer des logs envoyer dans l'array dans le fichier fichier
	@Deprecated
	public void savelog(ArrayList<String> tabLogs)
	{
		int i;
		
		try
		{ 
			for(i=0; i<tabLogs.size(); i++)
			{
				journalLog.write(tabLogs.get(i));	//ecriture du log � la suite du fichier
			}
				
			}catch (Exception e) { 
				e.getMessage(); 
			} 

		}
	
	public class ThreadLog extends Thread {
		  // La phrase � chercher dans le texte
		  public String log = "";

		  public ThreadLog(String newlog)
		  {
			  super();
			  log = newlog;
		  }
		  public void run() {
			  journalLog.write(Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + " - " + log);
		  }
		}
}
