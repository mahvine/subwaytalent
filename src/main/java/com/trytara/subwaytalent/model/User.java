package com.trytara.subwaytalent.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@Entity
public class User {

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	public Long id;
	
	@Column(name="email",unique=true)
	public String email;
	
	public String facebookId;
	
	@Column(name="encryptedPassword")
	public String encryptedPassword;
	
	public String salt;

	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.REMOVE})
	@JoinColumn(name="profile_id", referencedColumnName="id")
	public UserProfile profile;
	
	@ElementCollection(targetClass = Role.class)
	@Column(name = "name", nullable = false)
	@Enumerated(EnumType.STRING)
	public List<Role> roles = new ArrayList<Role>();
	
	@Column(name="reset_key")
	public String resetKey;

	@Column(name="activation_key")
	public String activationKey;

	@Column(name="apns_token")
	public String apnsToken;

	@Enumerated(EnumType.STRING)
	public Status status = Status.CREATED;
	
	public enum Role{
	}
	
	public enum Status{
		CREATED,VERIFIED
	}
	
	@Column(name="date_registered")
	public Date dateRegistered;
	
	
	
}
