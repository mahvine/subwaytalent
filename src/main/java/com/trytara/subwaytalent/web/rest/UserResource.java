/**
 * 
 */
package com.trytara.subwaytalent.web.rest;

import io.swagger.annotations.Api;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.trytara.subwaytalent.model.User;
import com.trytara.subwaytalent.model.User.Status;
import com.trytara.subwaytalent.model.UserProfile;
import com.trytara.subwaytalent.repository.UserProfileRepository;
import com.trytara.subwaytalent.repository.UserRepository;
import com.trytara.subwaytalent.service.MailService;
import com.trytara.subwaytalent.service.exception.GenericUserException;
import com.trytara.subwaytalent.service.exception.GenericUserException.AccountNotVerified;
import com.trytara.subwaytalent.service.exception.GenericUserException.EmailAlreadyRegistered;
import com.trytara.subwaytalent.service.exception.GenericUserException.InvalidPassword;
import com.trytara.subwaytalent.service.exception.GenericUserException.InvalidResetKey;
import com.trytara.subwaytalent.service.exception.GenericUserException.InvalidSessionToken;
import com.trytara.subwaytalent.service.exception.GenericUserException.UnregisteredEmail;
import com.trytara.subwaytalent.web.rest.dto.UserMapper;
import com.trytara.subwaytalent.web.rest.dto.account.ChangePasswordDTO;
import com.trytara.subwaytalent.web.rest.dto.account.ChangePasswordResetDTO;
import com.trytara.subwaytalent.web.rest.dto.account.FacebookLoginDTO;
import com.trytara.subwaytalent.web.rest.dto.account.LoginDTO;
import com.trytara.subwaytalent.web.rest.dto.account.RegisterDTO;
import com.trytara.subwaytalent.web.rest.dto.account.SaveApnsTokenDTO;
import com.trytara.subwaytalent.web.rest.dto.account.UserProfileDTO;
import com.trytara.subwaytalent.web.rest.dto.account.UserSessionDTO;
import com.trytara.subwaytalent.web.rest.util.PaginationUtil;

/**
 * 
 * @author JRDomingo
 * Dec 10, 2016
 */
@RestController
@Api(value = "Account Management",basePath="/api", description = "RPC that enables UserAccountManagement")
public class UserResource {
	
	private static final Logger logger = LoggerFactory.getLogger(UserResource.class);
	
	@Inject
	UserRepository userRepository;

	@Inject
	UserProfileRepository userProfileRepository;
	
	@Inject
	MailService mailService;
	
	@Inject
	UserMapper userMapper;
	
	public static Map<String,Long> sessionHash = new HashMap<String,Long>();
	static{
		sessionHash.put("ecb252044b5ea0f679ee78ec1a12904739e2904d", 2L);
	}
	public static String encrypt(String string){
		return DigestUtils.sha1Hex(string);
	}
	
	@RequestMapping(value="/api/register", method=RequestMethod.POST)
	public void register(@Valid @RequestBody RegisterDTO registerDTO,HttpServletRequest request){
		String email = registerDTO.email.toLowerCase().trim();
		String password = registerDTO.password;
		
		User user = userRepository.findByEmailIgnoreCase(email);
		if(user == null){
			user = new User();
			user.dateRegistered = new Date();
			user.email = email;
			user.salt = RandomStringUtils.randomAlphanumeric(6);
			user.encryptedPassword = encrypt(password, user.salt);
			user.profile = registerDTO.profile;
			user.activationKey = encrypt(user.email);
			userProfileRepository.save(user.profile);
			userRepository.save(user);
			String baseUrl = request.getScheme() + // "http"
                    "://" +                                // "://"
                    request.getServerName() +              // "myhost"
                    ":" +                                  // ":"
                    request.getServerPort();               // "80"
			
			mailService.sendActivationEmail(user, baseUrl);
			
		}else{
			throw new EmailAlreadyRegistered();
		}
	}
	

