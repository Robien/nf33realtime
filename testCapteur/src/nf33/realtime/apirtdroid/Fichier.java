/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import android.os.Environment;

/**
 * @author Romain
 * 
 */
public class Fichier
{

	private BufferedReader reader;
	private BufferedWriter writer;
	private String name;
	private Logs log;

	//constructeur 
	Fichier(String name, boolean addDate)
	{
		if (!addDate)
		{
			this.name = name;
		}
		else
		{
			this.name = name + "-" + Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + ".txt";
		}
	}
	
	//constructeur par défault
	Fichier()
	{
		this.name = "data-" + Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + ".txt";
	}
	
	//suppression du fichier
	public boolean delete()
	{
		File newfile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + name);
		if (newfile.exists())
		{
			newfile.delete();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	//ouverture du fichier en mode ecriture
	public void openFile()
	{
		try
		{
			File newfile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + name);
			newfile.createNewFile();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfile)));

		}
		catch (Exception e)
		{
			log.affiche_log("Erreur à l'ouverture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à l'ouverture : " + e.getMessage());
		}

	}
	
	//ouverture du fichier en mode lecture
	public Boolean openFileLecture()
	{
		try
		{
			
			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			File newfile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + name);
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(newfile)));
			return newfile.exists();
			
		}
		catch (Exception e)
		{
			log.affiche_log("Erreur à l'ouverture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à l'ouverture : " + e.getMessage());
		}
		return false;
	}

	//ecriture dans le ficher
	public void write(String data)
	{

		try
		{
			writer.write(data);
		}
		catch (IOException e)
		{
			log.affiche_log("Erreur à l'écriture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à l'écriture : " + e.getMessage());
		}
	}
	
	//lecture dans le fichier
	public String read()
	{
		
		try
		{
			String r = reader.readLine(); 
			if (r == null)
			{
				return "";
			}
			return r;
		}
		catch (IOException e)
		{
			log.affiche_log("Erreur à l'écriture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à l'écriture : " + e.getMessage());
		}
		return "";
	}
	
	//fermeture du fichier du mode ecriture
	public void close()
	{

		try
		{
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			log.affiche_log("Erreur à la fermeture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à la fermeture : " + e.getMessage());
		}
	}
	
	//fermeture du mode de lecture
	public void closeReader()
	{
		
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			log.affiche_log("Erreur à la fermeture : " + e.getMessage());
			//Log.d("Fichier", "Erreur à la fermeture : " + e.getMessage());
		}
	}
}
