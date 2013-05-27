/**
 * 
 */
package nf33.realtime.apirtdroid;

import java.util.ArrayList;
import java.util.List;

import nf33.realtime.testcapteur.TestCapteurActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.ViewDebug.CapturedViewProperty;

/**
 * @author Romain
 * 
 */
public class CapteurManager implements SensorEventListener
{

	private Activity activity;

	private SensorManager sensorManager;
	private Capteur capteurCourant = null;
	private long lastTimestamp = 0;

	private String listeCapteursTexte;
	private List<Capteur> capteurs;
	private ArrayList<ArrayList<Long>> delaisCapteurs;
	private ArrayList<Long> delaisCapteursMax;

	private int idCapteurCourant = 0;

	private Fichier fichier;

	public CapteurManager(Activity activity)
	{
		this.activity = activity;

		fichier = new Fichier("maxSauv.dat", false);
		boolean getFromSauv = fichier.openFileLecture();

		delaisCapteursMax = new ArrayList<Long>();
		delaisCapteurs = new ArrayList<ArrayList<Long>>();

		Log.d("CapteurManager", "onCreat - demande de sensor manager");
		sensorManager = getSensorManager();
		Log.d("CapteurManager", "onCreat sensor manager ok");
		List<Sensor> listCapteurs = sensorManager.getSensorList(Sensor.TYPE_ALL);
		Log.d("CapteurManager", "onCreat liste recuperé : " + listCapteurs.size());

		capteurs = new ArrayList<Capteur>();
		listeCapteursTexte = "liste des capteurs : \n";
		int i = 0;
		for (Sensor sensor : listCapteurs)
		{
			i++;
			capteurs.add(new Capteur(i, sensor));
			listeCapteursTexte += sensor.getName() + "\n";
			delaisCapteurs.add(new ArrayList<Long>());
			if (getFromSauv && fichier.read().equals(sensor.getName()))
			{
				delaisCapteursMax.add(Long.valueOf(fichier.read()));
			}
			else
			{
				delaisCapteursMax.add(Long.valueOf(0));
			}
		}
		if (getFromSauv)
		{
			fichier.closeReader();
		}

		Log.d("CapteurManager", "onCreat liste write ok");

		capteurCourant = capteurs.get(idCapteurCourant);
		if (!capteurCourant.isUsed())
		{
			nextCapteur();
		}

	}

	public String getListeCapteursTexte()
	{
		return listeCapteursTexte;
	}

	public List<Capteur> getListeCapteurs()
	{
		return capteurs;
	}

	public Boolean nextCapteur()
	{
		if (idCapteurCourant + 1 < capteurs.size())
		{
			setIdCapteurCourant(idCapteurCourant + 1);
			if (!capteurCourant.isUsed())
			{
				return nextCapteur();
			}
			return true;
		}
		else
		{
			fichier = new Fichier("maxSauv.dat", false);
			fichier.delete();
			fichier = new Fichier("maxSauv.dat", false);
			fichier.openFile();

			for (int i = 0; i < delaisCapteursMax.size(); i++)
			{
				fichier.write(capteurs.get(i).getSensor().getName() + "\n");
				fichier.write(delaisCapteursMax.get(i) + "\n");
			}
			fichier.close();

			return false;
		}
	}

	public void setIdCapteurCourant(int id)
	{
		idCapteurCourant = id;
		capteurCourant = capteurs.get(idCapteurCourant);
	}

	public int getCourantId()
	{
		return idCapteurCourant;
	}

	public void startMesure()
	{
		if (activity != null)
		{
//			activity.newMax(delaisCapteursMax.get(idCapteurCourant));
		}
		start();
	}

	public void stopMesure()
	{
		stop();
	}

	public ArrayList<ArrayList<Long>> getDelaisCapteurs()
	{
		return delaisCapteurs;
	}

	public ArrayList<Long> getDelaisCapteursMax()
	{
		return delaisCapteursMax;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{

		long delais = event.timestamp - lastTimestamp;
		lastTimestamp = event.timestamp;
		delaisCapteurs.get(idCapteurCourant).add(delais);
		if (delais > delaisCapteursMax.get(idCapteurCourant))
		{
			delaisCapteursMax.set(idCapteurCourant, delais);
			capteurCourant.setMaxPeriode(delais);
//			activity.newMax(delais);
		}
		 Log.d("MESURE", delais + "");
		// String chaine = new String("delais : " + delais + "ns");
		// fichier.write(chaine);
	}

	private void start()
	{
		setIdCapteurCourant(0);
		Log.d("DADU", "start : demande de capteur numéro " + capteurCourant.getId());
		// fichier.openFile();
		sensorManager.registerListener(this, capteurCourant.getSensor(), SensorManager.SENSOR_DELAY_NORMAL);
	}

	private void stop()
	{
		Log.d("DADU", "stop");
		sensorManager.unregisterListener(this, capteurCourant.getSensor());
		// fichier.close();
	}

	public void testAuto()
	{
		setIdCapteurCourant(0);
		for (int i = 10;; i = i * 2)
		{
			while (nextCapteur())
			{
				if (activity != null)
				{
//					activity.newMax(delaisCapteursMax.get(idCapteurCourant));
				}

				for (int j = 0; j < 1000000; ++j)
				{

				}

				// try
				// {
				// Thread.sleep(i);
				// }
				// catch (InterruptedException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		}
	}
	public SensorManager getSensorManager()
	{
		return (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
	}
	public Long getPeriodeMax()
	{
		Long max = 0l;
		for (Capteur capteur : capteurs)
		{
			if (max < capteur.getMaxPeriode())
			{
				max = capteur.getMaxPeriode();
			}
		}
		
		return max;
	}
	
}
