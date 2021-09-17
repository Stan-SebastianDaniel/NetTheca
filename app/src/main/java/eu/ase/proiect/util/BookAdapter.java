package eu.ase.proiect.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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

import java.util.List;

import eu.ase.proiect.Glide.MyAppGlideModule;
import eu.ase.proiect.R;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.service.AuthorService;
import eu.ase.proiect.database.service.BookService;

public class BookAdapter extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> listBooks;
    private List<Author> listAuthors;
    private LayoutInflater inflater;
    private int resource;

    public BookAdapter(@NonNull Context context,
                       int resource,
                       @NonNull List<Book> books,
                       List<Author> authors,
                       LayoutInflater inflater) {
        super(context, resource, books);
        this.context = context;
        this.resource = resource;
        this.listBooks = books;
        this.listAuthors = authors;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource,parent,false);
        Book book = listBooks.get(position);

        if(book != null) {
            addBookTitle(view,book.getTitle());
            addBookAuthor(view, getNameAuthor(book.getIdAuthor()));
            addRatingBar(view, book.getRating(),book.getReview());
            addNbPages(view, book.getPages(), book.getReview(),book.getIs_comic());

            addBookImg(view, book.getImgUrl(), book.getDrawableResource());

            if(book.getIs_favorite()==1){
                addFavoriteImg(view, true);
            }
        }
        return view;
    }

    private void addBookTitle(View view, String title) {
        TextView textView = view.findViewById(R.id.item_book_title);
        populateFromView(title, textView);
    }

    private void addBookAuthor(View view, String author) {
        TextView textView = view.findViewById(R.id.item_book_author);
        populateFromView(author, textView);
    }

    private void addRatingBar(View view, float rating, int review){
        RatingBar ratingBar = view.findViewById(R.id.item_book_ratingbar);
        if(rating >= 0){
            ratingBar.setRating(rating/review);
        } else{
            ratingBar.setRating(0);
        }
    }

    private void addNbPages(View view, int pages, int review, int is_comic){
        TextView textView = view.findViewById(R.id.item_book_pagesrev);
        if (pages >= 0 && review >=0 && is_comic==0) {
            textView.setText(pages + " pagini | " + review + " recenzii");
        }
        else if (pages >= 0 && review >=0 && is_comic==1){
            textView.setText(pages + " capitole | " + review + " recenzii");
        }
        else {
            textView.setText(R.string.no_content);
        }
    }

    private void addBookImg(View view, String imgUrl, int drawableResource) {
        final ImageView imageView = view.findViewById(R.id.item_book_img);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        try {
            StorageReference storageReference = storage.getReference().child("Img_Carti/" + imgUrl);
            Glide.with(context).load(storageReference).into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_uploading_photo);
            e.printStackTrace();
        }
    }

    private void addFavoriteImg(View view, boolean fav){
        ImageView imageView = view.findViewById(R.id.item_img_favorite);
        if (fav == true) {
            imageView.setImageResource(R.drawable.ic_favorite_red_24);
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

    }

    private void populateFromView(String string, TextView textView) {
        if (string != null && !string.trim().isEmpty()) {
            textView.setText(string);
        } else {
            textView.setText(R.string.no_content);
        }
    }

    private String getNameAuthor(long idFkAuthor){
        String nameAuthor="";
        for (Author a: listAuthors) {
            if(a.getIdAuthor() == idFkAuthor){
                nameAuthor = a.getNameAuthor();
                break;
            }
        }
        return nameAuthor;
    }


}
