package nf33.realtime.testcapteur;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.zip.DataFormatException;

import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestCapteurActivity extends Activity implements View.OnClickListener, SensorEventListener
{

	private Button b = null;
	private TextView text = null;
	private Boolean isStarted = false;
	private SensorManager sensorManager;
	private Sensor accelerometer = null;
	private long lastTimestamp = 0;
	private long delais = 0;


	private Fichier fichier;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		Log.d("DADU", "onCreat begin");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// affichage du message du capteur
		Resources res = getResources();
		String chaine = res.getString(R.string.information, delais);
		TextView vue = (TextView) findViewById(R.id.info);
		vue.setText(chaine);

		// ajout d'un listener sur le bouton
		b = (Button) findViewById(R.id.bouton);
		b.setOnClickListener(this);

		String texte = new String("");
		// recuperation de la liste des capteurs
		Log.d("DADU", "onCreat - demande de sensor manager");
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Log.d("DADU", "onCreat sensor manager ok");

		List<Sensor> liste = sensorManager.getSensorList(Sensor.TYPE_ALL);
		Log.d("DADU", "onCreat liste recuperé : " + liste.size());

		texte = "liste des capteurs : \n";
		for (Sensor sensor : liste)
		{
			texte += sensor.getName() + "\n";
		}

		// ecriture des capteurs gerer
		text = (TextView) findViewById(R.id.infocapteurs);
		text.setText(texte);

		Log.d("DADU", "onCreat liste write ok");

		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (accelerometer == null)
		{
			Toast.makeText(this, "aucun accelerometre", Toast.LENGTH_LONG).show();
			Log.e("DADU", "Aucun accelerometre");
			b.setEnabled(false);
		}
		
		fichier = new Fichier();
		
		Log.d("DADU", "onCreat end");
	}

	@Override
	protected void onResume()
	{
		Log.d("DADU", "onREsume begin");
		super.onResume();

		if (isStarted)
		{
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		}
		Log.d("DADU", "onREsume end");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (isStarted)
		{
			sensorManager.unregisterListener(this, accelerometer);
		}
	}

	public void onClick(View v)
	{
		if (isStarted)
		{
			b.setText(R.string.boutonStart);
			stop();
		}
		else
		{
			b.setText(R.string.boutonStop);
			start();
		}

	}

	private void start()
	{
		Log.d("DADU", "start : demande de capteur");
		fichier.openFile();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		isStarted = true;
	}

	private void stop()
	{
		Log.d("DADU", "stop : " + isStarted.toString());
		sensorManager.unregisterListener(this, accelerometer);
		isStarted = false;
		fichier.close();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

	}

	public void onSensorChanged(SensorEvent event)
	{

		delais = event.timestamp - lastTimestamp;
		lastTimestamp = event.timestamp;
		Resources res = getResources();
		String chaine = new String("delais : " + delais + "ns");
		TextView vue = (TextView) findViewById(R.id.info);
		vue.setText(chaine);
		fichier.write(chaine);
	}



}
