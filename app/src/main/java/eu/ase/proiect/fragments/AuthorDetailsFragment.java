package eu.ase.proiect.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.proiect.FireDatabase.getDataFromFireBase;
import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.Recenzie;
import eu.ase.proiect.database.service.AuthorService;
import eu.ase.proiect.database.service.BookService;
import eu.ase.proiect.util.AuthorAdapter;
import eu.ase.proiect.util.BookAdapter;
import eu.ase.proiect.util.RecenzieAdapter;


public class AuthorDetailsFragment extends Fragment {

    public static String COMENTARIU_AUTOR="forum_autor";

    private Author author;
    private ArrayList<Author> listAuthor = new ArrayList<>();
    private ArrayList<Book> listBooks = new ArrayList<>();
    private ArrayList<Book> listBooksWithSingleAuthor = new ArrayList<>();
//    private ListView lvAuthor;
    private ImageView poza_autor;
    private TextView nume_autor;
    private TextView tvBiographyAuthor;
    private ListView lvAuthorWithBooks;

    private BookService bookService;


    //partea de review
    //chestii pt Recenzie
    Dialog popAddPost;
    CircleImageView popup_user_img;
    Button popup_buton;
    EditText popup_recenzie;
    RatingBar popup_recenzie_scor;

    String PostKeyAuthor;
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

    // booleane pentru reparat probleme
    private boolean initializare_ratingBar=false;

    public AuthorDetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_details_author, container, false);
        initComponents(view);

        evClickRatingBar();

        return view;
    }

    private void initComponents(View view) {

        //opresc dublarea
        listBooks.clear();

        //        setez titlu
        ((MainActivity) getActivity()).setActionBatTitle(getString(R.string.title_book_read));
        getAuthorFromBookDetailsFragment();

//        lvAuthor = view.findViewById(R.id.lv_author_details);
        poza_autor=view.findViewById(R.id.item_author_img);
        nume_autor=view.findViewById(R.id.item_author_name);

        tvBiographyAuthor = view.findViewById(R.id.tv_f_author_details_description);
//        // activez scroll
//        tvBiographyAuthor.setMovementMethod(new ScrollingMovementMethod());
        lvAuthorWithBooks = view.findViewById(R.id.lv_author_books);




        nume_autor.setText(author.getNameAuthor());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        try {
            StorageReference storageReference = storage.getReference().child("Img_Autori/" + author.getImgUrlAuthor());
            Glide.with(getContext()).load(storageReference).into(poza_autor);
        } catch (Exception e) {
            poza_autor.setImageResource(R.drawable.ic_uploading_photo);
            e.printStackTrace();
        }

//        addAuthorAdapter();
        tvBiographyAuthor.setText(author.getShortBiography());

        for (Book b: listBooks) {
            if(b.getIdAuthor() == author.getIdAuthor()){
                listBooksWithSingleAuthor.add(b);
            }
        }

//        add adapter
        addBookAdapter();


        //  partea de inializare recenzii
        bara_recenzie=view.findViewById(R.id.bara_recenzie_autor);
        lasa_recenzie=view.findViewById(R.id.lasaReview_autor);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //        //setare ratingBar daca a facut userul recenzie inainte
        firebaseFirestore
                .collection("/Recenzii_Autor/"+author.getIdAuthor()+"/lista/")
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
        PostKeyAuthor=author.getIdAuthor()+"";
        //initializam pop-up-ul
        RvRecenzie=view.findViewById(R.id.recenzii_autor);
        // initiere Recyclerview Recenzii
        iniRvRecenzii();


        initPopUp();
        //------------------------

        //testez duplicare
        notifyBookAdapter();

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

                    getDataFromFireBase.adauga_recenzie_Autor(recenzie,author.getIdAuthor());
                    showMessage("comment added");
                    popup_recenzie.setText("");
                    popAddPost.hide();
                    listRecenzi.clear();

//                    //testez aduagarea in baza de date a review-ului
//                    book.setReview(book.getReview()+1);
//                    String titlul_carte=book.getImgUrl();
//                    titlul_carte=titlul_carte.replace(".jpg","");
//                    titlul_carte=titlul_carte.replace(".png","");
//                    firebaseFirestore.collection("Carti").document(titlul_carte).update("review",book.getReview());
//                    //-----------------

                    iniRvRecenzii();


                }
            }
        });
    }

    private void iniRvRecenzii() {
        RvRecenzie.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        //   getDataFromFireBase.get_recenzii(listRecenzi,book.getIdBook());

        firebaseFirestore
                .collection("/Recenzii_Autor/"+author.getIdAuthor()+"/lista/")
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

    }

    //     Eveniment click pe Rating Bar
    private void evClickRatingBar() {

        bara_recenzie.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (initializare_ratingBar==true) {
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
            bundle.putSerializable(COMENTARIU_AUTOR, author);
            forumFragment.setArguments(bundle);
            ft.replace(R.id.main_frame_container, forumFragment);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        //original return super.onOptionsItemSelected(item);
        return true;
    }


    private void getAuthorFromBookDetailsFragment() {
        Bundle bundle = getArguments();
        author = (Author)bundle.getSerializable(BookDetailsFragment.AUTHOR_KEY);
        listBooks = (ArrayList<Book>) bundle.getSerializable((BookDetailsFragment.LIST_BOOKS_P_KEY));
        if(author != null) {
            listAuthor.add(author);
        } else {
            Toast.makeText(getContext().getApplicationContext(), R.string.error_message_transfer_between_fragment,Toast.LENGTH_SHORT).show();
        }
    }

        // temporar nu mai folosesc listview pt desenare autor
//    private void addAuthorAdapter(){
//        AuthorAdapter authorAdapter = new AuthorAdapter(getContext().getApplicationContext(), R.layout.item_author, listAuthor, getLayoutInflater());
//        lvAuthor.setAdapter(authorAdapter);
//    }
//
//    public void notifyAuthorAdapter(){
//        ArrayAdapter adapter =  (ArrayAdapter) lvAuthor.getAdapter();
//        adapter.notifyDataSetChanged();
//    }


    private void addBookAdapter(){
        BookAdapter bookAdapter = new BookAdapter(getContext().getApplicationContext(), R.layout.item_book, listBooksWithSingleAuthor, listAuthor, getLayoutInflater());
        lvAuthorWithBooks.setAdapter(bookAdapter);
    }

    public void notifyBookAdapter(){
        ArrayAdapter adapter =  (ArrayAdapter) lvAuthorWithBooks.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void showMessage(String message) {

        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

    }

}