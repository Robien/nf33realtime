/**
 * 
 */
package nf33.realtime.testcapteur;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

}
