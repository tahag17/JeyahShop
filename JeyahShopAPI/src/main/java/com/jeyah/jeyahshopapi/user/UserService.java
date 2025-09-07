package com.jeyah.jeyahshopapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User updateUser(Integer id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Update allowed fields
        if(request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getPhone() != null) user.setPhone(request.getPhone());

        // Update address
        Address address = user.getAddress();
        if(address != null) {
            if(request.getStreet() != null) address.setStreet(request.getStreet());
            if(request.getCity() != null) address.setCity(request.getCity());
            if(request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
        } else {
            // Create address if null
            Address newAddress = new Address();
            newAddress.setUser(user);
            newAddress.setStreet(request.getStreet());
            newAddress.setCity(request.getCity());
            newAddress.setPostalCode(request.getPostalCode());
            addressRepository.save(newAddress);
            user.setAddress(newAddress);
        }

        return userRepository.save(user);
    }


    @Transactional
    public User updatePhone(Integer userId, String phone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setPhone(phone);
        return userRepository.save(user);
    }

    @Transactional
    public User updateFirstName(Integer userId, String firstName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setFirstName(firstName);
        return userRepository.save(user);
    }

    @Transactional
    public User updateLastName(Integer userId, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Transactional
    public User updateAddress(Integer userId, UpdateAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Address address = user.getAddress();
//        if (address == null) {
//            Address newAddress = new Address();
//            newAddress.setUser(user);
//            newAddress.setStreet(request.getStreet());
//            newAddress.setCity(request.getCity());
//            newAddress.setPostalCode(request.getPostalCode());
//            addressRepository.save(newAddress);
//            user.setAddress(newAddress);
//        } else {
//            if (request.getStreet() != null) address.setStreet(request.getStreet());
//            if (request.getCity() != null) address.setCity(request.getCity());
//            if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());
//        }
//
//        return userRepository.save(user);
        if (address == null) {
            address = new Address();
            address.setUser(user);
            user.setAddress(address);
        }

        if (request.getStreet() != null) address.setStreet(request.getStreet());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode());

        return userRepository.save(user); // cascade saves address

    }

    @Transactional
    public void updatePassword(Integer userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // First-time password setup
        if (user.getPassword() == null) {
            if (request.getOldPassword() != null && !request.getOldPassword().isBlank()) {
                throw new IllegalArgumentException("Ancien mot de passe ne doit pas être fourni pour la première configuration");
            }
            validateNewPassword(request.getNewPassword());
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            return;
        }

        // Updating existing password
        if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
            throw new IllegalArgumentException("Ancien mot de passe est requis");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Ancien mot de passe incorrect");
        }

        validateNewPassword(request.getNewPassword());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    }


    private void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères");
        }
    }





}
