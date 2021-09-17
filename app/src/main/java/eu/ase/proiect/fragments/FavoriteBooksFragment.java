package eu.ase.proiect.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.service.AuthorService;
import eu.ase.proiect.database.service.BookService;
import eu.ase.proiect.util.BookAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteBooksFragment extends Fragment implements Serializable {

    public static final String BOOK_DETAILS_KEY = "book_details_key";
    public static final String AUTHOR_DETAILS_KEY = "author_details_key";

    private ListView lvFavoriteBooks;
    private List<Book> listFavoriteBooks = new ArrayList<>();
    private List<Author> listAuthors = new ArrayList<>();
    private BookService bookService;
    private AuthorService authorService;

    //temporar edittext
    private EditText searchBar;

    //pop-up Filtrare
    private Dialog PopUpFiltrare;
    private TextView pop_up_alfabetic,pop_up_scor,pop_up_nrRecenzii;
    private RadioGroup pop_up_radioGroup_alfabetic,pop_up_radioGroup_scor,pop_up_radioGroup_nrRecenzii;
    private RadioButton pop_up_radioButton_alfabetic_crescator,pop_up_radioButton_alfabetic_descrescator,pop_up_radioButton_alfabetic_neutru
            ,pop_up_radioButton_scor_crescator,pop_up_radioButton_scor_descrescator,pop_up_radioButton_scor_neutru
            ,pop_up_radioButton_nrRecenzii_crescator,pop_up_radioButton_nrRecenzii_descrescator,pop_up_radioButton_nrRecenzii_neutru;
    private Button pop_up_Filtreaza;
    //----------------------------------
    //pop-up Categorii
    private Dialog PopUpCategorii;
    List<String> categorii=new ArrayList<>();
    ListView listaCategorii;
    //-----------------


    public FavoriteBooksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_books, container, false);

        initComponents(view);

