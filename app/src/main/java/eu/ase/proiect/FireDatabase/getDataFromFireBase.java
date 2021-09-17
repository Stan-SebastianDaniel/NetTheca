package eu.ase.proiect.FireDatabase;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Comentariu;
import eu.ase.proiect.database.model.LinkCapitole;
import eu.ase.proiect.database.model.Recenzie;

public class getDataFromFireBase {

    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    public DatabaseReference databaseReference=firebaseDatabase.getReference("Carti");
    public static String TAG;

//    public static void getaBook(final ArrayList<Book> listBooks) {
//        firebaseFirestore.collection("Carti").document("solo_leveling")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                      Book  carte = documentSnapshot.toObject(Book.class);
//                      listBooks.add(carte);
//                    }
//                });
//    }

    public static void getBooks(final ArrayList<Book> listBooks){
        listBooks.clear();
        firebaseFirestore.collection("Carti").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Book carte = document.toObject(Book.class);
                        listBooks.add(carte);
                    }
                }else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void getAuthors(final List<Author> listaAutori)
    {
        listaAutori.clear();
        firebaseFirestore.collection("Autori").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult())
                    {
                        Author autor = document.toObject(Author.class);
                        listaAutori.add(autor);
                    }
                }
            }
        });
    }

    public static void get_recenzii(final List<Recenzie> listaRecenzii, long idCarte){
        firebaseFirestore
//                .collection("Recenzii")
//                .document(String.valueOf(idCarte))
                .collection("/Recenzii/"+idCarte+"/lista/")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: task.getResult())
                    {
                        Recenzie recenzie = document.toObject(Recenzie.class);
                        listaRecenzii.add(recenzie);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"ma tai pe vene");
            }
        });

    }

    public static void adauga_recenzie(Recenzie recenzie, long idCarte){
        firebaseFirestore.collection("Recenzii"+"/"+idCarte+"/lista").add(recenzie).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"a mers");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"nu a mers");
            }
        });
    }

    public static void adauga_recenzie_Autor(Recenzie recenzie, long idAutor){
        firebaseFirestore.collection("Recenzii_Autor"+"/"+idAutor+"/lista").add(recenzie).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"a mers");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"nu a mers");
            }
        });
    }

    public static void adauga_comentariu(Comentariu comentariu, long idCarte){
        firebaseFirestore.collection("Comentarii"+"/"+idCarte+"/lista").add(comentariu).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"a mers");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"nu a mers");
            }
        });
    }

    public static void adauga_comentariu_autor(Comentariu comentariu, long idCarte){
        firebaseFirestore.collection("Comentarii_Autor"+"/"+idCarte+"/lista").add(comentariu).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG,"a mers");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d(TAG,"nu a mers");
            }
        });
    }

    public static void get_Capitol(Book comic,final List<LinkCapitole> linkCapitoleList,int nrCapitol){
        firebaseFirestore.collection("CapitoleComic"+"/"+comic.getIdBook()+"/"+nrCapitol)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult())
                            {
                                LinkCapitole link = document.toObject(LinkCapitole.class);
                                linkCapitoleList.add(link);
                            }
                        }
                    }
                });
    }
    public static void get_Capitol2(Book comic,final List<LinkCapitole> linkCapitoleList,int nrCapitol){
        firebaseFirestore.collection("CapitoleComic"+"/"+comic.getIdBook()+"/"+nrCapitol)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                            LinkCapitole link = queryDocumentSnapshot.toObject(LinkCapitole.class);
                            linkCapitoleList.add(link);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.d("esec","eroarea este "+e);
            }
        });
    }

    // descarcare capitole Audiobook
    public static void get_Capitole_audiobook(long idBook, final List<String> capitole, final List<Long> timpi){
        String idCarte=idBook+"";
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
                        }
                    }
                });
    }


   /* public static void getaCarteDinDB(List<Book> listBooks)
    {
        databaseReference.child().
    }*/


    }
