package eu.ase.proiect.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import eu.ase.proiect.LoginActivity;
import eu.ase.proiect.MainActivity;
import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private Button btnSave;
    private CheckBox checkBox_save_data;
    private CheckBox checkBox_automatic_login;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button btnLogOut;
    private Button btnChangPassword;
    private Button Delogare;
    private SharedPreferences preferences;

    //date pop-up salvare parola
    FirebaseUser user;
    private Dialog popUp_schimba_parola;
    private EditText scrie_parola;
    private EditText confirma_parola;
    private Button save_parola;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initComponents(view);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLoginDataInSharedPreferences();
                Toast.makeText(getContext().getApplicationContext(), getString(R.string.succesfull_save),Toast.LENGTH_SHORT).show();
//                if(preferences.getBoolean(LoginActivity.SAVE_LOGIN_DATA,false)){
//                    btnLogOut.setVisibility(View.VISIBLE);
//                }
//                else{
//                    btnLogOut.setVisibility(View.INVISIBLE);
//                }
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        btnChangPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp_schimba_parola.show();
            }
        });

        return view;
    }

    private void initComponents(View view) {
        //        setez titlu
        ((MainActivity) getActivity()).setActionBatTitle(getString(R.string.title_settings));
        btnSave = view.findViewById(R.id.btn_save_data_login);
        btnLogOut=view.findViewById(R.id.btn_log_out);
        checkBox_save_data = view.findViewById(R.id.cb_save_data);
        checkBox_automatic_login = view.findViewById(R.id.cb_automatic_login);

        radioGroup=view.findViewById(R.id.grup_radio_setari);
        btnChangPassword=view.findViewById(R.id.btn_schimb_parola);
        btnLogOut=view.findViewById(R.id.btn_log_out);

        preferences = this.getActivity().getSharedPreferences(LoginActivity.PROFILE_SHARED_PREF, Context.MODE_PRIVATE);
        loadFromSharedPreference();
//        if(checkBox_automatic_login.isChecked()){
//            btnLogOut.setVisibility(View.INVISIBLE);
//        }
        user= FirebaseAuth.getInstance().getCurrentUser();

        initPopUp();

    }

    private void initPopUp() {
        popUp_schimba_parola = new Dialog(getContext());
        popUp_schimba_parola.setContentView(R.layout.pop_up_schimba_parola);
        // popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(130);
        popUp_schimba_parola.getWindow().setBackgroundDrawable(d);

        popUp_schimba_parola.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popUp_schimba_parola.getWindow().getAttributes().gravity = Gravity.TOP;


          scrie_parola=popUp_schimba_parola.findViewById(R.id.parola_noua);
          confirma_parola=popUp_schimba_parola.findViewById(R.id.parola_noua_confirma);
          save_parola=popUp_schimba_parola.findViewById(R.id.save_parola_noua);

          save_parola.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  String s=scrie_parola.getText().toString();
                  String s2=confirma_parola.getText().toString();
                  if (s.equals(s2)==true) {
                      user.updatePassword(scrie_parola.getText().toString());
                      popUp_schimba_parola.hide();
                  }
              }
          });
    }

    private void loadFromSharedPreference(){
        checkBox_save_data.setChecked(preferences.getBoolean(LoginActivity.SAVE_LOGIN_DATA, false));
        checkBox_automatic_login.setChecked(preferences.getBoolean(LoginActivity.AUTOMATIC_LOGIN, false));
    }

    private void saveLoginDataInSharedPreferences() {
//        scriu in fisierul de preferinta numele si parola
        boolean checkBoxSave = checkBox_save_data.isChecked();
        boolean checkBoxAutomaticLogin = checkBox_automatic_login.isChecked();

//                salvarea in fisierul de preferinte
        SharedPreferences.Editor editor =preferences.edit();
        editor.putBoolean(LoginActivity.SAVE_LOGIN_DATA,checkBoxSave);
        editor.putBoolean(LoginActivity.AUTOMATIC_LOGIN,checkBoxAutomaticLogin);
        editor.apply();
    }

}