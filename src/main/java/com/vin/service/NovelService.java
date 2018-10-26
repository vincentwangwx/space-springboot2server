package com.vin.service;

import java.util.List;

import com.vin.entity.Novel;

public interface NovelService {
    public void save(Novel demo);
    public Novel get(long id);
    public List<Novel> list();
    public void delete(long id);
}