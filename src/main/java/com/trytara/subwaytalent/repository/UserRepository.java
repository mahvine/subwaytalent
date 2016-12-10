/**
 * 
 */
package com.trytara.subwaytalent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trytara.subwaytalent.model.User;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmailIgnoreCase(String email);

	User findByFacebookIdIgnoreCase(String fbId);
	
	User findByActivationKey(String activationKey);

}
