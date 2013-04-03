package com.example.recognize;

// TODO 端末がsleepに入るとそこで終わってしまう
//      1) sleepをやめさせる
//      2) sleep中も頑張れる？（onResume()とか）

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private EditText output;
    private SpeechRecognizer recognizer;
    private Intent start_recognizer_intent;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private Boolean continue_recognition = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        output = (EditText)findViewById(R.id.output);
        registerListeners();
    }

    private void registerListeners() {
        Button b;
        b = (Button)findViewById(R.id.basic_button);
        b.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
                startVoiceRecognitionActivity();
            }
        });

        b = (Button)findViewById(R.id.use_service_button);
        b.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continue_recognition = Boolean.TRUE;
                output.setText("");
                startVoceRecognizeService();
            }
        });

        b = (Button)findViewById(R.id.stop_service_button);
        b.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.stopListening();
                continue_recognition = Boolean.FALSE;
            }
        });

        recognizer = SpeechRecognizer.createSpeechRecognizer(MyActivity.this);
        recognizer.setRecognitionListener(new MyRecognitionListener(getApplicationContext(), mHandler));
        start_recognizer_intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        start_recognizer_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        start_recognizer_intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getPackageName());
    }

    private void startVoceRecognizeService(){
        recognizer.startListening(start_recognizer_intent);
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "hello Speech recognition");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Specify the recognition language. This parameter has to be specified only if the
        // recognition has to be done in a specific language and not the default one (i.e., the
        // system locale). Most of the applications do not have to set this parameter.
       // if (!mSupportedLanguageView.getSelectedItem().toString().equals("Default")) {
       //     intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
       //             mSupportedLanguageView.getSelectedItem().toString());
       // }

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS);
            output.append(matches.get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode != RESULT_CANCELED ){
            startVoiceRecognitionActivity();
        }
    }

    final Handler handler=new Handler();

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MyRecognitionListener.ERROR:
                    Log.v("recognize", "Recognize Error !! <" + msg.arg1 + ">");
                    break;

                case MyRecognitionListener.OK:
                    Log.v("recognize", "Message Received !!");
                    String s = (String) msg.getData().get("result");
                    handler.post(new GuiUpdater(s) );
                    break;
            }
            if( continue_recognition ){
                startVoceRecognizeService();
            }
        }
    };

    class GuiUpdater implements Runnable{

        private String s;
        public GuiUpdater( String s ){
            this.s = s;
        }

        @Override
        public void run() {
            output.append(this.s);
        }
    };


}
