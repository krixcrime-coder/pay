package com.app.rush47.models;

/**
 * Holds the currently logged-in user's session data.
 * Recreated from the original decompiled CurrentUser model.
 */
public class CurrentUser {

    private String memberId;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String token;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;

    public CurrentUser() {
        this.memberId = "";
        this.username = "";
        this.password = "";
        this.email = "";
        this.mobile = "";
        this.token = "";
        this.firstName = "";
        this.lastName = "";
        this.dob = "";
        this.gender = "";
    }

    public CurrentUser(String memberId, String username, String password, String email,
                        String mobile, String token, String firstName, String lastName) {
        this(memberId, username, password, email, mobile, token, firstName, lastName, "", "");
    }

    public CurrentUser(String memberId, String username, String password, String email,
                        String mobile, String token, String firstName, String lastName,
                        String dob, String gender) {
        this.memberId = memberId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.token = token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
    }

    public String getMemberid() { return memberId; }
    public void setMemberid(String memberId) { this.memberId = memberId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
