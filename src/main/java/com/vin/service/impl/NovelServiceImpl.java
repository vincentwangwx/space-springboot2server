package com.vin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.dao.NovelDao;
import com.vin.entity.Novel;
import com.vin.service.NovelService;

@Service
public class NovelServiceImpl implements NovelService {

    @Autowired
    private NovelDao novelDao;

    public void save(Novel novel){
        novelDao.save(novel);
    }
    public Novel get(long id){
        return novelDao.findById(id).get();
    }
    
    public List<Novel> list(){
    	return novelDao.findAll();
    }
    public void delete(long id){
    	 novelDao.deleteById(id);
    }
}