//      click pe item din list view
        lvFavoriteBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // trimit obiectul book in fragmentul BookDetailsFragment
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                BookDetailsFragment frg2 = new BookDetailsFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(BOOK_DETAILS_KEY, listFavoriteBooks.get(position));
                bundle.putSerializable(AUTHOR_DETAILS_KEY, getAuthorMeetBook(listFavoriteBooks.get(position).getIdAuthor()));
                frg2.setArguments(bundle);
                ft.replace(R.id.main_frame_container, frg2);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Filtrare();

        return view;
    }

    private void initComponents(View view) {


        lvFavoriteBooks = view.findViewById(R.id.lv_favorite_book);

        bookService = new BookService(getContext().getApplicationContext());
        authorService = new AuthorService(getContext().getApplicationContext());

        //      Preluare lista carti favorite din Db SQLite
        bookService.getAllFavoriteBooks(getAllFavoriteBooksDbCallback());
        authorService.getAll(getAllAuthorsDbCallback());

        //        setez titlu
        ((MainActivity) getActivity()).setActionBatTitle(getString(R.string.title_favorite_book));

        addBookAdapter();

        searchBar=view.findViewById(R.id.editTextTextCautareFavoriteFragment);

        //pop up de filtrare
        initPopUpFiltrare();
        //pop up de Categorii
        initPopUpCategorii();
    }

    private void initPopUpCategorii() {
        PopUpCategorii = new Dialog(getContext());
        PopUpCategorii.setContentView(R.layout.pop_up_categori);
        // popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Drawable d = new ColorDrawable(Color.WHITE);
//        d.setAlpha(130);
        PopUpCategorii.getWindow().setBackgroundDrawable(d);
        PopUpCategorii.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        PopUpCategorii.getWindow().getAttributes().gravity = Gravity.CENTER;

        categorii.add("Fictiune");
        categorii.add("Aventura");
        categorii.add("Fantezie");
        categorii.add("Romanta");
        categorii.add("Drama");
        categorii.add("Thriller");
        categorii.add("Actiune");

        listaCategorii=PopUpCategorii.findViewById(R.id.lista_categorii);

        // definire adapter pentru lista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext().getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, categorii);
        // Asignare adaptor pentru Lista
        listaCategorii.setAdapter(adapter);

        listaCategorii.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Book> listatemp=new ArrayList<>();
                for (Book carte:listFavoriteBooks) {
                    if (carte.getCategorii()!=null) {
                        String categorii_carte = carte.getCategorii();
                        String categorie_selectata = categorii.get(position);
                        if (categorii_carte.contains(categorie_selectata) == true) {
                            listatemp.add(carte);
                        }
                    }
                }

                BookAdapter adapter1 = new BookAdapter(getContext().getApplicationContext(), R.layout.item_book, listatemp, listAuthors, getLayoutInflater());
                lvFavoriteBooks.setAdapter(adapter1);

                PopUpCategorii.hide();
            }
        });

    }


    private void initPopUpFiltrare() {
        PopUpFiltrare = new Dialog(getContext());
        PopUpFiltrare.setContentView(R.layout.pop_up_filtrare_menu_item);
        // popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        PopUpFiltrare.getWindow().setBackgroundDrawable(d);

        PopUpFiltrare.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        PopUpFiltrare.getWindow().getAttributes().gravity = Gravity.TOP;

        pop_up_alfabetic=PopUpFiltrare.findViewById(R.id.textViewAlfabetic);
        pop_up_scor=PopUpFiltrare.findViewById(R.id.textViewScor);
        pop_up_nrRecenzii=PopUpFiltrare.findViewById(R.id.textViewNrRecenzii);

        pop_up_radioGroup_alfabetic=PopUpFiltrare.findViewById(R.id.radioGroupAlfabetic);
        pop_up_radioButton_alfabetic_crescator=PopUpFiltrare.findViewById(R.id.alfabeticCrescator);
        pop_up_radioButton_alfabetic_descrescator=PopUpFiltrare.findViewById(R.id.alfabeticDescrescator);
        pop_up_radioButton_alfabetic_neutru=PopUpFiltrare.findViewById(R.id.alfabeticNeutru);

        pop_up_radioGroup_scor=PopUpFiltrare.findViewById(R.id.radioGroupScor);
        pop_up_radioButton_scor_crescator=PopUpFiltrare.findViewById(R.id.ScorCrescator);
        pop_up_radioButton_scor_descrescator=PopUpFiltrare.findViewById(R.id.ScorDescrescator);
        pop_up_radioButton_scor_neutru=PopUpFiltrare.findViewById(R.id.ScorNeutru);

        pop_up_radioGroup_nrRecenzii=PopUpFiltrare.findViewById(R.id.radioGroupNrRecenzii);
        pop_up_radioButton_nrRecenzii_crescator=PopUpFiltrare.findViewById(R.id.NrRecenziiCrescator);
        pop_up_radioButton_nrRecenzii_descrescator=PopUpFiltrare.findViewById(R.id.NrRecenziiDescrescator);
        pop_up_radioButton_nrRecenzii_neutru=PopUpFiltrare.findViewById(R.id.NrRecenziiNeutru);

        pop_up_Filtreaza=PopUpFiltrare.findViewById(R.id.button_Menu_Filtrare);


        pop_up_Filtreaza.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                List<Book> listatemp=new ArrayList<>();
                listatemp=listFavoriteBooks;

                int alfabetic_check=pop_up_radioGroup_alfabetic.getCheckedRadioButtonId();
                switch (alfabetic_check){
                    case R.id.alfabeticCrescator:
                        listatemp.sort(Comparator.comparing(Book::getTitle));
                        break;
                    case R.id.alfabeticDescrescator:
                        listatemp.sort(Comparator.comparing(Book::getTitle).reversed());
                        break;
                }

                int scor_check=pop_up_radioGroup_scor.getCheckedRadioButtonId();
                switch (scor_check){
                    case R.id.ScorCrescator:
                        listatemp.sort(Comparator.comparing(Book::getRating));
                        break;
                    case R.id.ScorDescrescator:
                        listatemp.sort(Comparator.comparing(Book::getRating).reversed());
                        break;
                }

                int nrRecenzii_check=pop_up_radioGroup_nrRecenzii.getCheckedRadioButtonId();
                switch (nrRecenzii_check){
                    case R.id.NrRecenziiCrescator:
                        listatemp.sort(Comparator.comparing(Book::getReview));
                        break;
                    case R.id.NrRecenziiDescrescator:
                        listatemp.sort(Comparator.comparing(Book::getReview).reversed());
                        break;
                }


                listFavoriteBooks= (ArrayList<Book>) listatemp;
                notifyInternalAdapter();
                PopUpFiltrare.hide();
            }
        });

    }

    // initiere menu cu subiteme pentru Categorie si Filtrare
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categorie_filtrare,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        int id_item_menu=item.getItemId();

        if (id_item_menu==R.id.Categorie){
            PopUpCategorii.show();
        }

        if (id_item_menu==R.id.Filtrare){
            PopUpFiltrare.show();
            return true;
        }
        //original return super.onOptionsItemSelected(item);
        return true;
    }

    //     testare filtrare
    private void Filtrare() {

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Book> booksNoi=new ArrayList<>();
                for(Book bookN : listFavoriteBooks)
                {
                    if(bookN.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        booksNoi.add(bookN);
                    }
                }

                BookAdapter adapter1 = new BookAdapter(getContext().getApplicationContext(), R.layout.item_book, booksNoi, listAuthors, getLayoutInflater());
                lvFavoriteBooks.setAdapter(adapter1);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }




    private void addBookAdapter(){
        BookAdapter bookAdapter = new BookAdapter(getContext().getApplicationContext(), R.layout.item_book, listFavoriteBooks, listAuthors, getLayoutInflater());
        lvFavoriteBooks.setAdapter(bookAdapter);
    }

    public void notifyInternalAdapter(){
        ArrayAdapter adapter =  (ArrayAdapter) lvFavoriteBooks.getAdapter();
        adapter.notifyDataSetChanged();
    }

    /*************          DATABASE         ******************/
    private Callback<List<Book>> getAllFavoriteBooksDbCallback(){
        return new Callback<List<Book>>() {
            @Override
            public void runResultOnUiThread(List<Book> result) {
                if(result != null){
                    listFavoriteBooks.clear();
                    listFavoriteBooks.addAll(result);
                    notifyInternalAdapter();
                }
            }
        };
    }

    private Callback<List<Book>> getAllBooksFavoriteDbCallback(){
        return new Callback<List<Book>>() {
            @Override
            public void runResultOnUiThread(List<Book> result) {
                listFavoriteBooks.clear();
                listFavoriteBooks.addAll(result);
                notifyInternalAdapter();
            }
        };
    }

    private Callback<List<Author>> getAllAuthorsDbCallback(){
        return new Callback<List<Author>>() {
            @Override
            public void runResultOnUiThread(List<Author> result) {
                if(result!=null){
                    listAuthors.clear();
                    listAuthors.addAll(result);

                }
                else {
                    Toast.makeText(getContext().getApplicationContext(), R.string.error_extract_authors_fromDB_Sqlite,
                            Toast.LENGTH_SHORT).show();
                }

            }
        };
    }


    private Author getAuthorMeetBook(long idAuthor){
        for (Author a: listAuthors) {
            if(a.getIdAuthor() == idAuthor){
                return a;
            }
        }
        return null;
    }


}