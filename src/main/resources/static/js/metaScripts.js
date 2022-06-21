(function () {

 $("#login-submit").click(function(e){

 var xhr = new XMLHttpRequest();

    let myForm = document.getElementById('myForm');
    var name = myForm.elements['username'];
    var password = myForm.elements['password'];
    var username = name.value;
    var userPassword = password.value;


     xhr.onreadystatechange = function () {
         if (xhr.readyState == XMLHttpRequest.DONE) {
             if (xhr.responseText == "login-success") {
                 alert("ok");
             } else {
                if ($('.wrong-id').length === 0) {
                     $("<span style='color: red; padding-left:5px;' class='wrong-id'>Wrong Credentials</span>" ).appendTo( "#myForm" );
                }
             }
         }
     };

     xhr.open('POST', '/login', true);
     xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
     xhr.send(JSON.stringify({ "name" : username, "password" : userPassword}));
                         });
})();

function signInCallback(authResult) {
       if (authResult['code']) {

         // Hide the sign-in button now that the user is authorized, for example:
         $('#googleSingIn').attr('style', 'display: none');

         // Send the code to the server
         $.ajax({
           type: 'POST',
           url: 'http://localhost:8080/auth',
           // Always include an `X-Requested-With` header in every AJAX request,
           // to protect against CSRF attacks.
           headers: {
             'X-Requested-With': 'XMLHttpRequest'
           },
           contentType: 'application/octet-stream; charset=utf-8',
           success: function(result) {
             alert(result);
           },
           processData: false,
           data: authResult['code']
         });
       } else {
         alert("error");
       }
     }


function start() {
           gapi.load('auth2', function() {
             var auth2 = gapi.auth2.init({
               client_id: '1061393341050-3964m5lf33fvn1vs3lm6vdnfud1reuue.apps.googleusercontent.com',
               // Scopes to request in addition to 'profile' and 'email'
//               scope: 'https://www.googleapis.com/auth/drive'
             });
             $('#googleSingIn').click(function() {
               auth2.grantOfflineAccess().then(signInCallback);
             });
           });
         }