	@RequestMapping(value="/api/resendActivation", method=RequestMethod.GET)
	public void resendActivation(@RequestParam("email") String email,HttpServletRequest request){

		String baseUrl = request.getScheme() + // "http"
                "://" +                                // "://"
                request.getServerName() +              // "myhost"
                ":" +                                  // ":"
                request.getServerPort();               // "80"

		User user = userRepository.findByEmailIgnoreCase(email);
		if(user != null){
			mailService.sendActivationEmail(user, baseUrl);
		}
		
	}

	@RequestMapping(value="/api/login", method=RequestMethod.POST)
	public UserSessionDTO login(@Valid @RequestBody LoginDTO loginDTO){
		String email = loginDTO.email.toLowerCase().trim();
		String password = loginDTO.password;
		User user = userRepository.findByEmailIgnoreCase(email);
		if(user != null){
			String encryptedPassword = encrypt(password,user.salt);
			if(user.encryptedPassword.equals(encryptedPassword)){
				String sessionToken = encrypt(user.email);
				sessionHash.put(sessionToken, user.id);
				UserSessionDTO userDTO = new UserSessionDTO();
				userDTO.email = email;
				userDTO.profile = user.profile;
				userDTO.sessionToken = sessionToken;
				if(!user.status.equals(Status.VERIFIED)){
					throw new AccountNotVerified();
				}
				return userDTO;
			}else{
				throw new InvalidPassword();
			}
		}else{
			throw new UnregisteredEmail();
		}
	}
	

	@RequestMapping(value="/api/facebookLogin", method=RequestMethod.POST)
	public UserSessionDTO facebookLogin(@Valid @RequestBody FacebookLoginDTO request){
		User user = userRepository.findByFacebookIdIgnoreCase(request.facebookId);
		if(user == null){
			user = new User();
			user.dateRegistered = new Date();
			user.facebookId = request.facebookId;
			user.profile = request.profile;
			user = userRepository.save(user);
		}
		String sessionToken = encrypt(user.facebookId);
		sessionHash.put(sessionToken, user.id);
		UserSessionDTO userDTO = new UserSessionDTO();
		userDTO.profile = user.profile;
		userDTO.sessionToken = sessionToken;
		return userDTO;
	}
	
	
	@RequestMapping(value="/api/session", method=RequestMethod.GET)
	public UserSessionDTO getSession(@RequestHeader("X-session") String sessionToken){
		User user = getUser(sessionToken);
		if(user!=null){
			UserSessionDTO userSessionDTO = userMapper.userToUserSessionDTO(user, sessionToken);
			return userSessionDTO;
		}else{
			throw new InvalidSessionToken();
		}
	}

	@RequestMapping(value="/activate", method=RequestMethod.GET)
	public ModelAndView activate(@RequestParam("activationKey") String activationKey,ModelAndView modelAndView){
		User user = userRepository.findByActivationKey(activationKey);
		if(user!=null){
			user.status = Status.VERIFIED;
			userRepository.save(user);
		}else{
			throw new GenericUserException("Invalid activation key");
		}

		modelAndView.setViewName("mdl/layouts/base");
        modelAndView.addObject("view", "mdl/fragments/accountactivated");
        return modelAndView;
	}

	@RequestMapping(value="/api/resetPassword", method=RequestMethod.GET)
	public String resetPassword(@RequestParam("email") String email, HttpServletRequest request){
		User user = userRepository.findByEmailIgnoreCase(email);
		if(user!=null){
			user.resetKey = encrypt(user.email+new Date().toString());
			userRepository.save(user);

            String baseUrl = request.getScheme() + // "http"
            "://" +                            // "://"
            request.getServerName() +          // "myhost"
            ":" +                              // ":"
            request.getServerPort();           // "80"
			mailService.sendPasswordResetMail(user, baseUrl);
		}else{
			throw new GenericUserException("Invalid email");
		}
		
		return "reset password link sent to email. TODO create a webpage";
	}
	
	@RequestMapping(value="/api/resetPassword", method=RequestMethod.PUT)
	public void resetPasswordUsingResetKey(@RequestBody ChangePasswordResetDTO changePasswordResetDTO){
		
		String email = changePasswordResetDTO.email;
		User user = userRepository.findByEmailIgnoreCase(email);
		String resetKey = changePasswordResetDTO.resetKey;
		String newPassword = changePasswordResetDTO.newPassword;
		if(resetKey.equals(user.resetKey)){
			String encryptedPassword = encrypt(newPassword,user.salt);
			user.encryptedPassword = encryptedPassword;
			user.resetKey = RandomStringUtils.randomAlphanumeric(10); //reset key
			userRepository.save(user);
		}else{
			throw new InvalidResetKey();
		}
	}

