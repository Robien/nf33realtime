package nf33.realtime.testcapteur;


import java.util.ArrayList;

import nf33.realtime.apirtdroid.Capteur;
import nf33.realtime.apirtdroid.RTDroid;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TestCapteurActivity extends Activity 
{

    private Button _boutonExe = null;
    private Button _boutonConfig = null;
    private ProgressDialog _chargement = null;
    private EditText _frequenceDem = null;
    private TextView texte = null;
    private TextView texteinfogeneral = null;
    private TextView textebasgeneral = null;
    
    //programme utilisateur et API
    private ProgrammeUtilisateur programmeUtilisateur;
    private RTDroid _rtdroid;
    
    //etat de l'application
    private boolean configure = false;
    //etat de l'application
    private boolean isStarted = false;
    
    //etat de la barre de progression
    private int progression = 0;


   
      // Gère les communications avec le thread de utilisateur
    final private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {	
            super.handleMessage(msg);
            String message = (String) msg.obj;
            switch (msg.arg2)
            {
            case ProgrammeUtilisateur.MESSAGE_PERIODE:
                if(configure)
                {
                	texteinfogeneral.setText(message + msg.arg1);
                }
                break;
            case ProgrammeUtilisateur.MESSAGE_RECORD:
                texte.setText(message);
                textebasgeneral.setText("Moyen précision : " + msg.arg1);
                break;
            case ProgrammeUtilisateur.MESSAGE_FINCONFIG:
            	 //_chargement.dismiss();
            	if(msg.arg1 == 0)
            	{
            		Toast.makeText(getApplicationContext(), "La periode est impossible", Toast.LENGTH_LONG ).show();
                    allowConfiguration();
            	}
            	else
            	{
            		_boutonExe.setEnabled(true);
                    configure = true;
                    texteinfogeneral.setText(message);
            	}
                
                break;
            }
        }
    };
   

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

    	Log.d("DADU", "Lancement de l'application");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        //Ajout des boutons
        _boutonExe = (Button) findViewById(R.id.boutonExe);
        _boutonExe.setOnClickListener(new BoutonStart());
        _boutonExe.setEnabled(false);
        _boutonConfig = (Button) findViewById(R.id.boutonConfig);
        _boutonConfig.setOnClickListener(new BoutonConfig());
       
        
        //edit texte pour recuperer la valeur de la frequence
        _frequenceDem = (EditText) findViewById(R.id.numEditFre);
        _frequenceDem.setText("0");
        //zone de texte
        texte = (TextView)findViewById(R.id.infocapteurs);
        texteinfogeneral = (TextView)findViewById(R.id.info);
        textebasgeneral = (TextView)findViewById(R.id.infogeneral);
        
        //zone de chargement
        _chargement = new ProgressDialog(this);
        
        //activer la configuration
        allowConfiguration();
        //API
        _rtdroid = new RTDroid(this);
        
        //Programme Utilisateur
        programmeUtilisateur = new ProgrammeUtilisateur(_rtdroid, mHandler);

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



    @Override
    protected void onStop()
    {
        super.onStop();
        if (isStarted)
        {
            isStarted = _rtdroid.stop();
        }
    }

    public Resources getActivityResources()
    {
        return getResources();
    }
   

    public class BoutonStart implements View.OnClickListener
    {

		@Override
		public void onClick(View v)
		{
			 //capteurManager.testAuto();
	        if (isStarted)
	        {
	            isStarted = _rtdroid.stop();
	            texteinfogeneral.setText("");
	            allowConfiguration();
	            
	        }
	        else
	        {
	            isStarted = _rtdroid.launch();
	            texteinfogeneral.setText("");
	            
	            _boutonConfig.setEnabled(false);
	        }
	       
	        if (isStarted)
	        {
	            _boutonExe.setText(R.string.boutonStop);
	        }
	        else
	        {
	            _boutonExe.setText(R.string.boutonStart);
	        }
	       
			
		}
    
    };
    
    public void allowConfiguration()
    {
    	 texteinfogeneral.setText("");
         _boutonConfig.setEnabled(true);
         _boutonExe.setEnabled(false);
         configure = false;
    }
    
    public void selectionCapteur(long freqDemande)
    {	
    	
         ArrayList<Capteur> listeCapteur = new ArrayList<Capteur>();
         int i = 0;
         while(i < _rtdroid.getCapteurManager().getListeCapteurs().size() )
         {
             if(_rtdroid.getCapteurManager().getListeCapteurs().get(i).getSensor().getType() == 1)
             {
                 Log.d("DADU", "Capteur choisie : ("+ _rtdroid.getCapteurManager().getListeCapteurs().get(i).getName() + ") : " + _rtdroid.getCapteurManager().getListeCapteurs().get(i).getSensor().getType());
                 listeCapteur.add(_rtdroid.getCapteurManager().getListeCapteurs().get(i));
             }
             ++i;
         }
         Log.d("DADU", "debut declare");
        _rtdroid.declare(programmeUtilisateur, listeCapteur, freqDemande);
    }
    
    public class BoutonConfig implements View.OnClickListener
    {

		@Override
		public void onClick(View v)
		{
	        if (!configure)
	        {
	        	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	        	imm.hideSoftInputFromWindow(_frequenceDem.getWindowToken(), 0);
				texteinfogeneral.setText("Configuration...");
				_chargement.setMessage("Configuration en cours");
				_boutonConfig.setEnabled(false);
				// lancer configuration
				
				float valeur;
				try
				{
					valeur = Float.valueOf(_frequenceDem.getText().toString());
				}
				catch (NumberFormatException e)
				{
					_frequenceDem.setText("1000");
					valeur = 1000;
				}

				if (valeur >= 0)
				{
					selectionCapteur((long) (1000000 * valeur));
				}
				else
				{
					Toast.makeText(getApplicationContext(), "La periode est impossible", Toast.LENGTH_LONG ).show();
                    allowConfiguration();
				}
				//_chargement.show();
	        }
	
		}
    
    };
   
};


