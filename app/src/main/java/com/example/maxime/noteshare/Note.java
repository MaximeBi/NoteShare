package com.example.maxime.noteshare;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kapil on 12/05/2016.
 */
public class Note implements Serializable {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private String id;
    private String title;
    private String content;
    private String author;
    private ArrayList<String> keywords;
    private ArrayList<String> collaborators;
    private Date creationDate;
    private Date lastUpdate;

    public Note() {
        this.keywords = new ArrayList<String>();
        this.collaborators = new ArrayList<String>();
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.keywords = new ArrayList<String>();
        this.collaborators = new ArrayList<String>();
        this.creationDate = new Date();
        this.lastUpdate = new Date();
        this.id = createId();
    }

    public Note(Note n) {
        this.title = n.title;
        this.content = n.content;
        this.author = n.author;
        this.keywords = new ArrayList<String>();
        this.keywords.addAll(n.keywords);
        this.collaborators = new ArrayList<String>();
        this.collaborators.addAll(n.collaborators);
        this.creationDate = new Date();
        this.lastUpdate = new Date();
        this.id = createId();
    }

    public String createId() {
        if(id == null) {
            if(title != null) {
                return title + "_" + DATE_FORMAT.format(creationDate);
            } else if (content != null) {
                return (content.split(" "))[0] + "_" + DATE_FORMAT.format(creationDate);
            }
        }
        return null;
    }

    public void update(Note n) {
        this.title = n.title;
        this.content = n.content;
        this.keywords.clear();
        this.keywords.addAll(n.keywords);
        this.collaborators.clear();
        this.collaborators.addAll(n.collaborators);
        this.lastUpdate = new Date();
    }

    public void copy(Note n) {
        this.title = n.title;
        this.content = n.content;
        this.author = n.author;
        this.keywords.clear();
        this.keywords.addAll(n.keywords);
        this.collaborators.clear();
        this.collaborators.addAll(n.collaborators);
        this.creationDate = n.creationDate;
        this.lastUpdate = n.lastUpdate;
        this.id = n.id;
    }

    public void reset() {
        this.title = null;
        this.content = null;
        this.keywords.clear();
        this.collaborators.clear();
        this.creationDate = null;
        this.lastUpdate = null;
        this.id = null;

    }

    public boolean hasId() {
        return id != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public ArrayList<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(ArrayList<String> collaborators) {
        this.collaborators = collaborators;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
