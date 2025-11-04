## 1️⃣ Controller Level Security
@RestController
@RequestMapping("/manager/api/users")
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
@RequiredArgsConstructor


@PreAuthorize("hasAnyRole('MANAGER','ADMIN')"):

Before any endpoint is executed, Spring Security checks if the logged-in user has ROLE_MANAGER or ROLE_ADMIN.

If not → access denied automatically.

This means only managers and admins can hit this controller.

## 2️⃣ Getting All Users (GET /manager/api/users)
@GetMapping
public ResponseEntity<?> getAllUsers(...) {
Page<User> usersPage = userService.getAllUsers(page, size, sortBy, direction);
List<UserResponse> userResponses = usersPage.getContent()
.stream()
.map(UserMapper::toResponse)
.toList();
...
}


The manager/admin can list all users, with pagination & sorting.

Each user is mapped to a UserResponse DTO (frontend-safe object).

Note: No per-user role restrictions here → everyone sees all users.

## 3️⃣ Deleting a User (DELETE /manager/api/users/{id})
User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();
User targetUser = userRepository.findById(id)
.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER"));

if (isManager && targetUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MANAGER") || r.getName().equals("ROLE_ADMIN"))) {
return ResponseEntity.status(HttpStatus.FORBIDDEN)
.body(UserMapper.toErrorResponse("Vous n'avez pas la permission de supprimer cet utilisateur."));
}


How it works:

Get the logged-in user (currentUser) from Spring Security's Authentication.

Get the user that should be deleted (targetUser) from the database.

Determine the current user's role:

isAdmin → true if admin

isManager → true if manager

Check permission:

If the current user is a manager, they cannot delete users with roles MANAGER or ADMIN.

If this condition is met → return 403 FORBIDDEN.

Otherwise:

Delete the target user and return a success message.

Key point: Admins skip this check → they can delete anyone.

## 4️⃣ Toggling Enabled Status (PATCH /manager/api/users/{id}/toggle)
targetUser.setEnabled(!targetUser.isEnabled());
userRepository.save(targetUser);
return ResponseEntity.ok(UserMapper.toResponse(targetUser));


Same permission logic as deletion:

Managers cannot toggle enabled for other managers or admins.

Admins can toggle anyone.

After permission check, simply flip the enabled boolean.

## 5️⃣ Updating a User (PATCH /manager/api/users/{id})
User updated = userService.updateUser(id, request);
return ResponseEntity.ok(UserMapper.toResponse(updated));


Again, uses the same permission check:

Managers cannot update other managers/admins.

Admins can update anybody.

Then calls userService.updateUser to apply the changes.

Returns a safe UserResponse DTO to the frontend.

## 6️⃣ The Permission Logic in a Nutshell
if (isManager && targetUser has role MANAGER or ADMIN) {
return FORBIDDEN
}


isManager → logged-in user is a manager.

targetUser has role MANAGER or ADMIN → trying to modify someone at the same level or higher.

Managers are limited; they can only edit normal users.

Admins bypass this check → they can do anything.

## 7️⃣ Authentication & Roles

Every endpoint uses Authentication to get the current logged-in user:

User currentUser = ((CustomUserPrincipal) authentication.getPrincipal()).getUser();


CustomUserPrincipal wraps the User entity, including roles.

Roles are used to enforce per-user permissions.







