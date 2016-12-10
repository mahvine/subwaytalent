package com.trytara.subwaytalent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trytara.subwaytalent.model.UserProfile;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
