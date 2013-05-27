package nf33.realtime.apirtdroid;
import java.util.ArrayList;

import android.util.Log;

/**
 * 
 */

/**
 * @author Arselle
 *
 */
public class Logs
{
	//Fichier journalLog
	private Fichier journalLog;
	
	Logs() 
	{
		journalLog = new Fichier("Journal Logs", true);
	}
	
	
	//Afficher des logs
	public void affiche_log(String texte)
	{
		Log.d("DetDroid",texte);
	}
	
	
	
	//Enregistrer des logs dans un fichier
	public void savelog(ArrayList<String> tabLogs)
	{
		int i;
		
		try
		{ 
			journalLog.openFile();	//ouverture du fichier en mode écriture
			
			for(i=0; i<tabLogs.size(); i++)
			{
				journalLog.write(tabLogs.get(i));	//ecriture du log à la suite du fichier
			}
			
			journalLog.close();	//fermer le fichier après écriture
				
			}catch (Exception e) { 
				e.getMessage(); 
			} 
		    	finally { journalLog.close(); }
		}
}
