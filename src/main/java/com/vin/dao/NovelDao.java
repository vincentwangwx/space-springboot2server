package com.vin.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.vin.entity.Novel;

public interface NovelDao extends JpaRepository<Novel, Long> {

}