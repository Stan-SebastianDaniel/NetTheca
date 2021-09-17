package eu.ase.proiect.util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;

public class RegisterActivity extends AppCompatActivity {

    static int PCodeRequest=1;
    static int CodeRequest=1;
    ImageView pozaUtilizator;
    Uri pickedImgUri;
    private EditText nume_utilizator,email_utilizator,parola_utilizator,parola2_utilizator;
    private ProgressBar loadingProgress;
    private Spinner spinner;
    private FirebaseAuth autentificare_firebase;
    private FirebaseFirestore inregistrare_user_in_firebase;
    private Button button_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //inu views
        nume_utilizator=findViewById(R.id.reg_Username);
        email_utilizator=findViewById(R.id.reg_Email);
        parola_utilizator=findViewById(R.id.reg_Password);
        parola2_utilizator=findViewById(R.id.reg_Password2);
        loadingProgress=findViewById(R.id.reg_progressBar);
        loadingProgress.setVisibility(View.INVISIBLE);
        spinner=findViewById(R.id.reg_spinner);

        inregistrare_user_in_firebase=FirebaseFirestore.getInstance();
        autentificare_firebase = FirebaseAuth.getInstance();
        button_register=findViewById(R.id.reg_button);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_register.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String nume=nume_utilizator.getText().toString();
                final String email=email_utilizator.getText().toString();
                final String parola=parola_utilizator.getText().toString();
                final String parola2=parola2_utilizator.getText().toString();

                if (nume.isEmpty() || email.isEmpty() || parola.isEmpty() || !parola.equals(parola2)){
                    //punem mesaj de eroare pt ca credentialele de inregistrare sunt prost completate
                    arataMesaj("Verificati si completati toate campurile!");
                    button_register.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {
                    //creem metoda de creeare cont
                    CreeazaCont(nume,email,parola);
                }
            }
        });

        pozaUtilizator = findViewById(R.id.reg_ImageView);
        pozaUtilizator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >=22){
                    verificaSiCerePermisiune();
                }
                else {
                    deschideGaleria();
                }
            }
        });
    }

    private void CreeazaCont(final String nume, final String email, final String parola) {
        autentificare_firebase.createUserWithEmailAndPassword(email,parola)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //utilizatorul a fost creat cu succes
                            arataMesaj("Cont Creeat!");
                            //acum updatam contul utilizatorului cu numele si poza
                            UpdateUserInfo( nume ,pickedImgUri,autentificare_firebase.getCurrentUser());
                            Random r = new Random();
                            int randomNumber = r.nextInt(100);
                            String sex=spinner.getSelectedItem().toString();
                            User utilizator=new User(randomNumber,email,nume,parola,sex);
                            String id_str= String.valueOf(randomNumber);
                            inregistrare_user_in_firebase.collection("Users").document(id_str).set(utilizator);
                        }
                        else {
                            arataMesaj("creearea a dat gres!");
                            button_register.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void UpdateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser){
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("poze_utilizatori");
        final StorageReference imageFIlePath=mStorage.child(pickedImgUri.getLastPathSegment());
        imageFIlePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //imaginea a fost uploadata cu succes
                //acum obtinem url-ul imaginiii
                imageFIlePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //uri contine url-ul imaginii
                        UserProfileChangeRequest profileUpadte = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        currentUser.updateProfile(profileUpadte).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //s-a updatata informatia utilizatorului cu succes
                                    arataMesaj("Inregsitrare Completa");
                                    updateUI();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void arataMesaj(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void deschideGaleria() {
        Intent galerieIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galerieIntent.setType("image/*");
        startActivityForResult(galerieIntent,CodeRequest);
    }

    private void verificaSiCerePermisiune() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(RegisterActivity.this,"Acceptati permisiunile necesare",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PCodeRequest);
            }
        }
        else {
            deschideGaleria();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==CodeRequest && data != null ){
            // utilizatorul a ales cu succes o imagine
            // avem nevoie de referinta variabilei Uri a imaginii
            pickedImgUri = data.getData();
            pozaUtilizator.setImageURI(pickedImgUri);
        }
    }
}