package com.trytara.subwaytalent.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@Entity
public class UserProfile {

	@JsonIgnore
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    
    public String name;
    
    @Column(name = "picture_url")
    public String pictureUrl;
    
    @Column(name = "birth_date")
    public Date birthDate;

}
