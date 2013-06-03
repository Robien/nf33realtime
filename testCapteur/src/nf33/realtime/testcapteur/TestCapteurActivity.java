package nf33.realtime.testcapteur;


import java.util.ArrayList;

import nf33.realtime.apirtdroid.Capteur;
import nf33.realtime.apirtdroid.RTDroid;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestCapteurActivity extends Activity implements View.OnClickListener
{

	private Button b = null;
	private Boolean isStarted = false;
	private ProgrammeUtilisateur programmeUtilisateur;
	private RTDroid rtdroid;
	

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		

		Log.d("DADU", "onCreat begin");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ajout d'un listener sur le bouton
		b = (Button) findViewById(R.id.bouton);
		b.setOnClickListener(this);
		rtdroid = new RTDroid(this);
		programmeUtilisateur = new ProgrammeUtilisateur(rtdroid);
		
		ArrayList<Capteur> listeCapteur = new ArrayList<Capteur>();
		
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(2));
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(0));
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(1));
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(3));
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(4));
		listeCapteur.add(rtdroid.getCapteurManager().getListeCapteurs().get(5));
		
		rtdroid.declare(programmeUtilisateur, listeCapteur, 0l);
		

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
