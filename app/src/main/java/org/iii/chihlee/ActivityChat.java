package org.iii.chihlee;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;

import org.iii.more.common.Logs;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityChat extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    private ImageView imageViewRobot;
    private int voiceRecognitionRequestCode = 777;
    TextToSpeech mTTS = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        imageViewRobot = findViewById(R.id.imageViewRobot);
        ImageView imageViewChatBg = findViewById(R.id.imageViewMatrixBg);
        
        RequestManager requestManager = Glide.with(this);
        RequestBuilder requestBuilder = requestManager.load(R.drawable.robot_head);
        requestBuilder.into(imageViewRobot);
        
        RequestManager requestManager2 = Glide.with(this);
        RequestBuilder requestBuilder2 = requestManager2.load(R.drawable.matrix);
        requestBuilder2.into(imageViewChatBg);
        
        findViewById(R.id.imageViewRobot).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saySomething("您想要的服務是", 0);
                startVoiceRecognitionActivity();
            }
        });
        
        mTTS = new TextToSpeech(this, this);
        
    }
    
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "您想要的服務是");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 15000);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        
        
        startActivityForResult(intent, voiceRecognitionRequestCode);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == voiceRecognitionRequestCode && resultCode == Activity.RESULT_OK)
        {
            ArrayList<String> matches =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // 語音識別會有多個結果，第一個是最精確的
            String text = matches.get(0);
            Logs.showTrace("speech: " + text);
            ((TextView) findViewById(R.id.textViewSpeech)).setText(text);
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void saySomething(String text, int qmode)
    {
        if (qmode == 1)
        {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
        else
        {
            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    
    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            if (mTTS != null)
            {
                int result = mTTS.setLanguage(Locale.TAIWAN);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                }
                else
                {
                    imageViewRobot.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_LONG).show();
        }
    }
    
    
}
