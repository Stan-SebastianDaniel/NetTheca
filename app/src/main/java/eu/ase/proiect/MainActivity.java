package eu.ase.proiect;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import eu.ase.proiect.FireDatabase.getDataFromFireBase;
import eu.ase.proiect.asyncTask.AsyncTaskRunner;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.model.Recenzie;
import eu.ase.proiect.fragments.AllBooksFragment;
import eu.ase.proiect.fragments.AuthorDetailsFragment;
import eu.ase.proiect.fragments.CartiAudioFragment;
import eu.ase.proiect.fragments.CartiFragment;
import eu.ase.proiect.fragments.ComicFragment;
import eu.ase.proiect.fragments.FavoriteBooksFragment;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.fragments.SettingsFragment;
import eu.ase.proiect.network.HttpManager;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.util.BookJsonParser;

public class MainActivity extends AppCompatActivity {
            //old link https://jsonkeeper.com/b/8RH2
    public static String URL_BOOKS="https://jsonkeeper.com/b/DWCF";
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Fragment currentFragment;

    private AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    public static ArrayList<Book> listBooks = new ArrayList<>();
    public static ArrayList<Author> listAuthors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getDatedinFirebase();
        //testex sa vad daca porneste cu ce e in baza de date
//        getDataFromFireBase.getBooks(listBooks);
//        getDataFromFireBase.getAuthors(listAuthors);
        configNavigation();



        /***********    ACUM DATELE DE MAI JOS SUNT INCLUSE IN JSON     ****************/
////      lista allBooks este alcatuita din JSON si b, b1, b2 (hardcodate) (pe viitor va fi alc. din JSON si firebase)
//        Book b = new Book(100,"An American Marriage","Is a book about romance and sweeting love!",
//                "", 248, 11, 2.8f, R.drawable.book1, 0, 0, 202);
//        Book b1 = new Book(101,"The Great Gasby","This book live in last generation. It's abaout crime.",
//                "",308, 21, 4.2f, R.drawable.gatsby2, 0,0,203);
//        Book b2 = new Book(102,"The fault in our stars","Descriere amanuntita a cartii!",
//                "", 321, 34, 4.8f, R.drawable.thefault, 0, 0, 204);
//        Book b3 = new Book(103,"Silver Sparrow","Descriere amanuntita a cartii!",
//                "URLImage", 239, 19, 3.3f, R.drawable.silver_book, 0, 0, 202);
//          listBooks.add(b);
//          listBooks.add(b1);
//          listBooks.add(b2);
//          listBooks.add(b3);
//          Author a = new Author(202,"Toyari Jones","Scurta biografie despre autor", "");
//          Author a1 = new Author(203,"F. Scott Fitzgerald","Biografie despre autor, scurta","");
//          Author a2 = new Author(204,"Jhon Green","Scurta biografie despre autor222222","url nonfunctional");
//          listAuthors.add(a);
//          listAuthors.add(a1);
//          listAuthors.add(a2);


//                  Author a = new Author(200,"Ion Creanga","Scurta biografie despre autor", "url nonfunctional");
//        authorService.insertAuthor(insertAuthorIntoDbCallback(), a);
//                    Author a2 = new Author(201,"Ion Luca Caragiale","Scurta biografie despre autor2",
//                            "url nonfunctional");
//        authorService.insertAuthor(insertAuthorIntoDbCallback(), a2);

          //          bookService.delete(deleteBookFromDbCallback(),b);
//          bookService.delete(deleteBookFromDbCallback(),b1);
//          bookService.delete(deleteBookFromDbCallback(),b2);

//////        inserare carte in baza de date
//          bookService.insertBook(insertBookIntoDbCallback(), b);
//          bookService.insertBook(insertBookIntoDbCallback(), b1);
//          bookService.insertBook(insertBookIntoDbCallback(), b2);

//          bookService.getAll(getAllBooksDbCallback());

//      firebaseFirestore.collection("Carti").document("solo_leveling")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Book carte = documentSnapshot.toObject(Book.class);
//                        listBooks.add(carte);
//                    }
//                });


//        authorService.delete(deleteAuthorFromDbCallback(), listAuthors.get(0));
//        authorService.deleteById(deleteAuthorByIdFromDbCallback(), 200);
//        authorService.deleteById(deleteAuthorByIdFromDbCallback(), 201);
//
//       bookService.deleteByIdBook(deleteBookByIdBookFromDbCallback(),1000);
//
        //        Preluare carti (cu autori) din url
      //  getBooksFromNetwork();
        initComponents();
        openDefaultFragment(savedInstanceState);


    }

    public static void getDatedinFirebase() {
        getDataFromFireBase.getBooks(listBooks);
        getDataFromFireBase.getAuthors(listAuthors);
    }

    private void incarca_profil() {
        View navHeader=navigationView.getHeaderView(0);
        ImageView poza_profil=navHeader.findViewById(R.id.menu_imageView);
        TextView username_menu=navHeader.findViewById(R.id.menu_username);
        TextView usernmail_menu=navHeader.findViewById(R.id.menu_usermail);
        Uri uri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String mail=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Glide.with(navHeader).load(uri).into(poza_profil);
        username_menu.setText(name);
        usernmail_menu.setText(mail);
    }

    private void getBooksFromNetwork(){
        Callable<String> asyncOperation = new HttpManager(URL_BOOKS);
        Callback<String> mainThreadOperation = getMainThreadOperationForBooks();
        asyncTaskRunner.executeAsync(asyncOperation,mainThreadOperation);
    }


