package com.example.recognize;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerResultsIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: datsuns
 * Date: 13/04/02
 * Time: 21:18
 * To change this template use File | Settings | File Templates.
 */
// バックグラウンドで処理できるぽい
// Handlerとか渡してメッセージを貰うか？
public class MyRecognitionListener  implements RecognitionListener {
    private Context context;
    private Handler handler;
    public static final String RESULT = "result";
    public static final int OK = 0;
    public static final int ERROR = 1;
    public MyRecognitionListener( Context context, Handler handler ){
        this.context = context;
        this.handler = handler;
    }
    @Override
    public void onReadyForSpeech(Bundle params) {
        //To change body of implemented methods use File | Settings | File Templates.
        //Log.v("recognize", "onReadyForSpeech()");
    }

    @Override
    public void onBeginningOfSpeech() {
        //To change body of implemented methods use File | Settings | File Templates.
        Log.v("recognize", "onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //To change body of implemented methods use File | Settings | File Templates.
        //Log.v("recognize", "onRmsChanged()");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //To change body of implemented methods use File | Settings | File Templates.
        //Log.v("recognize", "onBufferReceived() <" + String.valueOf(buffer) + ">");
    }

    @Override
    public void onEndOfSpeech() {
        //To change body of implemented methods use File | Settings | File Templates.
        //Log.v("recognize", "onEndOfSpeech()");
    }

    @Override
    public void onError(int error) {
        //To change body of implemented methods use File | Settings | File Templates.
        Log.v("recognize", "onError() " + error );

        Message m = this.handler.obtainMessage();
        m.what = ERROR;
        m.arg1 = error;
        this.handler.sendMessage(m);
    }

    @Override
    public void onResults(Bundle results) {
        //To change body of implemented methods use File | Settings | File Templates.
        ArrayList<String> result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.v("recognize", "onResults() <" + result.get(0) + ">" );

        Message m = this.handler.obtainMessage();
        Bundle data = new Bundle();
        data.putString("result",result.get(0) );
        m.setData(data);
        m.what = OK;
        this.handler.sendMessage(m);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        //To change body of[ implemented methods use File | Settings | File Templates.
        //Log.v("recognize", "onPartialResults()");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        //To change body of implemented methods use File | Settings | File Templates.
        Log.v("recognize", "onEvent()");
    }


}
