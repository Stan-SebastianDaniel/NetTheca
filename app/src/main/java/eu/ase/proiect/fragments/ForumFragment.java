package eu.ase.proiect.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.proiect.FireDatabase.getDataFromFireBase;
import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.Comentariu;
import eu.ase.proiect.database.model.Recenzie;
import eu.ase.proiect.util.ComentariuAdapter;
import eu.ase.proiect.util.RecenzieAdapter;


public class ForumFragment extends Fragment {

    Book book;
    Author author;

    //declarare variabile pentru Forum
    CircleImageView comentariu_user_img;
    Button adauga_comentariu;
    EditText comentariu_text;

    String PostKeyForum_carte;
    String PostKeyForum_autor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    private RecyclerView RvComentariu;
    private ComentariuAdapter comentariuAdapter;
    private List<Comentariu> comentariuList = new ArrayList<>();

    static String COMENTARIU_KEY = "Comentariu" ;
    //----------

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();



        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            View view=inflater.inflate(R.layout.fragment_forum, container, false);


             initComponents(view);

             evClickAdaugaComentariu();

            return view;
    }




    private void initComponents(View view) {
        getBookFromBookDetailsFragment();
        getAuthorFromAuthorDetailsFragment();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        comentariu_user_img=view.findViewById(R.id.img_user_forum);
        adauga_comentariu=view.findViewById(R.id.add_comment_forum);
        comentariu_text=view.findViewById(R.id.comentariu_scris);
        RvComentariu=view.findViewById(R.id.recycle_forum);

        Glide.with(getContext()).load(firebaseUser.getPhotoUrl()).into(comentariu_user_img);

        //inutile
//        //facem rost de cartea in care vrem sa postam
//        if (book!=null) {
//            PostKeyForum_carte = book.getIdBook() + "";
//        }
//        //facem rost de autorul in care vrem sa postam
//        if (author!=null){
//            PostKeyForum_autor = author.getIdAuthor() + "";
//        }

        initRecycleView();
    }

    private void initRecycleView() {
        RvComentariu.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        if (book!=null) {
            firebaseFirestore
                    .collection("/Comentarii/" + book.getIdBook() + "/lista/")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                Comentariu comentariu = queryDocumentSnapshot.toObject(Comentariu.class);
                                comentariuList.add(comentariu);

                            }
                            comentariuAdapter = new ComentariuAdapter(getContext(), comentariuList);
                            RvComentariu.setAdapter(comentariuAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        }
        if (author!=null){
            firebaseFirestore
                    .collection("/Comentarii_Autor/" + author.getIdAuthor() + "/lista/")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                Comentariu comentariu = queryDocumentSnapshot.toObject(Comentariu.class);
                                comentariuList.add(comentariu);

                            }
                            comentariuAdapter = new ComentariuAdapter(getContext(), comentariuList);
                            RvComentariu.setAdapter(comentariuAdapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        }
    }

    private void evClickAdaugaComentariu() {
        adauga_comentariu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comentariu_text.getText().toString()!="" && comentariu_text.getText().toString().isEmpty()==false){

                    if (book!=null) {

                        String comment_content = comentariu_text.getText().toString();
                        String uid = firebaseUser.getUid();
                        String uname = firebaseUser.getDisplayName();
                        String uimg;
                        if (firebaseUser.getPhotoUrl() != null) {
                            uimg = firebaseUser.getPhotoUrl().toString();
                        } else {
                            uimg = "";
                        }

                        Comentariu comentariu = new Comentariu(comment_content, uid, uimg, uname);
                        getDataFromFireBase.adauga_comentariu(comentariu, book.getIdBook());
                        comentariu_text.setText("");
//                    showMessage("comment added");
                        comentariuList.clear();
                        initRecycleView();

                    }

                    if (author!=null){
                        String comment_content = comentariu_text.getText().toString();
                        String uid = firebaseUser.getUid();
                        String uname = firebaseUser.getDisplayName();
                        String uimg;
                        if (firebaseUser.getPhotoUrl() != null) {
                            uimg = firebaseUser.getPhotoUrl().toString();
                        } else {
                            uimg = "";
                        }

                        Comentariu comentariu = new Comentariu(comment_content, uid, uimg, uname);
                        getDataFromFireBase.adauga_comentariu_autor(comentariu, author.getIdAuthor());
                        comentariu_text.setText("");
//                    showMessage("comment added");
                        comentariuList.clear();
                        initRecycleView();
                    }

                }
            }
        });
    }




    private void getBookFromBookDetailsFragment() {
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(BookDetailsFragment.COMENTARIU_BOOK);
    }

    private void getAuthorFromAuthorDetailsFragment() {
        Bundle bundle = getArguments();
        author = (Author) bundle.getSerializable(AuthorDetailsFragment.COMENTARIU_AUTOR);
    }

}