//    Preluare carti din JSON
    private Callback<String> getMainThreadOperationForBooks() {
        return new Callback<String>() {
            @Override
            public void runResultOnUiThread(String result) {
//                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                listBooks.addAll(BookJsonParser.fromJson(result, listAuthors));
                if (currentFragment instanceof AllBooksFragment) {
                    ((AllBooksFragment) currentFragment).notifyInternalAdapter();
                }
                if (currentFragment instanceof FavoriteBooksFragment){
                    ((FavoriteBooksFragment) currentFragment).notifyInternalAdapter();
                }
            }
        };
    }


    private void configNavigation() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

  }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater=getMenuInflater();
//        inflater.inflate(R.menu.temporar_menu,menu);
//        return true;
//    }

    private void initComponents() {
        navigationView=findViewById(R.id.nav_view);

       // select item din meniu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.nav_all_books){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = AllBooksFragment.newInstance(listBooks,listAuthors);
                    ft.replace(R.id.main_frame_container, currentFragment);
                    ft.commit();
                }
                else if(item.getItemId() == R.id.nav_Carti){
                    ArrayList<Book> listBooks_doar_carti=new ArrayList<>();
                    for (Book carte:listBooks) {
                        if (carte.getIs_comic()==0 && carte.getPdfUrl()!=""){
                            listBooks_doar_carti.add(carte);
                        }

                    }
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = CartiFragment.newInstance(listBooks_doar_carti,listAuthors);
                    ft.replace(R.id.main_frame_container, currentFragment);
                    ft.commit();
                }
                else if(item.getItemId() == R.id.nav_carti_audio){
                    ArrayList<Book> listBooks_doar_audio=new ArrayList<>();
                    for (Book carte:listBooks) {
                        if (carte.getAudioUrl()!="" && carte.getAudioUrl()!=null){
                            listBooks_doar_audio.add(carte);
                        }

                    }
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = CartiAudioFragment.newInstance(listBooks_doar_audio,listAuthors);
                    ft.replace(R.id.main_frame_container, currentFragment);
                    ft.commit();
                }
                else if(item.getItemId() == R.id.nav_Comics){
                    ArrayList<Book> listBooks_doar_comicuri=new ArrayList<>();
                    for (Book carte:listBooks) {
                        if (carte.getIs_comic()==1){
                            listBooks_doar_comicuri.add(carte);
                        }

                    }
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    currentFragment = ComicFragment.newInstance(listBooks_doar_comicuri,listAuthors);
                    ft.replace(R.id.main_frame_container, currentFragment);
                    ft.commit();
                }
                else if(item.getItemId() == R.id.nav_favorite){
                    currentFragment = new FavoriteBooksFragment();
                    openFragment();
                }
                 else if(item.getItemId() == R.id.nav_settings){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    currentFragment= SettingsFragment.newInstance();
                    ft.replace(R.id.main_frame_container, currentFragment);
                    ft.commit();
                }

//                Toast.makeText(getApplicationContext(),
//                        getString(R.string.show_option,item.getTitle()),
//                        Toast.LENGTH_SHORT).show();

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        //incerc sa fac load la poza de profil
        incarca_profil();
    }



//    setare titlu pentru fiecare fragment
    public void setActionBatTitle(String title){
        toolbar.setTitle(title);
    }



    /*********   FRAGMENTE    *********/
    private void openFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame_container, currentFragment)
                .commit();
    }

    private void openDefaultFragment(Bundle saveInstanceState){
        if(saveInstanceState == null) {
            currentFragment =  AllBooksFragment.newInstance(listBooks, listAuthors);
            openFragment();
            navigationView.setCheckedItem(R.id.nav_all_books);

        }
    }



}