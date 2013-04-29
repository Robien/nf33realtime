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


	private BufferedWriter writer;

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
		openFile();
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		isStarted = true;
	}

	private void stop()
	{
		Log.d("DADU", "stop : " + isStarted.toString());
		sensorManager.unregisterListener(this, accelerometer);
		isStarted = false;
		close();
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
		write(chaine);
	}

	public void openFile()
	{
		try
		{

			
			
			//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			File newfile = new File(Environment.getExternalStorageDirectory().getPath()  + File.separator
					+ "data-" + Calendar.getInstance().getTime().toGMTString().replace(' ', '-').replace(':', '-') + ".txt");

			newfile.createNewFile();

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newfile)));

		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), "fichier pas ouvert : " + Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_SHORT)
					.show();
			Toast.makeText(getApplicationContext(),e.getMessage() , Toast.LENGTH_SHORT)
			.show();
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
			Toast.makeText(getApplicationContext(), "data " + data + "pas enregistré", Toast.LENGTH_SHORT).show();
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
			Toast.makeText(getApplicationContext(), "erreur à la fermeture", Toast.LENGTH_SHORT).show();
		}
	}

}
