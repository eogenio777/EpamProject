package main.java.com.podruzhinskii.domain;

public class Periodical {
    private Long id;
    private String name;
    private String about;


    public Periodical(Long id, String name, String about) {
        this.id = id;
        this.name = name;
        this.about = about;
    }

    public Periodical() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
