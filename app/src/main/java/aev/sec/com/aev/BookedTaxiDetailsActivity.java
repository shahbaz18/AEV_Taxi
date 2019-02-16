package aev.sec.com.aev;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import aev.sec.com.aev.apicalls.UnlockCarRequest;
import aev.sec.com.aev.interfaces.CallbackHandler;
import aev.sec.com.aev.model.AccessCode;
import aev.sec.com.aev.model.BookedTripDetails;

public class BookedTaxiDetailsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private ImageView accessCodeMic;
    private TextToSpeech tts;
    private String codeFromVoice;
    private EditText accessCodeInput;
    private Button unlockCar;
    private TextView id;
    private TextView vin;
    private TextView plateNum;
    private TextView make;
    private TextView model;
    private TextView year;
    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_taxi_details);


        id = (TextView) findViewById(R.id.vehicleID);
        vin = (TextView) findViewById(R.id.vehicleVin);
        plateNum = (TextView) findViewById(R.id.plateNumber);
        make = (TextView) findViewById(R.id.make);
        model = (TextView) findViewById(R.id.model);
        year = (TextView) findViewById(R.id.year);

        accessCodeMic  = (ImageView) findViewById(R.id.iv_Mic_unlockCode);
        accessCodeInput = (EditText) findViewById(R.id.accessCodeInput);

        accessCodeMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAccessCodeFromVoice();
            }
        });
        unlockCar = (Button) findViewById(R.id.btn_accessCode);

        BookedTripDetails details = (BookedTripDetails) getIntent().getSerializableExtra("details");

        id.setText(details.getId().toString());
        vin.setText(details.getVehicleVin());
        plateNum.setText(details.getPlateNumber());
        make.setText(details.getMake());
        model.setText(details.getModel());
        year.setText(details.getYear());

        unlockCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccessCode code = new AccessCode();
                if (codeFromVoice != null && !codeFromVoice.isEmpty()){
                    code.setAccessCode(Integer.parseInt(codeFromVoice));
                    new UnlockCarRequest(code, new CallbackHandler<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response != null) {
                                if(response.equalsIgnoreCase("CarUnlocked")){
                                    Toast.makeText(getApplicationContext(),"Car Unlocked",Toast.LENGTH_LONG).show();
                                }
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Error Unlocking Car",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).call();
                }else
                {
                    Toast.makeText(getApplicationContext(),"Fill Access Code First",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getAccessCodeFromVoice() {
        showLocationLoader("Speaking");
        tts = new TextToSpeech(getApplicationContext(), this);
    }

    private void showLocationLoader(String speaking) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(speaking);
        mProgressDialog.show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    private void speakOut() {

        String text = "Please Speak Access code to unlock the Car";

        HashMap<String, String> TTSParams = new HashMap();
        TTSParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                "UtteranceCompleteID");

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                Log.e("onStart","OnStart");
            }

            @Override
            public void onDone(String s) {
                hideLocationLoader();
                startRecognizeSpeech();
            }

            @Override
            public void onError(String s) {
                Log.e("onError","onError");
            }
        });

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, TTSParams);

    }

    private void hideLocationLoader() {
        if (mProgressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.hide();
                }
            });

        }
    }

    private void startRecognizeSpeech() {
        Intent recognizeSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizeSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        int RESULT_SPEECH = 995;

        try {
            startActivityForResult(recognizeSpeechIntent, RESULT_SPEECH);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    hideLocationLoader();
//                }
//            });


        } catch (ActivityNotFoundException a) {
            Toast.makeText(
                    getApplicationContext(),
                    "Oops! First you must download \"Voice Search\" App from Store",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 995) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                this.codeFromVoice = text.get(0);
                this.codeFromVoice = this.codeFromVoice.replaceAll("\\s+","");
                this.accessCodeInput.setText(this.codeFromVoice);
            }
        }
    }
}
