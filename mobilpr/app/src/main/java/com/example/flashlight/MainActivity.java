package com.example.flashlight;

import android.Manifest;
import android.content.Context;import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;import android.widget.Switch;import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lco.blinkingflashlight.R;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    SeekBar sb;
    Switch s;
    int zaman;   //yanıp sönme aralığı
    private boolean mod=false;  //flaşın o anda açık olup olmadığı
    ImageButton imageButton;
    boolean flashacık;
    HashMap<Character, String> symbolsAndMeanings = new HashMap<>(); ///dizelerde harflere uygun değerlerin tantılması
    int noktasüresi = 150;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        morstanim();

        sb=findViewById(R.id.seek);
        s=findViewById(R.id.sw);
        sb.setMax(10);

        imageButton = findViewById(R.id.torchbtn);

        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                imageflash();
            }                                 ///imagebutton açma ve kapanma için izin alma

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(MainActivity.this, "Kamera erişimi yok.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  //SOS switche tıklanması
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},10);
                }
                else if(s.isChecked()){
                    sosbaslatma();
                }
            }
        });
    }

         private void sosbaslatma () {  ///sos modu çalışma bildirimi
        Timer t = new Timer();
        if (!s.isChecked()) {     ///switch kapalıysa flash kapalı
            t.cancel();
            flashkapalı();
            mod = false;
            return;
        }
        zaman = 1000 / (sb.getProgress() == 0 ? 1 : sb.getProgress());

        t.schedule(new TimerTask() {
            @Override               // flash eger yanıyorsa kapat kapalıysa aç
            public void run() {
                if (mod) {
                    flashkapalı();
                    mod = false;
                } else {
                    flashacma();
                    mod = true;
                }
                sosbaslatma();
            }
        }, zaman);

    }
        void flashkapalı() {          //flashı kapatma methodu
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    String cameraId = cm.getCameraIdList()[0];
                    cm.setTorchMode(cameraId, false);
                } catch (Exception e) {

                }
            }
    }


       private void flashacma() {          //flashı kapatma methodu
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                CameraManager cm = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    String cameraId = cm.getCameraIdList()[0];
                    cm.setTorchMode(cameraId, true);
                } catch (Exception e) {

                }
            }
    }

    private void imageflash() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!flashacık)
                    {                                         ////flash açık değilse açar
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, true);
                            }
                            flashacık = true;
                            imageButton.setImageResource(R.drawable.torch_on);
                        }
                        catch (CameraAccessException e)
                        {}
                    }
                    else                                   //// flashacık true ise yani flash yanıyorsa kapatır
                    {
                        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                        try {
                            String cameraId = cameraManager.getCameraIdList()[0];
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, false);
                            }
                            flashacık = false;
                            imageButton.setImageResource(R.drawable.torch_off);
                        }
                        catch (CameraAccessException e)
                        {}
                    }
                }
            });
        }
    }


    public void morstanim(){                                  ///dizelerde harflere uygun değerlerin tantılması
        symbolsAndMeanings.put('a', ". -");
        symbolsAndMeanings.put('b', "- . . . ");
        symbolsAndMeanings.put('c', "- . - .");
        symbolsAndMeanings.put('d', "- . .");
        symbolsAndMeanings.put('e', ".");
        symbolsAndMeanings.put('f', ". . - .");
        symbolsAndMeanings.put('g', "- - .");
        symbolsAndMeanings.put('h', ". . . .");
        symbolsAndMeanings.put('i', ". .");
        symbolsAndMeanings.put('j', ". - - -");
        symbolsAndMeanings.put('k', "- . -");
        symbolsAndMeanings.put('l', ". - . .");
        symbolsAndMeanings.put('m', "- -");
        symbolsAndMeanings.put('n', "- .");
        symbolsAndMeanings.put('o', "- - -");
        symbolsAndMeanings.put('p', ". - - .");
        symbolsAndMeanings.put('q', "- - . -");
        symbolsAndMeanings.put('r', ". - .");
        symbolsAndMeanings.put('s', ". . .");
        symbolsAndMeanings.put('t', "-");
        symbolsAndMeanings.put('u', ". . -");
        symbolsAndMeanings.put('v', ". . . -");
        symbolsAndMeanings.put('w', ". - -");
        symbolsAndMeanings.put('x', "- . . -");
        symbolsAndMeanings.put('y', "- . - -");
        symbolsAndMeanings.put('z', "- - . .");

        symbolsAndMeanings.put('0', "- - - - -");
        symbolsAndMeanings.put('1', ". - - - -");
        symbolsAndMeanings.put('2', ". . - - -");
        symbolsAndMeanings.put('3', ". . . - -");
        symbolsAndMeanings.put('4', ". . . . -");
        symbolsAndMeanings.put('5', ". . . . .");
        symbolsAndMeanings.put('6', "- . . . .");
        symbolsAndMeanings.put('7', "- - . . .");
        symbolsAndMeanings.put('8', "- - - . .");
        symbolsAndMeanings.put('9', "- - - - .");

        symbolsAndMeanings.put('_', ". . - - . -");
        symbolsAndMeanings.put('.', ". - . - . -");
        symbolsAndMeanings.put(',', "- - . . - -");
        symbolsAndMeanings.put('?', ". . - - . .");
        symbolsAndMeanings.put('\'', ". - - - - .");
        symbolsAndMeanings.put('!', "- . - . - -");
        symbolsAndMeanings.put('/', "- . . - .");
        symbolsAndMeanings.put('(', "- . - - .");
        symbolsAndMeanings.put(')', "- . - - . -");
        symbolsAndMeanings.put('&', ". - . . .");
        symbolsAndMeanings.put(':', "- - - . . .");
        symbolsAndMeanings.put(';', "- . - . - .");
        symbolsAndMeanings.put('=', "- . . . -");
        symbolsAndMeanings.put('+', ". - . - .");
        symbolsAndMeanings.put('-', "- . . . . -");
        symbolsAndMeanings.put('\"', ". - . . - .");
        symbolsAndMeanings.put('$', ". . . - . . -");
        symbolsAndMeanings.put('@', ". - - . - .");
    }

    // uygulama kapatma
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // sembol ifadelerini dizilerini alma
    private String simplified(String inp) {
        String[] temp = inp.toLowerCase().split(" +");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < temp.length; i++) {
            char[] lettersOfTheWord = temp[i].toCharArray();
            for (int j = 0; j < temp[i].length(); j++) {
                sb.append(symbolsAndMeanings.get(lettersOfTheWord[j]));
                sb.append("   ");
            }
            sb.append("       ");
        }
        return sb.toString();
    }

    // kameraya erişim sağlanır
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void turnOnOrSwitchOffTheLight(Boolean onOrOff){
        CameraManager cameraManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            String cameraId = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, onOrOff);
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Cihaz kamerayı desteklemiyor. " +
                    "Flashlight'a ulaşılamıyor.", Toast.LENGTH_SHORT).show();
        }
    }

    // input text morsdan gelen bilgiler flasha iletilir
    public void onMainButtonClcik(View view) {
        EditText input = findViewById(R.id.InputText);
        String inputText = input.getText().toString();          //mors için kelime alımı
        if (inputText.isEmpty()) {
            Toast.makeText(this, "Bir şeyler girin.", Toast.LENGTH_SHORT).show();
        }
        String temp = simplified(inputText.replaceAll("[^A-Za-z\\d_ .,?'!/()&:;=+\"$@-]+", ""));
        TextView output = findViewById(R.id.OutputText);
        output.setText(temp);                                     ///mors kelimesinin donusmus hali
        final char [] toConvert = temp.toCharArray();

        // Morse ifadelerini flash isigina cevirir
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < toConvert.length; i++) {
                    if (toConvert[i] == ' ') {                             ///eğer bosluk geldiyse
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            turnOnOrSwitchOffTheLight(false);
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(noktasüresi);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (toConvert[i] == '.') {   ////nokta gelirse fener yanar
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            turnOnOrSwitchOffTheLight(true);
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(noktasüresi);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else if (toConvert[i] == '-') {            ///cizgi gelirse 3 katı kadar yanıyor
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            turnOnOrSwitchOffTheLight(true);
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(noktasüresi*3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t1.start();     ///görevlerin engellenmemesine calısılır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            turnOnOrSwitchOffTheLight(false);
        }
    }
}


