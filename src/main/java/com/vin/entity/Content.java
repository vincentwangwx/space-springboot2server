package com.vin.entity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity 
@Table(name="t_content")
@JsonIgnoreProperties(value={"novel"})
public class Content {
    
    public Content() {
    }
    
    @Id
    @GeneratedValue
    private long id;

    @Column(name="PageId")
    private long pageId;
    
    @ManyToOne(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="NovelId")
    private Novel novel;
    
    public long getPageId() {
		return pageId;
	}


	public void setPageId(long pageId) {
		this.pageId = pageId;
	}


	public String getContentPath() {
		return contentPath;
	}


	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	
    
    public Novel getNovel() {
		return novel;
	}


	public void setNovel(Novel novel) {
		this.novel = novel;
	}

	@Column(name="ContentPath")
    private String contentPath;
    
    public Content(long id, Novel novel,String content) {
        this.id = id;
        this.novel = novel;
        this.contentPath =content;
    }


	

	public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    
    
}