package eu.ase.proiect.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import eu.ase.proiect.database.model.Book;

@Dao
public interface BookDao {

    @Query("select * from books")
    List<Book> getAll();

    @Query("select * from books")
    List<Book> getAllFavoriteBooks();

    @Query("SELECT * FROM books WHERE books.idAuthor = :id")
    List<Book> getBooksWithIdAuthor(long id);

    //returneaza id ul inregistrarii sau -1 daca apar probleme
    @Insert
    long insert (Book book);


    //int ul rep. nr. de randuri afectate, -1 daca sunt probleme
    @Update
    int update (Book book);

    //int ul rep. nr. de randuri afectate, -1 daca sunt probleme
    @Delete
    int delete (Book book);

    @Query("DELETE FROM books WHERE idBook = :id")
    int deleteBookByIdBook(long id);

//    @Query("SELECT COUNT(*) FROM books WHERE books.idAuthor = :idAuthor")
//    int eachBooksHasAuthor(long idAuthor);

}
