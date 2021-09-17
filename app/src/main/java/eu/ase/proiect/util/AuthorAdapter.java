package eu.ase.proiect.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.context.AttributeContext;

import java.util.List;

import eu.ase.proiect.R;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;

public class AuthorAdapter extends ArrayAdapter<Author> {
    private Context context;
    private List<Author> listAuthors;
    private LayoutInflater inflater;
    private int resource;

    public AuthorAdapter(@NonNull Context context,
                         int resource,
                         @NonNull List<Author> authors,
                         LayoutInflater inflater) {
        super(context, resource, authors);
        this.context = context;
        this.listAuthors = authors;
        this.inflater = inflater;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource,parent,false);

        Author author = listAuthors.get(0);
        if(author != null) {
            addBookAuthor(view, author.getNameAuthor());
            addBookImg(view, author.getImgUrlAuthor());
        }
        return view;
    }


    private void addBookAuthor(View view, String author) {
        TextView textView = view.findViewById(R.id.item_author_name);
        populateFromView(author, textView);
    }

    private void addBookImg(View view, String imgUrl) {
        final ImageView imageView = view.findViewById(R.id.item_author_img);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        try {
            StorageReference storageReference = storage.getReference().child("Img_Autori/" + imgUrl);
            Glide.with(context).load(storageReference).into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_uploading_photo);
            e.printStackTrace();
        }
    }

    private void populateFromView(String string, TextView textView) {
        if (string != null && !string.trim().isEmpty()) {
            textView.setText(string);
        } else {
            textView.setText(R.string.no_content);
        }
    }


}
