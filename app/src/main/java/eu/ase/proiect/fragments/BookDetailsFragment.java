package eu.ase.proiect.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.DocumentsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.Recenzie;
import eu.ase.proiect.database.service.AuthorService;
import eu.ase.proiect.database.service.BookService;
import eu.ase.proiect.util.BookAdapter;
import eu.ase.proiect.util.RecenzieAdapter;
import eu.ase.proiect.util.RegisterActivity;
import eu.ase.proiect.FireDatabase.getDataFromFireBase;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {

    public static final String AUTHOR_KEY = "author_key";
    public static final String BOOK_WITH_PDF_KEY = "bookWithPdf_key";
    public static final String LIST_BOOKS_P_KEY = "listBooksP_key";
    public static final String AUDIO_BOOK = "audio_book";
    public static final String COMENTARIU_BOOK = "comentariu_book";
    public static final String COMENTARIU_AUTH = "comentariu_author";
    //coduri pt descarare pdfs
    static int PCodeRequest=1;
    public static final int CodeRequestDescarca = 34;
    //---
    private Book book; //primita ca parametru la deschidere
    private Author author; // primit ca parametru la deschidere
    private List<Book> listBooks = new ArrayList<>(); //contine o carte si e trimitsa ca parametru pe adapter
    private List<Author> listAuthor = new ArrayList<>(); //contine un autor si e trimitsa ca parametru pe adapter
    private List<Book> listFavoriteBooks = new ArrayList<>(); // contine toate cartile favorite
    private List<Author> listAllAuthors = new ArrayList<>(); // contine toati autorii

    private ArrayList<Book> lBooksP = new ArrayList<>();

    //pt descarcare
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    //------

    private ListView lvBookDetails;
    private TextView tvDescription;
    private Button btnAuthorDetails;
    private Button btnReadpdf;
    // chestii descarcare
    private Button btnDescarca;
    private String pathDescarcareForlder;
    //-------
    //incercare audio player
    private Button btnAsculta;
    //--------
    //chestii pt Recenzie
    Dialog popAddPost;
    CircleImageView popup_user_img;
    Button popup_buton;
    EditText popup_recenzie;
    RatingBar popup_recenzie_scor;

    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    private RatingBar bara_recenzie;
    private TextView lasa_recenzie;
    private RecyclerView RvRecenzie;
    private RecenzieAdapter recenzieAdapter;
    private List<Recenzie> listRecenzi = new ArrayList<>();
   // private List<Recenzie> listRecenzi;
    static String RECENZIE_KEY = "Recenzie" ;
    //----------

    private BookService bookService;
    private AuthorService authorService;
    private Integer eachBooksHasAuthor=0;

    // booleane pentru reparat probleme
    private boolean initializare_ratingBar=false;


    public BookDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        initComponents(view);

        evClickFavorizare();


        evClickBtnAuthorDetails();

        evClickBtnReadPDF();


        //testare descarcare
        evClickBtnDescarca();
        //--

        //--testare audio book
        evClickBtnAsculta();

        //--recenzie
        evClickRatingBar();

        initializare_ratingBar=false;

        return view;
    }



    private void evClickBtnAuthorDetails() {
        btnAuthorDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                AuthorDetailsFragment frg2 = new AuthorDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(AUTHOR_KEY, author);
                bundle.putSerializable(LIST_BOOKS_P_KEY, lBooksP);
                frg2.setArguments(bundle);
                ft.replace(R.id.main_frame_container, frg2);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void initComponents(View view) {


//        initializare views
        lvBookDetails = view.findViewById(R.id.lv_book_details);
        // testez orpirea dublarii
        listBooks.clear();
        listRecenzi.clear();


        tvDescription = view.findViewById(R.id.tv_f_book_details_description);
        //se poate face scroll in descrierea textview-ului
        tvDescription.setMovementMethod(new ScrollingMovementMethod());

        btnAuthorDetails = view.findViewById(R.id.btn_authorDetails);

        // incercare descarcare
        btnDescarca=view.findViewById(R.id.btn_descarca);
        //----------
        // incercare audio
        btnAsculta=view.findViewById(R.id.btn_f_book_details_Asculta);
        //---------

        btnReadpdf=view.findViewById(R.id.btn_f_book_details_Read_Book);

        //preiau obiectul book si author din fragmentul AllBookFragment  /  FavoriteBooksFragment
        getBookFromAllBookFragment();

//              setez titlu
        ((MainActivity) getActivity()).setActionBatTitle(getString(R.string.title_book_details));

        bookService = new BookService(getContext().getApplicationContext());
        authorService = new AuthorService(getContext().getApplicationContext());



        //        lista de carti favorite este populata cu cartile din baza de date
        bookService.getAllFavoriteBooks(getAllFavoriteBooksDbCallback());
        authorService.getAll(getAllAuthorsDbCallback());

        addBookAdapter();

        //  partea de inializare recenzii
        bara_recenzie=view.findViewById(R.id.bara_recenzie);
        lasa_recenzie=view.findViewById(R.id.lasaReview);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //        //setare ratingBar daca a facut userul recenzie inainte
        firebaseFirestore
                .collection("/Recenzii/"+book.getIdBook()+"/lista/")
                .whereEqualTo("uId",firebaseUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                            Recenzie recenzieTemp = queryDocumentSnapshot.toObject(Recenzie.class);
                            bara_recenzie.setRating(recenzieTemp.getScore());
                            bara_recenzie.setIsIndicator(true);
                        }
                    }
                });

        //------------------------------------

        //facem rost de cartea in care vrem sa postam
        PostKey=book.getIdBook()+"";
        //initializam pop-up-ul
        RvRecenzie=view.findViewById(R.id.recenzii);
        // initiere Recyclerview Recenzii
        iniRvRecenzii();


        initPopUp();
        //------------------------


