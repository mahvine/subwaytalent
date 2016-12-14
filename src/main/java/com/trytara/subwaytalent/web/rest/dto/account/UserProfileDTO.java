package com.trytara.subwaytalent.web.rest.dto.account;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.trytara.subwaytalent.model.User;
import com.trytara.subwaytalent.model.User.Role;

/**
 * 
 * @author JRDomingo
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDTO {
    public Long id;
    public String email;
    public String name;
    public String pictureUrl;
    public Date birthDate;
    public String status;
    public User.Type type;

    public UserProfileDTO() {
    }

    public UserProfileDTO(User user) {
        if (user.profile != null) {
            this.name = user.profile.name;
            this.pictureUrl = user.profile.pictureUrl;
            this.birthDate = user.profile.birthDate;
        }
        this.id = user.id;
        this.type = user.type;
        this.email = user.email;
        this.status = user.status.toString();
    }
}
