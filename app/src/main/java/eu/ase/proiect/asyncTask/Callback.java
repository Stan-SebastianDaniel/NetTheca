package eu.ase.proiect.asyncTask;

public interface Callback<R> {
    void runResultOnUiThread(R result);
}
