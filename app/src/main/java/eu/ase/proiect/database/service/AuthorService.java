package eu.ase.proiect.database.service;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import eu.ase.proiect.asyncTask.AsyncTaskRunner;
import eu.ase.proiect.asyncTask.Callback;
import eu.ase.proiect.database.DatabaseManager;
import eu.ase.proiect.database.dao.AuthorDao;
import eu.ase.proiect.database.model.Author;
import eu.ase.proiect.database.model.Book;

public class AuthorService {
    private final AuthorDao authorDao;
    private final AsyncTaskRunner taskRunner;


    public AuthorService(Context context) {
        authorDao = DatabaseManager.getInstance(context).getAuthorDao();
        taskRunner = new AsyncTaskRunner();
    }

    public void getAll(Callback<List<Author>> callback){
        Callable<List<Author>> callable = new Callable<List<Author>>() {
            @Override
            public List<Author> call() {
                return authorDao.getAll();
            }
        };
        taskRunner.executeAsync(callable,callback);
    }


    public void insertAuthor(Callback<Author> callback, final Author author){
        Callable<Author> callable = new Callable<Author>() {
            @Override
            public Author call() {
                if(author == null){
                    return null;
                }
                long id = authorDao.insert(author);
                if(id == -1){
                    return null;
                }
                author.setIdAuthor(id);
                return author;
            }
        };
        taskRunner.executeAsync(callable,callback);
    }



    public void getNameAuthorById(Callback<String> callback, final long id){
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() {
                return authorDao.getNameAuthorFromIdBook(id);
            }
        };
        taskRunner.executeAsync(callable,callback);
    }

    public void delete(Callback<Integer> callback, final Author author){
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                if(author == null){
                    return -1;
                }
                return authorDao.delete(author);
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

    public void deleteById(Callback<Integer> callback, final long idAuthor){
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call(){
                if(idAuthor < 0){
                    return -1;
                }
                return authorDao.delete(idAuthor);
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

}
