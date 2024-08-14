package br.ufg.ceia.gameinsight.userservice.dtos;

import br.ufg.ceia.gameinsight.userservice.domain.user.UserProfile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class UserProfileDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private Date birthdate;
    private String phoneNumber;

    public UserProfileDto() {}

    public UserProfileDto(UserProfile userProfile) {
        this.firstName = userProfile.getFirstName();
        this.lastName = userProfile.getLastName();
        this.birthdate = userProfile.getBirthdate();
        this.phoneNumber = userProfile.getPhoneNumber();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserProfile toUserProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(this.firstName);
        userProfile.setLastName(this.lastName);
        userProfile.setBirthdate(this.birthdate);
        userProfile.setPhoneNumber(this.phoneNumber);
        return userProfile;
    }
}
