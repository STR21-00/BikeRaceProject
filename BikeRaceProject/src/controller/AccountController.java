package controller;

import model.Administrator;
import model.User;

// AccountController, from the MVC diagram: +manageUser()
// Controller only calls the model and gives back the result, it never prints 
// view is supposed to print
public class AccountController {

    public String manageUser(Administrator admin, User user) {
        return admin.manageUser(user);
    }
}
