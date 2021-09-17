package eu.ase.proiect.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.ase.proiect.FireDatabase.getDataFromFireBase;
import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Book;

import static android.app.Activity.RESULT_OK;


public class AudioBookPlayerFragment extends Fragment {

    // firestore
    FirebaseFirestore firebaseFirestore;

    //coduri pt descarare
    static int PCodeRequest=1;
    public static final int CodeRequestDescarca = 34;
    private String pathDescarcareForlder;

    private Book book; //primita ca parametru la deschidere

    boolean sa_pus_pauza=false;

    private TextView pozitiePlayer,durataCarte;
    private SeekBar seekBar;
    ImageView imgCarte,btnRewind,btnPlay,btnPause,btnForward,btnDonwload;
    private ListView listViewCapitole;
    private List<String> capitole=new ArrayList<>();
    private List<Long> timpi=new ArrayList<>();

    MediaPlayer mediaPlayer;
    Handler handler=new Handler();
    Runnable runnable;




    public AudioBookPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_book_player, container, false);

        initComponents(view);

        evClickPlay();
        evClickPause();
        evClickRewind();
        evCLickForword();
        evFolosesteSeekBar();
        evFinalAudio();
        evClickDownload();
        return view;
    }


    private void initComponents(View view) {

        //setare titlu activitate
        ((MainActivity) getActivity()).setActionBatTitle(getString(R.string.player_asculta_carte));

        getBook();

        pozitiePlayer=view.findViewById(R.id.timp_curent);
        durataCarte=view.findViewById(R.id.durata_totala);
        seekBar=view.findViewById(R.id.bara_player);
        imgCarte=view.findViewById(R.id.player_imagine_carte);
        btnRewind=view.findViewById(R.id.button_rewind);
        btnPause=view.findViewById(R.id.button_pause);
        btnPlay=view.findViewById(R.id.button_play);
        btnForward=view.findViewById(R.id.button_forward);
        btnDonwload=view.findViewById(R.id.button_download);
        listViewCapitole=view.findViewById(R.id.audio_book_capitole);

        //incarcare lista capitole
//        getDataFromFireBase.get_Capitole_audiobook(book.getIdBook(),capitole,timpi);
//        capitole.add("ceva");
//        timpi.add(1);

        String idCarte=book.getIdBook()+"";
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("CapitoleAudio")
                .document(idCarte)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()){
                            capitole.addAll((List<String>) document.get("Capitole"));
                            timpi.addAll((List<Long>) document.get("Timpi"));
                            // definire adapter pentru lista
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext().getApplicationContext(),
                                    android.R.layout.simple_list_item_1, android.R.id.text1, capitole);
                            // Asignare adaptor pentru Lista
                            listViewCapitole.setAdapter(adapter);
                        }
                    }
                });


        evClicCapitol();

        //initializare media player
        mediaPlayer = new MediaPlayer();

        // below line is use to set the audio stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // below line is use to set our
        // url to our media player.
        try {
// "https://firebasestorage.googleapis.com/v0/b/proiect-librarie-dam.appspot.com/o/Audio_Carti%2FNaruto%20OST%201%20-Strong%20and%20Strike.mp3?alt=media&token=6b09c9b0-3711-4745-a540-3cd1f2b31f4a"
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(book.getAudioUrl());
            mediaPlayer.prepare();// il fac non async temp
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {


                    // Initalizare runnable
                    runnable=new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer.getCurrentPosition() >= 0){
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                handler.postDelayed(this,500);
                            }
                            else {
                                Toast.makeText(getContext(), "Media playerul nu sa initializat", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    // punem durata cartii audio
                    int durata=mediaPlayer.getDuration();
                    //Convertim in ore , minute , secunde
                    if (durata!=0) {
                        String sDurata = convertFormat(durata);
                        // Setare durata pe TextView-uri
                        durataCarte.setText(sDurata);
                    }

                    // Setare Seekbar max
                    //seekBar.setMax(mediaPlayer.getDuration());
                    //pornire Handler comentez temporar
                    //handler.postDelayed(runnable,0);
                }
            });

           // mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/proiect-librarie-dam.appspot.com/o/Audio_Carti%2FNaruto%20OST%201%20-Strong%20and%20Strike.mp3?alt=media&token=6b09c9b0-3711-4745-a540-3cd1f2b31f4a");
            Toast.makeText(getContext(), "Sa incarcat fisierul", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    private void evClicCapitol() {
        listViewCapitole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long timp_capitol=timpi.get(position)*1000;
                mediaPlayer.seekTo((int) timp_capitol);
            }
        });
    }

    private void evFinalAudio() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Ascundem Butonul de Pause
                btnPause.setVisibility(View.GONE);
                //Arata Butonul de Play
                btnPlay.setVisibility(View.VISIBLE);
                //setam inapoi la pozitia initiala
                mediaPlayer.seekTo(0);
            }
        });
    }

    private void evFolosesteSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //cand se misca bara de seek bar setam progresul
                    mediaPlayer.seekTo(progress);
                }
                pozitiePlayer.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void evCLickForword() {
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // luam momentul curent
                int acum=mediaPlayer.getCurrentPosition();
                //luam durata maxima
                int durataTota=mediaPlayer.getDuration();


                if (acum!=durataTota ) {
                    // sarim 30 secunde
                    acum+=30000;
                    //facem forward 30s
                    mediaPlayer.seekTo(acum);

                    if(mediaPlayer.isPlaying() == false){
                        pozitiePlayer.setText(convertFormat(acum));
                        seekBar.setProgress(acum);
                    }

                }

            }
        });
    }

    private void evClickRewind() {
        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // luam momentul curent
               int acum=mediaPlayer.getCurrentPosition();


                if (acum!=0) {
                    // ne intoarcem 30 secunde
                    acum-=30000;
                    //facem rewind 30s
                    mediaPlayer.seekTo(acum);
                    if(mediaPlayer.isPlaying() == false){
                        pozitiePlayer.setText(convertFormat(acum));
                        seekBar.setProgress(acum);
                    }

                }
            }
        });
    }

    private void evClickPause() {
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {

                    //Ascundem Butonul de Pause
                    btnPause.setVisibility(View.GONE);
                    //Arata Butonul de Play
                    btnPlay.setVisibility(View.VISIBLE);

                    // pausing the media player if
                    // media player is playing we are
                    // calling below line to stop our media player.
                    mediaPlayer.pause();
//                    mediaPlayer.reset();
//                    mediaPlayer.release();

                    // Oprim Handler-ul
                    handler.removeCallbacks(runnable);

                    // below line is to display a message when media player is paused.
                    Toast.makeText(getContext(), "Audio has been paused", Toast.LENGTH_SHORT).show();
                } else {
                    // this method is called when media player is not playing.
                    Toast.makeText(getContext(), "Audio has not played", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void evClickPlay() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ascundem Butonul de play
                btnPlay.setVisibility(View.GONE);
                //Arata Butonul de Pause
                btnPause.setVisibility(View.VISIBLE);
                // pORNIRE player audio
                // below line is use to prepare
                // and start our media player.
//                if (sa_pus_pauza == false) {
//
//                    mediaPlayer.start();
//                }
//                else {
//                    try {
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }

                //playAudioe motada veche, functionala
                //playAudio("https://firebasestorage.googleapis.com/v0/b/proiect-librarie-dam.appspot.com/o/Audio_Carti%2FNaruto%20OST%201%20-Strong%20and%20Strike.mp3?alt=media&token=6b09c9b0-3711-4745-a540-3cd1f2b31f4a");

                mediaPlayer.start();
                // Setare Seekbar max
                seekBar.setMax(mediaPlayer.getDuration());
                //pornire Handler comentez temporar
                handler.postDelayed(runnable,0);


            }
        });
    }



    private String convertFormat(int durata) {

        String afis= String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(durata),
                TimeUnit.MILLISECONDS.toMinutes(durata)%60,
                TimeUnit.MILLISECONDS.toSeconds(durata)%60);

        return afis;
    }

    private void getBook() {
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(BookDetailsFragment.AUDIO_BOOK);
//        getDataFromFireBase.get_Capitole_audiobook(book.getIdBook(),capitole,timpi);
        if (book !=null){
//            Toast.makeText(getContext().getApplicationContext(), "A mers",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext().getApplicationContext(), R.string.error_message_transfer_between_fragment,Toast.LENGTH_LONG).show();
        }
    }

    private void playAudio(String audioUrl) {

        try {
            // below line is use to set our
            // url to our media player.
            //comentez temporar
            //mediaPlayer.setDataSource(audioUrl);

            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

            // below line is use to display a toast message.
            Toast.makeText(getContext(), "Audio started playing..", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // this line of code is use to handle error while playing our audio file.
            Toast.makeText(getContext(), "Error found is " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void evClickDownload() {
        btnDonwload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificaSiCerePermisiune();
            }
        });
    }


    private void deschideGaleria() {
        Intent galerieIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //DocumentsContract.Document.MIME_TYPE_DIR in setType

        startActivityForResult(galerieIntent,CodeRequestDescarca);
    }

    private void verificaSiCerePermisiune() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getContext(),"Acceptati permisiunile necesare",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions((Activity) getContext(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PCodeRequest);
            }
        }
        else {
            deschideGaleria();
        }
    }


    public void download(Context context, String fileName, String filetype, String destination, String url){
        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destination,fileName+filetype);
        downloadManager.enqueue(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==CodeRequestDescarca && data != null ){
            // utilizatorul a ales cu succes o imagine
            // avem nevoie de referinta variabilei Uri a imaginii
            pathDescarcareForlder= String.valueOf(data.getData());
            download(getContext(),book.getTitle(),".mp3",pathDescarcareForlder,book.getAudioUrl());
        }
    }

    //-----------

}