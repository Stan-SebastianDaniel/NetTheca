package eu.ase.proiect.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.service.BookService;
import eu.ase.proiect.util.BookAdapter;


public class CapitoleComicFragment extends Fragment {

    public static String COMIC="trimite_comic";
    public static String NRCAPITOL="trimite_nrCapitol";

    private ListView listViewCapitoleComicFragment;
    //temporar edittext
    private EditText searchBar;

    private Book book;
    private List<String> listCapitoleComic = new ArrayList<>();

    //pop-up Filtrare
    private Dialog PopUpFiltrare;
    private TextView pop_up_alfabetic,pop_up_scor,pop_up_nrRecenzii;
    private RadioGroup pop_up_radioGroup_alfabetic,pop_up_radioGroup_scor,pop_up_radioGroup_nrRecenzii;
    private RadioButton pop_up_radioButton_alfabetic_crescator,pop_up_radioButton_alfabetic_descrescator,pop_up_radioButton_alfabetic_neutru
            ,pop_up_radioButton_scor_crescator,pop_up_radioButton_scor_descrescator,pop_up_radioButton_scor_neutru
            ,pop_up_radioButton_nrRecenzii_crescator,pop_up_radioButton_nrRecenzii_descrescator,pop_up_radioButton_nrRecenzii_neutru;
    private Button pop_up_Filtreaza;
    //----------------------------------


    public CapitoleComicFragment() {
        // Required empty public constructor
    }

    public static CapitoleComicFragment newInstance(String param1, String param2) {
        CapitoleComicFragment fragment = new CapitoleComicFragment();
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
        View view = inflater.inflate(R.layout.fragment_capitole_comic, container, false);
        listCapitoleComic.clear();
        initComponents(view);

        listViewCapitoleComicFragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                PdfReader frg2 = new PdfReader();
                Bundle bundle = new Bundle();
                bundle.putSerializable(COMIC, book);
                int capitol=position+1;
                bundle.putSerializable(NRCAPITOL,capitol);
                frg2.setArguments(bundle);
                ft.replace(R.id.main_frame_container, frg2);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //Search bar
        Filtrare();

        return view;
    }

    private void initComponents(View view) {
        //initializare view
        listViewCapitoleComicFragment = view.findViewById(R.id.lv_book_CapitoleComic_fragment);

        // obtinere Comic
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(BookDetailsFragment.BOOK_WITH_PDF_KEY);
        // Creeare lista capitole
        for (int i=0;i<book.getPages();i++){
            String capitol="Capitolul "+(i+1);
            listCapitoleComic.add(capitol);
        }
//        // definire adapter pentru lista
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, listCapitoleComic);
//        // Asignare adaptor pentru Lista
//        listViewCapitoleComicFragment.setAdapter(adapter);
        addAdaptorLista();


        //temp serachbar
        searchBar=view.findViewById(R.id.editTextTextCautareCapitoleComicFragment);
    }

    private void addAdaptorLista() {
        // definire adapter pentru lista
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext().getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, listCapitoleComic);
        // Asignare adaptor pentru Lista
        listViewCapitoleComicFragment.setAdapter(adapter);
    }

    //     testare filtrare
    private void Filtrare() {

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> capitoleNoi = new ArrayList<>();
                for (String capitolN : listCapitoleComic) {
                    if (capitolN.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        capitoleNoi.add(capitolN);
                    }
                }

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, capitoleNoi);
                listViewCapitoleComicFragment.setAdapter(adapter1);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}