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
	private Boolean isTestingMaxPeriod;

	public CapteurManager(Activity activity)
	{
		isTestingMaxPeriod = true;
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
			Capteur capteurTmp = new Capteur(i, sensor);
			capteurs.add(capteurTmp);
			capteurTmp.setName(sensor.getName());
			listeCapteursTexte += sensor.getName() + "\n";
			Log.d("CAPTEURS", sensor.getType() +  "");
			delaisCapteurs.add(new ArrayList<Long>());
			if (getFromSauv && fichier.read().equals(sensor.getName()))
			{
				Long tmpLong = Long.valueOf(fichier.read());
				delaisCapteursMax.add(tmpLong);
				capteurTmp.setMaxPeriode(tmpLong);
			}
			else
			{
				delaisCapteursMax.add(Long.valueOf(0));
				capteurTmp.setMaxPeriode(Long.valueOf(0));
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
		stop();
		if (idCapteurCourant + 1 < capteurs.size())
		{
			setIdCapteurCourant(idCapteurCourant + 1);
			if (!capteurCourant.isUsed())
			{
				return nextCapteur();
			}
			start();
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
			// activity.newMax(delaisCapteursMax.get(idCapteurCourant));
		}
		setIdCapteurCourant(0);
		//TODO : et si 0 n'est pas utilisé ?
		start();
	}
	public void startCaptureCapteur()
	{
		isTestingMaxPeriod = false;
		for (Capteur capteur : capteurs)
		{
			if (capteur.isUsed())
			{
				sensorManager.registerListener(this, capteur.getSensor(), SensorManager.SENSOR_DELAY_FASTEST);
				Log.d("DADU", "Register n°" + capteur.getId());
			}
		}
	}
	
	public void stopCapteurCapteur()
	{
		for (Capteur capteur : capteurs)
		{
			if (capteur.isUsed())
			{
				sensorManager.unregisterListener(this, capteur.getSensor());
				Log.d("DADU", "unregister n°" + capteur.getId());
			}
		}
		isTestingMaxPeriod = true;
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

		if (isTestingMaxPeriod)
		{
			if (delais > delaisCapteursMax.get(idCapteurCourant))
			{
				delaisCapteursMax.set(idCapteurCourant, delais);
				capteurCourant.setMaxPeriode(delais);
				// activity.newMax(delais);
			}
			Log.d("MESURE", delais + "");
		}
		else
		{
//			Log.d("MESURE", event.values[0] + "");
			for (Capteur capteur : capteurs)
			{
				if (event.sensor == capteur.getSensor())
				{
					capteur.setLastSensorEvent(event);
				}
			}
		}
		// String chaine = new String("delais : " + delais + "ns");
		// fichier.write(chaine);
	}

	private void start()
	{

		// fichier.openFile();
		sensorManager.registerListener(this, capteurCourant.getSensor(), SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void stop()
	{
		Log.d("DADU", "stop");
		sensorManager.unregisterListener(this, capteurCourant.getSensor());
		// fichier.close();
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
			if (capteur.isUsed())
			{
				Log.d("DADU", "CapteurManager : capteur type : " + capteur.getSensor().getType());
				if (max < capteur.getMaxPeriode())
				{
					max = capteur.getMaxPeriode();
				}
			}
		}

		return max;
	}

	public Capteur getCurrentCapteur()
	{
		return capteurs.get(idCapteurCourant);
	}
	
	public ArrayList<Capteur> getListeCapteurUtilise()
	{
		ArrayList<Capteur> listCateurs = new ArrayList<Capteur>();
		for (Capteur capteur : capteurs)
		{
			if (capteur.isUsed())
			{
				listCateurs.add(capteur);
				Log.d("DADU", "getListeCapteurUtilise id :" + capteur.getId());
			}
		}
		return listCateurs;
	}
	
}