//           daca cartea exista in lista de favorite, btn Add e invizibil, altfel btn Remove e invizibil
//        updateVisibilityButtons(view);

        evVerificareVizibilitatiButoane();

    }

    private void initPopUp() {
        popAddPost = new Dialog(getContext());
        popAddPost.setContentView(R.layout.popup_recenzie);
       // popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        popAddPost.getWindow().setBackgroundDrawable(d);

        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        popup_user_img=popAddPost.findViewById(R.id.img_user_popup_recenzie);
        popup_recenzie=popAddPost.findViewById(R.id.popup_recenzie);

        popup_buton=popAddPost.findViewById(R.id.add_recenzie);
        popup_recenzie_scor=popAddPost.findViewById(R.id.ratingBar_popup_recenzie);
        popup_recenzie_scor.setRating(bara_recenzie.getRating());

        Glide.with(getContext()).load(firebaseUser.getPhotoUrl()).into(popup_user_img);

        popup_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popup_recenzie.getText().toString().isEmpty()){

                    //DatabaseReference recenzieReference = firebaseDatabase.getReference().child(RECENZIE_KEY).child(PostKey).push();

                    String comment_content = popup_recenzie.getText().toString();
                    String uid = firebaseUser.getUid();
                    String uname = firebaseUser.getDisplayName();
                    String uimg;
                    if (firebaseUser.getPhotoUrl() !=null) {
                         uimg = firebaseUser.getPhotoUrl().toString();
                    }
                    else {
                        uimg="";
                    }
                    float scorul_acordat=popup_recenzie_scor.getRating();
                    bara_recenzie.setRating(scorul_acordat);

                    Recenzie recenzie = new Recenzie(comment_content,uid,uimg,uname,scorul_acordat);

                    getDataFromFireBase.adauga_recenzie(recenzie,book.getIdBook());
                    showMessage("comment added");
                    popup_recenzie.setText("");
                    popAddPost.hide();
                    listRecenzi.clear();
                    initializare_ratingBar=false;

                    //testez aduagarea in baza de date a review-ului
                    book.setReview(book.getReview()+1);
                    book.setRating(book.getRating()+scorul_acordat);
                    String titlul_carte=book.getImgUrl();
                    titlul_carte=titlul_carte.replace(".jpg","");
                    titlul_carte=titlul_carte.replace(".png","");
                    firebaseFirestore.collection("Carti").document(titlul_carte).update("review",book.getReview());
                    firebaseFirestore.collection("Carti").document(titlul_carte).update("rating",book.getRating());
                    notifyAdapter();
                    //-----------------
                    MainActivity.getDatedinFirebase();
                    iniRvRecenzii();


//                    final DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
//                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot snapshot) {
//                            if (snapshot.hasChild(RECENZIE_KEY)){
//                                reference.child(RECENZIE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot snapshot) {
//                                        if (snapshot.hasChild(RECENZIE_KEY+"/"+PostKey)){
//                                            // to do event click
//                                            showMessage("a ajuns aici");
//                                        }
//                                        else {
//                                            reference.child(RECENZIE_KEY).push();
//                                            reference.child(RECENZIE_KEY).setValue(PostKey);
//                                            showMessage("sa facut de la "+RECENZIE_KEY+" referinta catre "+PostKey);
//                                        }
//                                    }
//
//
//                                    @Override
//                                    public void onCancelled(DatabaseError error) {
//
//                                    }
//                                });
//                            }
//                            else {
//                                reference.push();
//                                reference.setValue(RECENZIE_KEY);
//                                reference.child(RECENZIE_KEY).push();
//                                reference.child(RECENZIE_KEY).setValue(PostKey);
//                                showMessage("sa facut de la 0");
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError error) {
//
//                        }
//                    });
//
//                    //schimb recenzieReferance cu alta
//                    recenzieReference.setValue(recenzie).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            showMessage("comment added");
//                            popup_recenzie.setText("");
//                            popAddPost.hide();
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            showMessage("fail to add comment : "+e.getMessage());
//                        }
//                    });


                }
            }
        });
    }

    private void iniRvRecenzii() {
        RvRecenzie.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

     //   getDataFromFireBase.get_recenzii(listRecenzi,book.getIdBook());

        firebaseFirestore
                .collection("/Recenzii/"+book.getIdBook()+"/lista/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                            Recenzie recenzie = queryDocumentSnapshot.toObject(Recenzie.class);
                            listRecenzi.add(recenzie);

                        }
                        recenzieAdapter = new RecenzieAdapter(getContext(),listRecenzi);
                        RvRecenzie.setAdapter(recenzieAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                showMessage("Nu a mers");
            }
        });

