package br.ufg.ceia.gameinsight.userservice.dtos;

import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class UserProfileDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Date birthdate;

    public UserProfileDto() {}

    public UserProfileDto(UserProfile userProfile) {
        this.birthdate = userProfile.getBirthdate();
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public UserProfile toUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setBirthdate(this.birthdate);
        return userProfile;
    }
}
