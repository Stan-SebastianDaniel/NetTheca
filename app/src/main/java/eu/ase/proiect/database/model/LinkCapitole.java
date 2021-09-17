package eu.ase.proiect.database.model;

import java.io.Serializable;

public class LinkCapitole implements Serializable {
    private String pdfUrl;

    LinkCapitole(){}

    public LinkCapitole(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