//        recenzieAdapter = new RecenzieAdapter(getContext(),listRecenzi);
//        RvRecenzie.setAdapter(recenzieAdapter);


        //nu merge cu Realtime Database
        //DatabaseReference commentRef = firebaseDatabase.getReference(RECENZIE_KEY).child(PostKey);

//        commentRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listRecenzi = new ArrayList<>();
//                for (DataSnapshot snap:dataSnapshot.getChildren()) {
//
//                    Recenzie comment = snap.getValue(Recenzie.class);
//                    listRecenzi.add(comment) ;
//
//                }
//
//                recenzieAdapter = new RecenzieAdapter(getContext(),listRecenzi);
//                RvRecenzie.setAdapter(recenzieAdapter);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    // initiere menu cu subiteme pentru Categorie si Filtrare
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forum,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        int id_item_menu=item.getItemId();

        if (id_item_menu==R.id.forum || id_item_menu==R.id.forum_icon){
            ForumFragment forumFragment=new ForumFragment();

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            Bundle bundle = new Bundle();
            bundle.putSerializable(COMENTARIU_BOOK, book);
            forumFragment.setArguments(bundle);
            ft.replace(R.id.main_frame_container, forumFragment);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        //original return super.onOptionsItemSelected(item);
        return true;
    }


    private void evVerificareVizibilitatiButoane() {
        if (book!=null) {
            if (book.getPdfUrl()== null) {
                btnReadpdf.setVisibility(View.INVISIBLE);
                btnDescarca.setVisibility(View.INVISIBLE);
            }
            else if (book.getPdfUrl().isEmpty()==true) {
                btnReadpdf.setVisibility(View.INVISIBLE);
                btnDescarca.setVisibility(View.INVISIBLE);
            }

            if (book.getAudioUrl()==null) {
                btnAsculta.setVisibility(View.INVISIBLE);
            }
            else if (book.getAudioUrl().isEmpty()==true) {
                btnAsculta.setVisibility(View.INVISIBLE);
            }
        }
    }



    //     Eveniment click pe Rating Bar
    private void evClickRatingBar() {
//            initializare_ratingBar=false;
                bara_recenzie.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (bara_recenzie.isIndicator()==false && initializare_ratingBar==true) {
                        popup_recenzie_scor.setRating(bara_recenzie.getRating());
                        popAddPost.show();
                        bara_recenzie.setIsIndicator(true);
                    }
                    else {
                        initializare_ratingBar=true;
                    }
                    }
                });
    }

    //incercare buton asculta
    private void evClickBtnAsculta() {
        btnAsculta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioBookPlayerFragment audioBookPlayerFragment=new AudioBookPlayerFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                Bundle bundle = new Bundle();
                bundle.putSerializable(AUDIO_BOOK, book);
                audioBookPlayerFragment.setArguments(bundle);
                ft.replace(R.id.main_frame_container, audioBookPlayerFragment);
                ft.addToBackStack(null);
                ft.commit();
                // sa vedem daca merge fara
                //notifyInternalAdapter();
            }
        });

    }
    //-------------

    //incercare buton descarcare
    private void   evClickBtnDescarca(){
        btnDescarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificaSiCerePermisiune();
                //download(getContext(),book.getTitle(),".pdf",DIRECTORY_DOWNLOADS,book.getPdfUrl());
            }
        });
    }

    private void deschideGaleria() {
        Intent galerieIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //DocumentsContract.Document.MIME_TYPE_DIR in setType

        startActivityForResult(galerieIntent,CodeRequestDescarca);
    }

    private void verificaSiCerePermisiune() {
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
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


    public void download(Context context, String fileName,String filetype, String destination, String url){
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
            download(getContext(),book.getTitle(),".pdf",pathDescarcareForlder,book.getPdfUrl());
        }
    }

    //-----------


    //    preluare book / author din allbookFragment / favoriteBooksFragmnet
    private void getBookFromAllBookFragment() {
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(AllBooksFragment.BOOK_DETAILS_KEY);
        author = (Author)bundle.getSerializable(AllBooksFragment.AUTHOR_DETAILS_KEY);
        lBooksP=(ArrayList<Book>)bundle.getSerializable(AllBooksFragment.L_BOOK_P_KEY);

        if(book != null && author != null) {
            listBooks.add(book);
            tvDescription.setText(book.getDescription());
            listAuthor.add(author);
        } else {
            Toast.makeText(getContext().getApplicationContext(), R.string.error_message_transfer_between_fragment,Toast.LENGTH_LONG).show();
        }
    }



    private boolean isFavoriteBook(){
        try {
            Book book2 = listBooks.get(0);
            for (Book b : listFavoriteBooks) {
                if (b.getIdBook() == book2.getIdBook()) {
                    book.setIs_read(b.getIs_read());
                    return true;
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),"ceva nu merge la favorite",Toast.LENGTH_SHORT);
        }
        return false;
    }

    private boolean authorExist() {
        Author author2 = listAuthor.get(0);
        for (Author a: listAllAuthors) {
            if(a.getIdAuthor() == author2.getIdAuthor()){
                return true;
            }
        }
        return false;
    }

    //citire din pdf firebase
    private void   evClickBtnReadPDF(){
        if (book.getIs_comic()==0) {
            btnReadpdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // PdfReader nextFrag= new PdfReader();

//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_frame_container, nextFrag, "PdfReader Fragment")
//                        .addToBackStack(null)
//                        .commit();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    PdfReader frg2 = new PdfReader();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BOOK_WITH_PDF_KEY, book);
                    frg2.setArguments(bundle);
                    ft.replace(R.id.main_frame_container, frg2);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
        else {
            btnReadpdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    CapitoleComicFragment capitoleComicFragment=new CapitoleComicFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BOOK_WITH_PDF_KEY, book);
                    capitoleComicFragment.setArguments(bundle);
                    ft.replace(R.id.main_frame_container, capitoleComicFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }

    private boolean authorHasBooks(long idAuthor){
        int nr=0;
        for (Book b: listFavoriteBooks) {
            if(b.getIdAuthor()==idAuthor){
                nr++;
                if(nr==2){
                    return true;
                }
            }
        }
        return false;
    }






    private void evClickFavorizare() {
        lvBookDetails.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isFavoriteBook()){
                    if(!authorExist()){
//                        add author in db  + in Callback  se face insert la carte + update la listFavoriteBooks
                        authorService.insertAuthor(insertAuthorIntoDbCallback(), author);
                    } else{
                        book.setIs_favorite(1);
                        bookService.insertBook(insertBookIntoDbCallback(),book);
                    }
                }
                else {
                    if(book.getIs_read()==0){
                        //TODO delete book, after author
                        bookService.delete(deleteBookFromDbCallback(),book);
                    }
                }
            }
        });
    }

    private void addBookAdapter(){
        BookAdapter bookAdapter = new BookAdapter(getContext().getApplicationContext(), R.layout.item_book, listBooks, listAuthor, getLayoutInflater());
        lvBookDetails.setAdapter(bookAdapter);

    }

    public void notifyAdapter(){
        ArrayAdapter adapter =  (ArrayAdapter) lvBookDetails.getAdapter();
        adapter.notifyDataSetChanged();
    }


    /*************          DATABASE         ******************/
    private Callback<Book> insertBookIntoDbCallback(){
        return new Callback<Book>() {
            @Override
            public void runResultOnUiThread(Book result) {
                if(result != null){
                    listFavoriteBooks.add(result);

                    Toast.makeText(getContext().getApplicationContext(),getString(R.string.confirm_add_to_favorite, book.getTitle()), Toast.LENGTH_SHORT).show();
                    notifyAdapter();
//                    book.setIs_favorite(1);
//                    bookService.updateBook(updateBookIntoDbCallback(),book);
                }
            }
        };
    }

    private Callback<Author> insertAuthorIntoDbCallback(){
        return new Callback<Author>() {
            @Override
            public void runResultOnUiThread(Author result) {
                if(result != null){
                    listAllAuthors.add(result);
                    book.setIs_favorite(1);
                    bookService.insertBook(insertBookIntoDbCallback(), book);
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<List<Book>> getAllFavoriteBooksDbCallback(){
        return new Callback<List<Book>>() {
            @Override
            public void runResultOnUiThread(List<Book> result) {
                if(result != null){
                    listFavoriteBooks.clear();
                    listFavoriteBooks.addAll(result);
                }
            }
        };
    }

    private Callback<List<Author>> getAllAuthorsDbCallback(){
        return new Callback<List<Author>>() {
            @Override
            public void runResultOnUiThread(List<Author> result) {
                if(result != null){
                    listAllAuthors.clear();
                    listAllAuthors.addAll(result);
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<Book> updateBookIntoDbCallback(){
        return new Callback<Book>() {
            @Override
            public void runResultOnUiThread(Book result) {
                if(result!=null){
                    notifyAdapter();
                }
            }
        };

    }

    private Callback<Integer> deleteBookFromDbCallback(){
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if(result != -1){
                    book.setIs_favorite(0);
                    notifyAdapter();

                    if(!authorHasBooks(author.getIdAuthor())){
                        authorService.delete(deleteAuthorFromDbCallback(),author);
                    }
                    Toast.makeText(getContext(), getString(R.string.confirm_remove_to_favorite,book.getTitle()),Toast.LENGTH_SHORT).show();
                    bookService.getAllBooks(getAllBooksDbCallback());
                    notifyAdapter();
                }
            }
        };
    }

    private Callback<List<Book>> getAllBooksDbCallback() {
        return new Callback<List<Book>>() {
            @Override
            public void runResultOnUiThread(List<Book> result) {
                if(result!=null){
                    listFavoriteBooks.clear();
                    listFavoriteBooks.addAll(result);
                }
            }
        };
    }


    private Callback<Integer> deleteAuthorFromDbCallback(){
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if(result != -1){
                    //update listAllAuthors
                    authorService.getAll(getAllAuthorsDbCallback());
                    notifyAdapter();
                }
                notifyAdapter();
            }
        };
    }

    private void showMessage(String message) {

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

    }

//    private Callback<Integer> eachBooksHasAuthorCallback(){
//        return new Callback<Integer>() {
//            @Override
//            public void runResultOnUiThread(Integer result) {
//                if(result != -1){
//                    eachBooksHasAuthor = result;
//                }
//            }
//        };
//    }



}