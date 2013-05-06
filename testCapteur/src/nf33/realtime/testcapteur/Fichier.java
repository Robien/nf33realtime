/**
 * 
 */
package nf33.realtime.testcapteur;

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
import android.util.Log;

/**
 * @author Romain
 * 
 */
public class Fichier
{

	private BufferedReader reader;
	private BufferedWriter writer;
	private String name;

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
	
	Fichier()
	{
		this.name = "data-" + Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + ".txt";
	}
	
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
	
	
	public void openFile()
	{
		try
		{

			// SimpleDateFormat dateFormat = new
			// SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			File newfile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + name);
			newfile.createNewFile();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfile)));

		}
		catch (Exception e)
		{
			Log.d("Fichier", "Erreur à l'ouverture : " + e.getMessage());
		}

	}
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
			Log.d("Fichier", "Erreur à l'ouverture : " + e.getMessage());
		}
		return false;
	}

	public void write(String data)
	{

		try
		{
			writer.write(data);
		}
		catch (IOException e)
		{
			Log.d("Fichier", "Erreur à l'écriture : " + e.getMessage());
		}
	}
	public String read()
	{
		
		try
		{
			return reader.readLine();
		}
		catch (IOException e)
		{
			Log.d("Fichier", "Erreur à l'écriture : " + e.getMessage());
		}
		return "";
	}
	

	public void close()
	{

		try
		{
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			Log.d("Fichier", "Erreur à la fermeture : " + e.getMessage());
		}
	}
	public void closeReader()
	{
		
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			Log.d("Fichier", "Erreur à la fermeture : " + e.getMessage());
		}
	}

}
