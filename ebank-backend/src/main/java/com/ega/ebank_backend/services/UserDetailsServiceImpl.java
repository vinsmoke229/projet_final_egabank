package com.ega.ebank_backend.services;

import com.ega.ebank_backend.entities.AppUser;
import com.ega.ebank_backend.repositories.AppUserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository userRepository;

    public UserDetailsServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));

        if (!appUser.isActive()) throw new DisabledException("Ce compte a été clôturé ou désactivé par la banque.");

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUser.getRole());

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                List.of(authority)
        );
    }
}