	@RequestMapping(value="/api/password", method=RequestMethod.PUT)
	public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO, @RequestHeader("X-session") String session){
		
		User user = getUser(session);
		String oldPassword = changePasswordDTO.oldPassword;
		String newPassword = changePasswordDTO.newPassword;

		String encryptedPassword = encrypt(oldPassword,user.salt);
		if(user.encryptedPassword.equals(encryptedPassword)){
			user.encryptedPassword  = encrypt(newPassword,user.salt);
			user.resetKey = RandomStringUtils.randomAlphanumeric(10); //reset key
			userRepository.save(user);
		}else{
			throw new InvalidPassword();
		}
	}

	@RequestMapping(value = "/api/profile", method = RequestMethod.GET)
	public UserProfileDTO getProfile(@RequestHeader("X-session") String session) {
		User user = getUser(session);
		return new UserProfileDTO(user);
	}

	@RequestMapping(value = "/api/profile", method = RequestMethod.PUT)
	public void updateProfile(@RequestBody UserProfileDTO userProfileDTO, @RequestHeader("X-session") String session) {
		User user = getUser(session);
		if (user.profile == null) {
			user.profile = new UserProfile();
			userProfileRepository.save(user.profile);
			userRepository.save(user);
		}
		user.profile.name = userProfileDTO.name;
		user.profile.pictureUrl = userProfileDTO.pictureUrl;
		user.profile.birthDate = userProfileDTO.birthDate;
		userRepository.save(user);
		userProfileRepository.save(user.profile);
	}
	
	@RequestMapping(value="/appleDevice", method=RequestMethod.POST)
	public void saveApnsToken(@RequestHeader("X-session") String sessionToken, @RequestBody SaveApnsTokenDTO request){
		User user = getUser(sessionToken);
		if(user!=null){
			user.apnsToken = request.apnsToken;
			userRepository.save(user);
		}
	}

	@RequestMapping(value="/api/users",method=RequestMethod.GET)
	public ResponseEntity<List<UserProfileDTO>> listUsers(@RequestParam(value="page",required=false, defaultValue="1") int page,
			@RequestParam(value="size",required=false, defaultValue="10") int size, @RequestParam(value="sort", defaultValue="id") String sortBy,
			@RequestParam(value="direction", defaultValue="DESC") Direction direction) throws URISyntaxException{
		Sort sort = new Sort(direction,sortBy);
		PageRequest pageRequest = PaginationUtil.generatePageRequest(page, size, sort);
		Page<User> pageUsers = userRepository.findAll(pageRequest);
		List<UserProfileDTO> userProfileDTOs = UserMapper.usersToUserProfileDTOs(pageUsers.getContent());

		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(pageUsers, "/api/users", page, size);
		return new ResponseEntity<List<UserProfileDTO>>(userProfileDTOs,headers,HttpStatus.OK);
	}
	
	@RequestMapping(value="/api/users/{userId}", method=RequestMethod.PUT)
	public void updateUserInfo(@PathVariable Long userId, @RequestBody UserProfileDTO request){
		User user = userRepository.findOne(userId);
		logger.debug("Save of user:{} started",userId);
//		User user = new User();
//		user.id = userId;
		userRepository.save(user);

		logger.debug("Save of user:{} was successful",userId);
	}
	
	@RequestMapping(value="/api/users",method=RequestMethod.DELETE)
	public void deleteUser(@RequestParam("email") String email){
		User user = userRepository.findByEmailIgnoreCase(email);
		userRepository.delete(user);
	}

	public User getUser(String sessionToken){
		Long userId = sessionHash.get(sessionToken);
		if(userId!=null){
			return userRepository.findOne(userId);
		}
		throw new InvalidSessionToken();
	}

	public static String encrypt(String password,String salt){
		return encrypt(password+salt);
	}
}
