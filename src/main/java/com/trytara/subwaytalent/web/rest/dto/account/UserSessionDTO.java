/**
 * 
 */
package com.trytara.subwaytalent.web.rest.dto.account;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.trytara.subwaytalent.model.User;
import com.trytara.subwaytalent.model.User.Role;
import com.trytara.subwaytalent.model.UserProfile;

/**
 * 
 * @author JRDomingo
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserSessionDTO {

	public String email;
	
	public UserProfile profile;
	
	public String sessionToken;
	
	public List<Role> roles;
	public UserSessionDTO(){
		
	}
	
	public UserSessionDTO(User user, String sessionToken){
		this.email = user.email;
		this.profile = user.profile;
		this.sessionToken = sessionToken;
		this.roles = user.roles;
	}
	
}
