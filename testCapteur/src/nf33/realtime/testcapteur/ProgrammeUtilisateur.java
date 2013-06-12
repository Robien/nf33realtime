package nf33.realtime.testcapteur;

import java.util.ArrayList;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import nf33.realtime.apirtdroid.CapteurValue;
import nf33.realtime.apirtdroid.RTDroid;
import nf33.realtime.apirtdroid.RTRunnable;

public class ProgrammeUtilisateur implements RTRunnable
{
    private Handler mHandler;
    private RTDroid rtdroid;
   
    public static final int MESSAGE_RECORD = 2;
    public static final int MESSAGE_PERIODE = 1;
    public static final int MESSAGE_FINCONFIG = 0;
   
    private long frequenceAttendu = 1;
    
    private double sommePrecision = 0;
    private float nbPrecision = 0;
    private boolean executionStarted = false;
    
   
    ProgrammeUtilisateur(RTDroid rtdroid, Handler handler)
    {
        this.rtdroid = rtdroid;
        mHandler = handler;
    }
   
    //methode appellée avant l'execution de la methode utilisateur
	@Override
	public void init()
	{
	    sommePrecision = 0;
	    nbPrecision = 0;
	    executionStarted = false;

		
	}


	//methode appelée a la fin de la configuration
    @Override
    public void endConfiguration(Boolean isRunable, Long frequence, Long wcet)
    {
		frequenceAttendu = frequence;
		Message msg = mHandler.obtainMessage();
		msg.obj = new String("Configuration terminée\nfrequence : " + frequence + "\nwcet : " + wcet);
		msg.arg2 = MESSAGE_FINCONFIG;
		if (isRunable)
		{
			msg.arg1 = 1;
		}
		else
		{
			msg.arg1 = 0;
		}

		mHandler.sendMessage(msg);
       
    }




    @Override
    public void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues)
    {
    	/***************Programme utilisateur***********************/
		if (executionStarted)
		{
			// envoie de la periode à l'interface
			Message msg = mHandler.obtainMessage();
			msg.arg2 = MESSAGE_PERIODE;
			sommePrecision += ((double) timeSinceLast / (double) (frequenceAttendu));
			nbPrecision++;
			msg.obj = new String("précision : " + ((float) timeSinceLast / (float) (frequenceAttendu)));
			msg.arg1 = (int) (sommePrecision*1000000000/nbPrecision);
			mHandler.sendMessage(msg);
		}
		else
		{
			executionStarted = true;
		}
       
        if (capteursValues != null)
        {
            for (int i = 0; i < capteursValues.size(); ++i)
            {
                //si le l'un des capteur est un accelerometre
                if (capteursValues.get(i).getType() == 1)
                {
                	Message msg = mHandler.obtainMessage();
                    msg = mHandler.obtainMessage();
                    msg.obj = new String("Nouvelle valeur\nx:"+ capteursValues.get(i).getValues()[SensorManager.DATA_X] +"\ny:"+capteursValues.get(i).getValues()[SensorManager.DATA_Y] +"\nz:"+ capteursValues.get(i).getValues()[SensorManager.DATA_Z]);
                    msg.arg2 = MESSAGE_RECORD;
                    mHandler.sendMessage(msg);
                   
                   
                }
            }

        }
        else
        {
        	Message msg = mHandler.obtainMessage();
            msg = mHandler.obtainMessage();
            msg.obj = new String("TestMethode\n");
            msg.arg2 = MESSAGE_RECORD;
            mHandler.sendMessage(msg);
        }

    }




   
}