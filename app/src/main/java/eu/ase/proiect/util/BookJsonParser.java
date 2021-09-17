package eu.ase.proiect.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;

public class BookJsonParser {

    public static final String ID_BOOK = "idBook";
    public static final String TITLE = "title";
    public static final String BOOK_DETAILS_FOR_JSON = "BookDetailsForJSON";
    public static final String DESCRIPTION = "description";
    public static final String IMG_URL_BOOK = "imgUrlBook";
    public static final String PAGES = "pages";
    public static final String REVIEW = "review";
    public static final String RATING = "rating";
    public static final String DRAWABLE_RESOURCE = "drawableResource";
    public static final String AUTHOR = "author";
    public static final String ID_AUTHOR = "idAuthor";
    public static final String NAME = "name";
    public static final String IMG_URL_AUTHOR = "imgUrlAuthor";
    public static final String SHORT_BIOGRAPHY = "shortBiography";
    public static final String PDF_URL = "pdfUrl";

    public static List<Book> fromJson (String json, List<Author> listAuthors){
        if(json == null || json.isEmpty()){
            return new ArrayList<>();
        }

        try {
            JSONArray array = new JSONArray(json);
            List<Book> resultsBooks = new ArrayList<>();

            for(int i=0; i < array.length(); i++){
                JSONObject object = array.getJSONObject(i);
                int idBook = object.getInt(ID_BOOK);
                String title = object.getString(TITLE);
                JSONObject objectBookDetails = object.getJSONObject(BOOK_DETAILS_FOR_JSON);
                    String description = objectBookDetails.getString(DESCRIPTION);
                    String imgUrlBook = objectBookDetails.getString(IMG_URL_BOOK);
                    int nbPages = objectBookDetails.getInt(PAGES);
                    int review = objectBookDetails.getInt(REVIEW);
                    float rating = (float) objectBookDetails.getDouble(RATING);
                    int drawableResource = objectBookDetails.getInt(DRAWABLE_RESOURCE);
                    int is_favorite = 0;
                    int is_read = 0;
                    String pdfUrl=objectBookDetails.getString(PDF_URL);
                    JSONObject objectAuthor = objectBookDetails.getJSONObject(AUTHOR);
                        int idAuthor = objectAuthor.getInt(ID_AUTHOR);
                        String nameAuthor = objectAuthor.getString(NAME);
                        String shortBiography = objectAuthor.getString(SHORT_BIOGRAPHY);
                        String imgUrlAuthor = objectAuthor.getString(IMG_URL_AUTHOR);
                Book book = new Book(idBook,title,description,imgUrlBook,nbPages,review,rating,drawableResource, is_favorite, is_read, idAuthor,pdfUrl,"",0,"");
                resultsBooks.add(book);

                Author author = new Author(idAuthor,nameAuthor,shortBiography,imgUrlAuthor);
                listAuthors.add(author);
            }
            return resultsBooks;

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();

    }


}
