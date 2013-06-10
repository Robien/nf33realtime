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
   
    //vitesse minimun pour détecter un shake
    private static final int FORCE_THRESHOLD = 20;//350;
    // temps minimun pour commencer a détecter la position du téléphone
    private static final long TIME_THRESHOLD =100000000l;
    // temps minimun entre 2 position pour être considérer comme un shaker
    private static final long SHAKE_TIMEOUT = 5000000000l;
    // temps entre un 2 shake pour incrementer le nombre de shake
    private static final long SHAKE_DURATION = 1000000000l;
    // nombre de shake pour lancer une action
    private static final int SHAKE_COUNT = 1;
   
    private SensorManager mSensorMgr;
    private float mLastX=-1.0f, mLastY=-1.0f, mLastZ=-1.0f;
    private long mLastTime;

    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    private long frequenceAttendu = 0;
   
    ProgrammeUtilisateur(RTDroid rtdroid, Handler handler)
    {
        this.rtdroid = rtdroid;
        mHandler = handler;
    }
   



    @Override
    public void endConfiguration(Boolean isRunable, Long frequence, Long wcet)
    {
    	frequenceAttendu = frequence;
        Message msg = mHandler.obtainMessage();
        msg.obj = new String("Configuration terminée\nfrequence : "+frequence+ "\nwcet : "+ wcet);
        msg.arg2 = MESSAGE_FINCONFIG;
        mHandler.sendMessage(msg);
       
    }




    @Override
    public void periodicEvent(long timeSinceLast, ArrayList<CapteurValue> capteursValues)
    {
        /*
        try
        {
            Thread.sleep((long) (Math.random()*90), 0);
            Message msg = mHandler.obtainMessage();
            msg.obj = new String("période : ");
            msg.arg1 = (int)(timeSinceLast);
            mHandler.sendMessage(msg);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (capteursValues == null)
        {
            try
            {
                Thread.sleep((long) (Math.random()*15), 0);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            Log.d("DADU", "VIVE LES POMMES");
            Log.d("DADUtilisateur", "" +capteursValues.get(0).getValues()[0]);
        }
        */
       
        /***************Programme utilisateur***********************/
        Message msg = mHandler.obtainMessage();
        msg.arg2 = MESSAGE_PERIODE;
        msg.obj = new String("Erreur : " +  ((float)timeSinceLast/(float)(frequenceAttendu)) );
        msg.arg1 = (int)(timeSinceLast);
        mHandler.sendMessage(msg);
        if (capteursValues != null)
        {
            for (int i = 0; i < capteursValues.size(); ++i)
            {
                //si le l'un des capteur est un accelerometre
                if (capteursValues.get(i).getType() == 1)
                {
                    msg = mHandler.obtainMessage();
                    msg.obj = new String("!! Grosse Secousse !!\n");
                    msg.arg1 = (int)(capteursValues.get(i).getValues()[0]*1000);
                    msg.arg2 = MESSAGE_RECORD;
                    mHandler.sendMessage(msg);
                   
                   
                }
            }

        }
        else
        {
            msg = mHandler.obtainMessage();
            msg.obj = new String("!! Grosse Secousse !!\n");
            msg.arg1 = (int)(0*1000);
            msg.arg2 = MESSAGE_RECORD;
            mHandler.sendMessage(msg);
        }

    }

   
}