# 🔐 User Login Flow (Spring Boot + Angular)

This document explains **exactly what happens** when a user logs into the system — from the moment they submit the login
form in the frontend to the point the backend responds with user data.

---

## 🔑 1. The Moment the User Submits the Login Form

When the user fills in their email and password and clicks **Login**,  
the Angular frontend sends an HTTP POST request to the backend:

POST /public/api/auth/login
Content-Type: application/json

with a body like:

```json
{
  "email": "taha@example.com",
  "password": "mypassword"
}
```

## ⚙️ 2. The Request Reaches AuthController.login()

Spring Boot routes the request to this method:

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest)

Step-by-step:
✅ Step 1: Validate Input

The controller checks if the email and password fields are not empty:

if (request.getEmail() == null || request.getEmail().isBlank()) ...
if (request.getPassword() == null || request.getPassword().isBlank()) ...


If one is missing, an exception (IllegalArgumentException) is thrown and handled by the global exception handler.

✅ Step 2: Attempt Authentication

Spring Security verifies the credentials:

Authentication authentication = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
);


This single line triggers Spring Security’s authentication flow.

## 🧠 3. What Happens Inside authenticationManager.authenticate(...)

The AuthenticationManager calls your custom implementation of UserDetailsService:

CustomUserDetailsService.loadUserByUsername(String email)

✅ Step 3.1: Load the User from the Database
User user = userRepository.findByEmail(email)
    .orElseThrow(() -> new UsernameNotFoundException(email));


If no user exists → UsernameNotFoundException is thrown.
If found → the backend prints:

Hello you just logged in
<User info>

✅ Step 3.2: Wrap the User in CustomUserPrincipal
return new CustomUserPrincipal(user);


This class tells Spring Security:

The username → user.getEmail()

The password → user.getPassword()

The authorities (roles) → converted to Spring format:

user.getRoles().stream()
    .map(role -> new SimpleGrantedAuthority(role.getName()))
    .collect(Collectors.toList());


This step gives Spring Security all the information about the user’s permissions.

✅ Step 3.3: Password Check

Spring Security compares the provided password with the encoded one stored in the database using:

passwordEncoder.matches(request.getPassword(), user.getPassword());


If the passwords match → authentication is successful.
If not → a BadCredentialsException is thrown.

## 🔒 4. Authentication Is Successful

At this point, Spring creates an Authentication object containing:

principal: your CustomUserPrincipal

authorities: the user’s roles (e.g., ROLE_USER, ROLE_ADMIN)

authenticated: true

✅ Step 4.1: Save Authentication in the Security Context
SecurityContext securityContext = SecurityContextHolder.getContext();
securityContext.setAuthentication(authentication);


This stores the user’s authentication info in the current thread’s context.

✅ Step 4.2: Save Authentication in the HTTP Session
HttpSession session = httpRequest.getSession(true);
session.setAttribute(
    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
    securityContext
);


Spring now attaches a session cookie to the HTTP response:

Set-Cookie: JSESSIONID=abc123; Path=/; HttpOnly


This cookie allows the backend to recognize the user in future requests.

## 🧍 5. Prepare and Send the Response

After authentication, the backend retrieves the logged-in user:

CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();


It converts the user into a simplified, JSON-safe DTO using the UserMapper:

var response = UserMapper.toResponse(principal.getUser());


This object contains non-sensitive information:

ID, name, email, roles, address, timestamps, etc.

Passwords and internal flags are excluded.

Finally, it returns:

return ResponseEntity.ok(response);

## 📤 6. The Response Sent Back to the Frontend

HTTP Response:

HTTP/1.1 200 OK
Set-Cookie: JSESSIONID=abc123; Path=/; HttpOnly
Content-Type: application/json


Response Body:

{
  "id": 12,
  "firstName": "Taha",
  "lastName": "Ghailan",
  "fullName": "Taha Ghailan",
  "email": "taha@example.com",
  "phone": null,
  "enabled": true,
  "hasPassword": true,
  "roles": ["ROLE_USER"],
  "address": null,
  "creationDate": "2025-10-15T12:00:00Z",
  "lastModifiedDate": "2025-11-03T09:42:00Z"
}

## 🍪 7. What Happens in Future Requests

From this point forward:

The browser automatically includes the JSESSIONID cookie with every request.

Spring Security uses this cookie to restore the SecurityContext.

