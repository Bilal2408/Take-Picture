package de.bilal.takepicture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // Wenn ein Code zur Bestätigung abgefragt wird, wird nur bei älteren Geräten der Fall sein
    private static final int PERMISSION_CODE = 0000;

    // Wenn ein Code zur Bestätigung abgefragt wird, wird nur bei älteren Geräten der Fall sein
    private static final int CAPTURE_CODE = 1111;
    // Button - welches die Kamera öffnen soll
    MaterialButton Button;
    // Ein ImageView um das Start bild anzuzeigen
    ImageView imageView;
    // Ein Uri um das ImageView zu ersetzen und das neu Fotografierte Foto dort anzuzeigen
    Uri image_uri;


    @Override
    // Wird aufgerufe, wenn die Aktivität beginnt
    // savedInstanceState => Wenn die Aktivität neu initialisiert wird, nachdem sie zuvor heruntergefahren wurde, enthält dieses Bundle die Daten, die es zuletzt in bereitgestellt hat . Hinweis: Andernfalls ist es null.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aufrufen der Benutzeroberfläche
        setContentView(R.layout.activity_main);

        // zum programmgesteuerten Interagieren mit Widgets in der Benutzeroberfläche (Zuweisung)


        Button = findViewById(R.id.capture_picture_Id);
        imageView = findViewById(R.id.ImageViewId);


        // Wenn auf dem Button gedrückt wird dann:
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SDK => Die SDK-Version der Software, die derzeit auf diesem Hardwaregerät ausgeführt wird.               Dieser Wert ändert sich nie, während ein Gerät gestartet wird, er kann sich jedoch erhöhen, wenn der Hardwarehersteller ein OTA-Update bereitstellt.
                // Version_code => Aufzählung der derzeit bekannten SDK-Versionscodes
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {

                    // Öffnung Permission-Code für die Kamera!
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            //  Dies wird zurückgegeben, wenn die Berechtigung für das angegebene
                            //  paket (Kamera) nicht erteilt wurde
                            PackageManager.PERMISSION_DENIED ||   // ODER

                            // Öffnung Permission-Code für den externen Speicherplatz
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    //  Dies wird zurückgegeben, wenn die Berechtigung für das angegebene Paket
                                    //  (Speichermedium) nicht erteilt wurde
                                    PackageManager.PERMISSION_DENIED){

                        // Ermöglicht der Anwendung, in einen externen Speicher zu schreiben.
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        // Kontrolle der Berechetigung ob sie erteilt wurde oder nicht
                        requestPermissions(permission,PERMISSION_CODE);
                    } else{
                        // Öffne die Kamera wenn die Berechtigung für die Verwendung der Kamera vorhanden ist
                        openCamera();
                    }
                } else{
                    // Öffne die Kamera wenn die Berechtigung zum speicehrn vorhanden ist
                    openCamera();
                }
            }
        });


    }
    // Kammera öffnen Methode:
    private void openCamera() {
        // Diese Klasse wird verwendet, um eine Reihe von Werten zu speichern
        ContentValues values = new ContentValues();

        // Das Bild wird ersetzt
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        // nimmt einen Anfragecode und gibt sie dann an Ihre Anrufaktivität (von wo der Befehl gekommen ist) zurück.
        // Sie übergeben ihr einen eindeutigen ganzzahligen Wert
        // Der requestCode hilft zu erkennen, von welchem Intent Sie zurückgekommen sind
        startActivityForResult(camintent, CAPTURE_CODE);
    }


    // onRequestPermissionsResult => Rückruf für das Ergebnis der Anforderung von Berechtigungen. Diese Methode wird bei jedem Aufruf von aufgerufen requestPermissions.
    @Override
    // @NonNull String[] permissions => Die angeforderten Berechtigungen darf nie null/ leer sein
    // @NonNull int[] grantResults   => Die Grant-Ergebnisse für die entsprechenden Berechtigungen sind entweder PERMISSION_GRANTED oder PERMISSION_DENIED. Nie null.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] ==

                        // Dies wird zurückgegeben, wenn die Berechtigung für das angegebene Paket erteilt wurde
                PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    // Wenn der Zugriff für die App - das Bilder und Videos "nicht" aufgenommen werden können Fehlgeschlagen wird:
                    Toast.makeText(this, "Berechtigung fehlgeschlagen !", Toast.LENGTH_SHORT).show();
                }
        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    // onActivityResult() -> um zu wissen, woher das Ergebnis stammt
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            // Das erstellte Foto wird nun in Set_uri gesetzt und wird auf der Startseite des Apps angezeigt
            imageView.setImageURI(image_uri);
        }
    }
}