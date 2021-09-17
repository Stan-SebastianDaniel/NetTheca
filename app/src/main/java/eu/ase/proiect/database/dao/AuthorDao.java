package eu.ase.proiect.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import eu.ase.proiect.database.model.Author;

@Dao
public interface AuthorDao {
    @Query("select * from authors")
    List<Author> getAll();

    //returneaza id ul inregistrarii sau -1 daca apar probleme
    @Insert
    long insert (Author author);

    //int ul rep. nr. de randuri afectate, -1 daca sunt probleme
    @Update
    int update (Author author);

    //int ul rep. nr. de randuri afectate, -1 daca sunt probleme
    @Delete
    int delete (Author author);

    @Query("DELETE FROM authors WHERE idAuthor = :id")
    int delete( long id);

    @Query("SELECT nameAuthor FROM authors WHERE idAuthor = :id")
    public String getNameAuthorFromIdBook(long id);


}
