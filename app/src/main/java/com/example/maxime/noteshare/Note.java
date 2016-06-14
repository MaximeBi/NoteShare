package com.example.maxime.noteshare;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Note implements Serializable, Comparable<Note> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private String id;
    private String title;
    private String content;
    private String author;
    private ArrayList<String> keywords;
    private ArrayList<String> collaborators;
    private Date creationDate;
    private Date lastUpdate;
    private ArrayList<String> smartWord;
    private ArrayList<String> smartDef;
    private Date serverVersionDate;

    public Note() {
        this.keywords = new ArrayList<>();
        this.collaborators = new ArrayList<>();
        this.smartWord = new ArrayList<>();
        this.smartDef = new ArrayList<>();
    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.keywords = new ArrayList<>();
        this.collaborators = new ArrayList<>();
        this.creationDate = new Date();
        this.lastUpdate = new Date();
        this.smartWord = new ArrayList<>();
        this.smartDef = new ArrayList<>();
        this.serverVersionDate = this.creationDate;
        this.id = createId();
    }

    public Note(Note n) {
        this.title = n.title;
        this.content = n.content;
        this.author = n.author;
        this.keywords = new ArrayList<>();
        this.keywords.addAll(n.keywords);
        this.collaborators = new ArrayList<>();
        this.collaborators.addAll(n.collaborators);
        this.creationDate = new Date();
        this.lastUpdate = new Date();
        this.smartWord = new ArrayList<>();
        this.smartWord.addAll(n.smartWord);
        this.smartDef = new ArrayList<>();
        this.smartDef.addAll(n.smartDef);
        this.serverVersionDate = this.creationDate;
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
        this.smartWord.clear();
        this.smartWord.addAll(n.smartWord);
        this.smartDef.clear();
        this.smartDef.addAll(n.smartDef);
        this.lastUpdate = new Date();
        this.serverVersionDate = this.creationDate;
    }

    public void copy(Note n) {
        this.title = n.title;
        this.content = n.content;
        this.author = n.author;
        this.keywords.clear();
        this.keywords.addAll(n.keywords);
        this.collaborators.clear();
        this.collaborators.addAll(n.collaborators);
        this.smartWord.clear();
        this.smartWord.addAll(n.smartWord);
        this.smartDef.clear();
        this.smartDef.addAll(n.smartDef);
        this.creationDate = n.creationDate;
        this.lastUpdate = n.lastUpdate;
        this.serverVersionDate = this.creationDate;
        this.id = n.id;
    }

    public void reset() {
        this.title = null;
        this.content = null;
        this.keywords.clear();
        this.collaborators.clear();
        this.creationDate = null;
        this.lastUpdate = null;
        this.smartWord.clear();
        this.smartDef.clear();
        this.serverVersionDate = this.creationDate;
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

    public ArrayList<String> getSmartWord() {
        return smartWord;
    }

    public void setSmartWord(ArrayList<String> smartWord) {
        this.smartWord = smartWord;
    }

    public ArrayList<String> getSmartDef() {
        return smartDef;
    }

    public void setSmartDef(ArrayList<String> smartDef) {
        this.smartDef = smartDef;
    }

    @Override
    public int compareTo(Note another) {
        return another.lastUpdate.compareTo(lastUpdate);
    }

    public Date getServerVersionDate() {
        return serverVersionDate;
    }

    public void setServerVersionDate(Date serverVersionDate) {
        this.serverVersionDate = serverVersionDate;
    }
}
