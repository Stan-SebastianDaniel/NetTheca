package eu.ase.proiect.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import eu.ase.proiect.database.dao.AuthorDao;
import eu.ase.proiect.database.dao.BookDao;
import eu.ase.proiect.database.model.Book;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.util.DateConverter;

@Database(entities = {Book.class, Author.class}, exportSchema = false, version = 9)
@TypeConverters({DateConverter.class})
public abstract class DatabaseManager extends RoomDatabase {

    public static final String BOOKS_DB = "booksDb";
    private static DatabaseManager databaseManager;

    public static DatabaseManager getInstance(Context context) {
        if(databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null) {
                    databaseManager = Room.databaseBuilder(context,
                            DatabaseManager.class,
                            BOOKS_DB).fallbackToDestructiveMigration().build();
                }
            }
        }
        return databaseManager;

    }

    public abstract BookDao getBookDao();

    public abstract AuthorDao getAuthorDao();

}
