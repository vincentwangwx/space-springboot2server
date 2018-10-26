package com.vin.entity;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;;


@Entity 
@Table(name="t_novel")
@DynamicUpdate(true)
public class Novel {
	
	@Id
    @GeneratedValue
    private long id;

    @Column(name="name")
    private String name;
    
    @Column(name="fileid")
    private String fileId;

	@Column(name="author")
    private String author="";
	
    @Column(name="path")
    private String path="";
    
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "novel",cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();
    
    public Novel() {
    }

    
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getFileId() {
		return fileId;
	}


	public void setFileId(String fileId) {
		this.fileId = fileId;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public List<Content> getContents() {
		return contents;
	}


	public void setContents(List<Content> contents) {
		this.contents = contents;
	}


	public Novel(long id, String name,String author,String path) {
        this.id = id;
        this.name = name;
        this.author =author;
        this.path = path;
    }
    
   

}