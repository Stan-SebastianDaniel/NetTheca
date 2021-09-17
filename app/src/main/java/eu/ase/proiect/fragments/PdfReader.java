package eu.ase.proiect.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.LinkCapitole;
import eu.ase.proiect.database.model.Recenzie;
import eu.ase.proiect.FireDatabase.getDataFromFireBase;
import io.perfmark.Link;


public class PdfReader extends Fragment {

    private TextView pdftext;
    private PDFView pdfView;
    private Book book;
    // folosit pt determinare capitolului
    private int capitol;
    List<LinkCapitole> linkCapitoleList=new ArrayList<>();
    //-----------------
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();




    public PdfReader() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String value = snapshot.getValue(String.class);
//                pdftext.setText(value);
//                String url = pdftext.getText().toString();
//                new RetrievePdfStream().execute(url);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pdf_reader, container, false);
//        pdftext = view.findViewById(R.id.pdftext);
//        pdfView = view.findViewById(R.id.pdfviewer);
//        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                pdfView.fromUri(task.getResult()).load();
//            }
//        });
        //getPdf(reference.getPath());



//        String value = reference.getPath();
//        pdftext.setText(value);
//        String url = pdftext.getText().toString();
//        new RetrievePdfStream().execute(url);

        //luam carte din fragmentul BookDetails
        getBookFromAllBookFragment();

        // verficam daca este comic
        if (book.getIs_comic()==0) {



            WebView webView = view.findViewById(R.id.pdf_webciew);
            final ProgressBar progressBar = view.findViewById(R.id.pdf_progressbar);
            progressBar.setVisibility(View.VISIBLE);
            String url = "";
            if (book.getPdfUrl() != "") {
                url = book.getPdfUrl();
            } else {
                arataMesaj("Nu a luat Url-ul");
            }
            webView.getSettings().setJavaScriptEnabled(true);
            //webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

            });
            String newurl = "";
            try {
                newurl = URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String finalUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + newurl;
            webView.loadUrl(finalUrl);
        }
        else if (book.getIs_comic()==1){


//            Task<QuerySnapshot> docRef=firebaseFirestore.collection("CapitoleComic")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()){
//                                for (QueryDocumentSnapshot document: task.getResult())
//                                {
//                                    LinkCapitole capitole = document.toObject(LinkCapitole.class);
//                                    linkCapitoleList.add(capitole);
//                                }
//                            }
//                        }
//                    });


            //alta modaliate
            firebaseFirestore.collection("CapitoleComic"+"/"+book.getIdBook()+"/"+capitol)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots){
                                LinkCapitole link = queryDocumentSnapshot.toObject(LinkCapitole.class);
                                linkCapitoleList.add(link);

                            }




                            WebView webView = view.findViewById(R.id.pdf_webciew);
                            final ProgressBar progressBar = view.findViewById(R.id.pdf_progressbar);
                            progressBar.setVisibility(View.VISIBLE);
                            webView.getSettings().setJavaScriptEnabled(true);
                            webView.getSettings().setBuiltInZoomControls(true);
                            webView.getSettings().setAllowFileAccessFromFileURLs(true);
                            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                            webView.getSettings().setLoadWithOverviewMode(true);
                            webView.getSettings().setUseWideViewPort(true);
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                }

                            });
                            String newurl = "";
                            String urlnou=linkCapitoleList.get(0).getPdfUrl();
                            try {
                                newurl = URLEncoder.encode(urlnou, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String finalUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + newurl;
                            webView.loadUrl(finalUrl);



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.d("esec","eroarea este "+e);
                }
            });

//            getDataFromFireBase.get_Capitol2(book,linkCapitoleList,capitol);


        }

        return view;
    }

    //    preluare book / author din allbookFragment / favoriteBooksFragmnet
    private void getBookFromAllBookFragment() {
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(BookDetailsFragment.BOOK_WITH_PDF_KEY);
        if(book != null ) {
          //TODO

        } else {

            getCapitolComic();
//            Toast.makeText(getContext().getApplicationContext(), R.string.error_message_transfer_between_fragment,Toast.LENGTH_LONG).show();
        }
    }

    // Obtinere capitol
    private void getCapitolComic(){
        Bundle bundle = getArguments();
        book = (Book)bundle.getSerializable(CapitoleComicFragment.COMIC);
        capitol= (int) bundle.getSerializable(CapitoleComicFragment.NRCAPITOL);

    }

    private void arataMesaj(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }
//    class RetrievePdfStream extends AsyncTask<String,Void,InputStream>{
//
//        @Override
//        protected InputStream doInBackground(String... strings) {
//            InputStream inputStream=null;
//            try {
//                URL url=new URL (strings[0]);
//                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
//                if (urlConnection.getResponseCode()==200){
//                    inputStream=new BufferedInputStream(urlConnection.getInputStream());
//
//                }
//            }catch (IOException e){
//                return null;
//            }
//            return inputStream;
//        }
//
//        @Override
//        protected void onPostExecute(InputStream inputStream) {
//            pdfView.fromStream(inputStream).load();
//        }
//    }
}

//    private void getPdf(final String url) {
//
//        pdfView.setVisibility(View.VISIBLE);
//        final InputStream[] input = new InputStream[1];
//
//        new AsyncTask<Void, Void, Void>() {
//            @SuppressLint({"WrongThread", "StaticFieldLeak"})
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    input[0] = new URL(url).openStream();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                pdfView.fromStream(input[0])
//                        .pageFitPolicy(FitPolicy.WIDTH)
//                        .enableDoubletap(false)
//                        .load();
//            }
//        }.execute();
//
//    }
//}