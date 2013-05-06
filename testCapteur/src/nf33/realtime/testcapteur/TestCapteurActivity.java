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

public class TestCapteurActivity extends Activity implements View.OnClickListener
{

	private Button b = null;
	private TextView text = null;
	private Boolean isStarted = false;
	private Sensor accelerometer = null;
	private long lastTimestamp = 0;
	private long delais = 0;
	
	private CapteurManager capteurManager;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		

		Log.d("DADU", "onCreat begin");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// affichage du message du capteur
//		Resources res = getResources();
//		String chaine = res.getString(R.string.information, delais);
//		TextView vue = (TextView) findViewById(R.id.info);
//		vue.setText(chaine);

		// ajout d'un listener sur le bouton
		b = (Button) findViewById(R.id.bouton);
		b.setOnClickListener(this);

		
		capteurManager = new CapteurManager(this);
		

		// recuperation de la liste des capteurs
//		Log.d("DADU", "onCreat - demande de sensor manager");
		

		// ecriture des capteurs geré
//		text = (TextView) findViewById(R.id.infocapteurs);
//		text.setText(capteurManager.getListeCapteursTexte());

//		Log.d("DADU", "onCreat liste write ok");

//		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		if (accelerometer == null)
//		{
//			Toast.makeText(this, "aucun accelerometre", Toast.LENGTH_LONG).show();
//			Log.e("DADU", "Aucun accelerometre");
//			b.setEnabled(false);
//		}
		
		
		Log.d("DADU", "onCreat end");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d("DADU", "onREsume begin");
//		capteurManager.startMesure();
//		Log.d("DADU", "onREsume end");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		capteurManager.stopMesure();
	}

	public void onClick(View v)
	{
	
		if (isStarted == false)
		{
			capteurManager.setIdCapteurCourant(0);
			TextView vue = (TextView) findViewById(R.id.info);
			vue.setText((capteurManager.getCourantId()+1) + "/" + capteurManager.getListeCapteurs().size() + " : " +  capteurManager.getListeCapteurs().get(capteurManager.getCourantId()).getName());
			isStarted = true;
			b.setText(R.string.boutonStop);
			capteurManager.startMesure();
		}
		else
		{
			capteurManager.stopMesure();
			if (capteurManager.nextCapteur())
			{
				TextView vue = (TextView) findViewById(R.id.info);
				vue.setText((capteurManager.getCourantId()+1) + "/" + capteurManager.getListeCapteurs().size() + " : " +  capteurManager.getListeCapteurs().get(capteurManager.getCourantId()).getName());
				capteurManager.startMesure();
			}
			else
			{
				isStarted = false;
				b.setText(R.string.boutonStart);
				TextView vue = (TextView) findViewById(R.id.info);
				vue.setText("");
			}
		}
		

	}

//	private void start()
//	{
//		Log.d("DADU", "start : demande de capteur");
//		fichier.openFile();
//		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//		isStarted = true;
//	}
//
//	private void stop()
//	{
//		Log.d("DADU", "stop : " + isStarted.toString());
//		sensorManager.unregisterListener(this, accelerometer);
//		isStarted = false;
//		fichier.close();
//	}

//	public void onAccuracyChanged(Sensor sensor, int accuracy)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	public void onSensorChanged(SensorEvent event)
//	{
//
////		delais = event.timestamp - lastTimestamp;
////		lastTimestamp = event.timestamp;
////		Resources res = getResources();
////		String chaine = new String("delais : " + delais + "ns");
////		TextView vue = (TextView) findViewById(R.id.info);
////		vue.setText(chaine);
////		fichier.write(chaine);
//	}

	
	public SensorManager getSensorManager()
	{
		return (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	}

	public Resources getActivityResources()
	{
		return getResources();
	}
	
	public void newMax(long max)
	{
		TextView vue = (TextView) findViewById(R.id.info);
		vue.setText((capteurManager.getCourantId()+1) + "/" + capteurManager.getListeCapteurs().size() + " : " +  capteurManager.getListeCapteurs().get(capteurManager.getCourantId()).getName() + " Max : " + max);

	}

}
