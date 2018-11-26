package aev.sec.com.aev;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TtsService extends TextToSpeechService implements TextToSpeech.OnInitListener, ServiceConnection {

    private TextToSpeech mTts;
    private String str;
    private String id;

    private final IBinder binder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public void onBindingDied(ComponentName name) {

    }


    public interface ServiceCallbacks {
        void TTSSericeCallback(String id);
    }

    public TtsService() {

    }


    public class LocalBinder extends Binder {
        TtsService getService() {
            return TtsService.this;
        }
    }

    @Override
    protected int onIsLanguageAvailable(String s, String s1, String s2) {
        return 0;
    }

    @Override
    protected String[] onGetLanguage() {
        return new String[0];
    }

    @Override
    protected int onLoadLanguage(String s, String s1, String s2) {
        return 0;
    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent !=null && intent.getExtras()!=null){
            str = intent.getExtras().getString("TextToSpeak");
            mTts = (TextToSpeech) intent.getExtras().get("obj");
            id = intent.getExtras().getString("ID");
        }
        mTts = new TextToSpeech(this, this);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = mTts.setLanguage(Locale.US);
            mTts.setSpeechRate(0.9f);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

        HashMap<String, String> TTSParams = new HashMap();
        TTSParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                "UtteranceCompleteID");

        mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                if (serviceCallbacks != null) {
                    serviceCallbacks.TTSSericeCallback(id);
                }
            }

            @Override
            public void onError(String s) {

            }
        });

        mTts.speak(str, TextToSpeech.QUEUE_FLUSH, TTSParams);
    }
}
