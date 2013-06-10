package nf33.realtime.testcapteur;


import java.util.ArrayList;

import nf33.realtime.apirtdroid.Capteur;
import nf33.realtime.apirtdroid.RTDroid;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestCapteurActivity extends Activity implements View.OnClickListener
{

	private Button b = null;
	private TextView texte = null;
	private TextView texteinfogeneral = null;
	private TextView texterecord = null;
	private int record = 0;
	private Boolean isStarted = false;
	private ProgrammeUtilisateur programmeUtilisateur;
	private RTDroid rtdroid;
	
	  // Gère les communications avec le thread de utilisateur
	  final private Handler mHandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	      super.handleMessage(msg);
	      String message = (String)msg.obj;
	      if(message.contains("période"))
	      {
	    	  texteinfogeneral.setText(message + msg.arg1 );
	      }
	      else
	      {
	    	  texte.setText(message + msg.arg1);
	    	  if(record< msg.arg1)
	    	  {
	    		  record = msg.arg1;
	    		  texterecord.setText("record : " + record);
	    	  }
	      }
	    }
	  };
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		

		Log.d("DADU", "onCreat begin");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ajout d'un listener sur le bouton
		b = (Button) findViewById(R.id.bouton);
		
		texte = (TextView)findViewById(R.id.infocapteurs);
		texteinfogeneral = (TextView)findViewById(R.id.info);
		texterecord = (TextView)findViewById(R.id.record);
		b.setOnClickListener(this);
		rtdroid = new RTDroid(this);
		programmeUtilisateur = new ProgrammeUtilisateur(rtdroid, mHandler);
		
		ArrayList<Capteur> listeCapteur = new ArrayList<Capteur>();
		
		int i = 0;
		while(i < rtdroid.getCapteurManager().getListeCapteurs().size() && rtdroid.getCapteurManager().getListeCapteurs().get(i).getSensor().getType() != SensorManager.SENSOR_ACCELEROMETER)
		{
			++i;
		}
		if (i < rtdroid.getCapteurManager().getListeCapteurs().size())
		{
			// ajoute le capteur
			listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(i));
			Log.d("DADU", "Capteur choisie ("+ SensorManager.SENSOR_ACCELEROMETER+") : "+rtdroid.getCapteurManager().getListeCapteurs().get(i).getSensor().getType());
		}
		else
		{
			Log.d("DADU", "Aucun accelerometre trouvé !!");
			// ajoute le capteur
			listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(2));
		}
		
		
		rtdroid.declare(programmeUtilisateur, listeCapteur, 1000000000l);
		

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.d("DADU", "onREsume begin");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	public void onClick(View v)
	{
		//capteurManager.testAuto();
		if (isStarted)
		{
			isStarted = rtdroid.stop();
		}
		else
		{
			isStarted = rtdroid.launch();
		}
		
		if (isStarted)
		{
			b.setText(R.string.boutonStop);
		}
		else
		{
			b.setText(R.string.boutonStart);
		}
		

	}


	public Resources getActivityResources()
	{
		return getResources();
	}
	

}