The backend instantly knows which user is making the request.

Example endpoint using the authenticated user:

@GetMapping("/profile")
public UserResponse getProfile(Authentication auth) {
    CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
    return UserMapper.toResponse(principal.getUser());
}

# 🖥️ Frontend Login Flow (AuthService)

This service handles everything related to authentication in the Angular frontend.

## 🔑 1. Service Initialization

When the app starts, the constructor runs:

const storedUser = localStorage.getItem('currentUser');
if (storedUser) {
const backendUser = JSON.parse(storedUser); // stored in backend format
const user = mapBackendUserToUser(backendUser);
this.currentUserSubject.next(user);
}


The service checks if a user is already stored in localStorage.

If yes:

Parse the stored backend user JSON.

Map it to the frontend User model using mapBackendUserToUser.

Push it into currentUserSubject → this is a BehaviorSubject that keeps the current user state.

Result: Any component subscribed to currentUser$ will immediately get the logged-in user info.

## ⚙️ 2. login() Method: Sending the Credentials
login(credentials: { email: string; password: string })


Steps:

Calls HttpClient.post to your backend /public/api/auth/login.

this.http.post<BackendUser>(this.loginUrl, credentials, { withCredentials: true })


credentials contains { email, password }.

withCredentials: true ensures cookies (JSESSIONID) from the backend are included, enabling session-based authentication.

Receives a BackendUser object from the backend response.

## 🧩 3. Mapping Backend Data to Frontend Model
map((backendUser) => this.handleLoginSuccess(backendUser))


handleLoginSuccess() converts the BackendUser (raw API response) into the frontend User model.

Stores it in:

currentUserSubject → keeps the state reactive.

localStorage → persists the user so refreshing the page doesn’t log them out.

Example backend → frontend mapping:

BackendUser: {
"id": 12,
"firstName": "Taha",
"lastName": "Ghailan",
"roles": ["ROLE_USER"]
}

Mapped User:
User {
id: 12,
fullName: "Taha Ghailan",
roles: ["ROLE_USER"],
...
}

## ✅ 4. State Management

currentUserSubject is a BehaviorSubject, meaning:

It always has the latest logged-in user.

Components can subscribe to currentUser$ to automatically react to login/logout events.

get currentUser$(): Observable<User | null> {
return this.currentUserSubject.asObservable();
}


Example usage in a component:

this.authService.currentUser$.subscribe(user => {
if(user) {
console.log('User logged in:', user.fullName);
}
});

## 🚀 5. Navigation After Login

After a successful login, the frontend can redirect users based on roles:

navigateAfterLogin(user: User) {
if (user.roles.includes('ROLE_MANAGER')) {
this.router.navigate(['/dashboard/profile']);
} else if (user.roles.includes('ROLE_USER')) {
this.router.navigate(['/profile']);
} else {
this.router.navigate(['/']);
}
}


ROLE-based routing is handled here.

This ensures each user sees only the pages they are allowed to access.

## 🔒 6. Logout
logout() {
this.currentUserSubject.next(null);
localStorage.removeItem('currentUser');
}


Clears the current user state.

Removes the persisted user from localStorage.

After logout, currentUser$ emits null, so components react accordingly (e.g., hiding protected pages).

## 🧠 7. Error Handling
catchError((err) => {
const backendMessage = err.error?.message || err.error?.error || 'Unknown error';
console.error('Login error:', backendMessage);
return throwError(() => new Error(backendMessage));
})


If the backend returns an error (e.g., invalid credentials), the frontend:

Extracts the error message.

Logs it.

Returns an observable error for components to handle (showing a toast or alert).

## 🍪 8. How the Frontend Uses Cookies

withCredentials: true ensures the JSESSIONID cookie from the backend is automatically sent with every request.

This cookie allows the backend to associate requests with the authenticated session, without sending a JWT manually.

On page refresh:

The backend will still recognize the user session using the cookie.

If the user is stored in localStorage, the frontend can immediately restore the logged-in state.

# 📝 Summary: Full Login Flow

User submits email & password → Angular sends HTTP POST.

Backend validates & authenticates → returns user DTO + session cookie.

Angular maps backend user → frontend User.

currentUserSubject emits new user state.

User is stored in localStorage for persistence.

Frontend navigates user based on roles.

Cookie ensures subsequent requests remain authenticated.

Logout clears state & storage.