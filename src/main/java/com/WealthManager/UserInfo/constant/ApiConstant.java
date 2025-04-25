package com.WealthManager.UserInfo.constant;

public class ApiConstant {

    //Post
    public static final String SAVE_USER = "/registerUser";
    public static final String VERIFY_USER = "/verifyUser";
    //Get
    public static final String GET_ALL_USERS = "/getAllUsers/{page}/{size}";
    public static final String GET_USER_BY_ID = "/getUser/{userId}";
    public static final String GET_USER = "/user/getUser";
    public static final String GET_USER_BY_EMAIL = "/getUser";
    //Put
    public static final String UPDATE_USER_BY_ID = "/updateUser/{userId}";
    public static final String ADD_ADDRESS="/addPatientAddress/{patientId}";

    //Delete
    public static final String DELETE_USER_BY_ID = "/deleteUser/{userId}";
    public static final String DELETE_ALL_USERS = "/deleteUsers";
    //Password
    public static final String FORGOT_PASSWORD ="/forgotPassword/{email}";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String RESET_PASSWORD = "/resetPassword";
    //Login
    public static final String LOGIN = "/login";
    public static final String REFRESH_TOKEN = "/login/refreshtoken";
    public static final String GOOGLE_CALLBACK = "/auth/google";



}
