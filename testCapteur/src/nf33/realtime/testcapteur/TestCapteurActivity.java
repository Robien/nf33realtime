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
    private TextView texteInfoCentre = null;
    private TextView texteprecision = null;
    private TextView texteInfoBas = null;
    
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
                	texteprecision.setText(message);
                	double moyen = (double)msg.arg1/(double)10000000;
                	texteInfoBas.setText("Moyen précision : " + moyen);
                }
                break;
            case ProgrammeUtilisateur.MESSAGE_CAPTEUR:
            	if(configure)
                {
            		texteInfoCentre.setText(message);
                }
                break;
            case ProgrammeUtilisateur.MESSAGE_PROGRESS:
            	if(!configure)
                {
            		_chargement.setMessage(message);
            		_chargement.setProgress(msg.arg1);
                }
                break;
            case ProgrammeUtilisateur.MESSAGE_ENDEXETEST:
            	if(configure)
            	{
            		isStarted = _rtdroid.stop();
    	            allowConfiguration();
    	            texteInfoBas.setText(message);
            	}
            	break;
            case ProgrammeUtilisateur.MESSAGE_FINCONFIG:
            	 _chargement.dismiss();
            	if(msg.arg1 == 0)
            	{
            		Toast.makeText(getApplicationContext(), "La période demandée est impossible à satisfaire", Toast.LENGTH_LONG ).show();
                    allowConfiguration();
            	}
            	else
            	{
            		_boutonExe.setEnabled(true);
                    configure = true;
                    texteprecision.setText(message);
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
        texteInfoCentre = (TextView)findViewById(R.id.infogeneral);
        texteprecision = (TextView)findViewById(R.id.infoprecision);
        texteInfoBas = (TextView)findViewById(R.id.infocomplementaire);
        
        //zone de chargement
        _chargement = new ProgressDialog(this);
        //changement du style 
        _chargement.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _chargement.setTitle("Configuration");
        _chargement.setMax(100);
        
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
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isStarted)
        {
            isStarted = _rtdroid.stop();
            texteprecision.setText("");
            allowConfiguration();
        }
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
	            allowConfiguration();
	            
	        }
	        else
	        {
	            isStarted = _rtdroid.launch();
	            texteprecision.setText("");
	            
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
    	 texteprecision.setText("Entrez une période en milliseconde puis configuez");
    	 texteInfoCentre.setText("");
    	 texteInfoBas.setText("");
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
				texteprecision.setText("");
		    	 texteInfoCentre.setText("");
		    	 texteInfoBas.setText("");
				_chargement.setMessage("Configuration en cours...");
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
				_chargement.show();
	        }
	
		}
    
    };
   
};


