package com.vin.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.controller.pagination.PaginationFormatting;
import com.vin.controller.pagination.PaginationMultiTypeValuesHelper;
import com.vin.dao.PersonsRepository;
import com.vin.entity.Persons;


@RestController
@RequestMapping("/persons")
public class MainController {

    @Autowired
    private PersonsRepository personsRepository;

    @Value(("${com.vin.paginatio.max-per-page}"))
    Integer maxPerPage;

    @RequestMapping(value = "/sex", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSexAll() {

        /*
         * @api {GET} /api/persons/sex Get all sexList
         * @apiName GetAllSexList
         * @apiGroup Info Manage
         * @apiVersion 1.0.0
         * @apiExample {httpie} Example usage:
         *
         *     http /api/persons/sex
         *
         * @apiSuccess {String} label
         * @apiSuccess {String} value
         */

        ArrayList<Map<String, String>> results = new ArrayList<>();

        for (Object value : personsRepository.findSex()) {

            Map<String, String> sex = new HashMap<>();

            sex.put("label", value.toString());

            sex.put("value", value.toString());

            results.add(sex);
        }

        ResponseEntity<ArrayList<Map<String, String>>> responseEntity = new ResponseEntity<>(results,
                HttpStatus.OK);

        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, PaginationMultiTypeValuesHelper> getPersonsAll(
            @RequestParam(value = "page", required = false) Integer pages,
            @RequestParam(value ="sex", required = false) String sex,
            @RequestParam(value ="email", required = false) String email
    ) {


        if (pages == null) {
            pages = 1;
        }
        if(sex == null) {
        	sex = "";
        }
        if(email == null) {
        	email = "";
        }

        Sort sort = new Sort(Direction.ASC, "id");

        Pageable pageable = new PageRequest(pages - 1, maxPerPage, sort);

        PaginationFormatting paginInstance = new PaginationFormatting();

        return paginInstance.filterQuery(sex, email, pageable);
    }
    
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String deletePerson(@PathVariable Long id) {
    	personsRepository.deleteById(id);
    	 //personsRepository.delete(id);

        return ""+id;
    }


    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public Persons createUser(@RequestBody Persons data) {

    	System.out.println(data.getPhone()+":"+data.getEmail());
        Persons user = new Persons();
        user.setPhone(data.getPhone());
        user.setZone(data.getZone());
        user.setUsername(data.getUsername());
        user.setEmail(data.getEmail());
        user.setSex(data.getSex());
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        user.setCreate_datetime(sdf.format(d));
        return personsRepository.save(user);

    }
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Persons> getUserDetail(@PathVariable Long id) {

        Optional<Persons> user = personsRepository.findById(id);
        //Persons user = personsRepository.findById(id);
        //Persons a = user.get();
        Persons p = new Persons();
        if(user.isPresent()) {
        	p = (Persons)user.get();
        }
        return new ResponseEntity<>( user.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "/detail/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Persons updateUser(@PathVariable Long id, @RequestBody Persons data) {

         Optional<Persons> user = personsRepository.findById(id);
    	 Persons p = new Persons();
         if(user.isPresent()) {
         	p = (Persons)user.get();
         }
    	p.setPhone(data.getPhone());
        p.setZone(data.getZone());
        p.setUsername(data.getUsername());
        p.setEmail(data.getEmail());
        p.setSex(data.getSex());
        return personsRepository.save(p);
    }

}