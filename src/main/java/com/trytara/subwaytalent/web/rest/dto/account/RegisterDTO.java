/**
 * 
 */
package com.trytara.subwaytalent.web.rest.dto.account;

import com.trytara.subwaytalent.model.User;
import com.trytara.subwaytalent.model.UserProfile;

/**
 * @author JRDomingo
 * 
 */
public class RegisterDTO extends LoginDTO{
	
	public User.Type type;
	public UserProfile profile;
	
	
}
