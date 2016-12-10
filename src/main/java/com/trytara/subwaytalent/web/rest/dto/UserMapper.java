/**
 * 
 */
package com.trytara.subwaytalent.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.trytara.subwaytalent.model.User;
import com.trytara.subwaytalent.web.rest.dto.account.UserProfileDTO;
import com.trytara.subwaytalent.web.rest.dto.account.UserSessionDTO;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@Component
public class UserMapper {

	public UserSessionDTO userToUserSessionDTO(User user, String sessionToken){
		UserSessionDTO userSessionDTO = new UserSessionDTO();
		userSessionDTO.email = user.email;
		userSessionDTO.profile = user.profile;
		userSessionDTO.sessionToken = sessionToken;
		return userSessionDTO;
	}
	
	public UserSessionDTO userToUserSessionDTO(User user){
		return userToUserSessionDTO(user, null);
	}
	
	public List<UserSessionDTO> usersTouserSessionDTOs(List<User> users){
		List<UserSessionDTO> userSessionDTOs = new ArrayList<UserSessionDTO>();
		for(User user: users){
			userSessionDTOs.add(userToUserSessionDTO(user));
		}
		return userSessionDTOs;
	}
	
	public static List<UserProfileDTO> usersToUserProfileDTOs(List<User> users){
		List<UserProfileDTO> userProfileDTOs = new ArrayList<UserProfileDTO>();
		for(User user: users){
			userProfileDTOs.add(new UserProfileDTO(user));
		}
		return userProfileDTOs;
	}
	
	public static UserProfileDTO userToUserProfileDTO(User user){
		return new UserProfileDTO(user);
	}

}
