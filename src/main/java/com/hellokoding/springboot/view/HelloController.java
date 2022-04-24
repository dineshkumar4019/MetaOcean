package com.hellokoding.springboot.view;

import com.hellokoding.springboot.view.model.NewUser;
import com.hellokoding.springboot.view.model.User;
import com.hellokoding.springboot.view.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class HelloController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/hello"})
    public String hello(Model model) {
        return "hello";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute("newUser") NewUser newUser, Model model) {
        userService.saveUser(newUser);
        return "hello";
    }

    @RequestMapping(value = "login", method=RequestMethod.POST)
    @ResponseBody
    public String login(@RequestBody User user) {
        Boolean isUserAvailable = userService.isUserAvailable(user);
        return  isUserAvailable ? "login-success" : "error";
    }

    @PostMapping(value = "/auth", consumes = "application/octet-stream")
    public String authGoogleUser(@RequestBody String authCode
                                 , HttpServletRequest request ) throws IOException {

        System.out.println(authCode);
        if (request.getHeader("X-Requested-With") == null) {
            return null;
        }
        return userService.authGoogleUser(authCode);
    }
}
