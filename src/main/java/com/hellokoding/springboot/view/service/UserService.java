package com.hellokoding.springboot.view.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.hellokoding.springboot.view.model.EmailAuthUser;
import com.hellokoding.springboot.view.model.NewUser;
import com.hellokoding.springboot.view.model.User;
import com.hellokoding.springboot.view.repository.EmailAuthRepository;
import com.hellokoding.springboot.view.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailAuthRepository emailAuthRepository;

    public NewUser saveUser(NewUser newUser) {
        return userRepository.save(newUser);
    }

    public Boolean isUserAvailable(User user) {
        List<NewUser> users = userRepository.getUserByEmail(user.getName());
        if (users.isEmpty()) {
            return false;
        } else if (!users.get(0).getPassword().equals(user.getPassword())) {
            return false;
        }
        return true;
    }

    public String authGoogleUser(String authCode) throws IOException {

// Set path to the Web application client_secret_*.json file you downloaded from the
// Google API Console: https://console.developers.google.com/apis/credentials
// You can also find your Web application client ID and client secret from the
// console and specify them directly when you create the GoogleAuthorizationCodeTokenRequest
// object.
        String CLIENT_SECRET_FILE = "classpath:googleUserContent.json";
        System.out.println(authCode);

// Exchange auth code for access token
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JacksonFactory.getDefaultInstance(), new FileReader(ResourceUtils.getFile(CLIENT_SECRET_FILE)));
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        authCode,
                        "http://localhost:8080")  // Specify the same redirect URI that you use with your web
                        // app. If you don't have a web version of your app, you can
                        // specify an empty string.
                        .execute();

        // String accessToken = tokenResponse.getAccessToken();

        //Use access token to call API
        // GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        // Drive drive =
        //         new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
        //                 .setApplicationName("Auth Code Exchange Demo")
        //                 .build();
        // File file = drive.files().get("appfolder").execute();

        //Get profile info from ID token
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        System.out.print(payload.toString());
        String userId = payload.getSubject();  // Use this value as a key to identify a user.
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");
        System.out.println(userId + name);
        EmailAuthUser emailAuthUser = new EmailAuthUser(email, name, emailVerified, pictureUrl
                ,locale, familyName,givenName);

        emailAuthRepository.save(emailAuthUser);

        return "hi";
    }


}
