package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.security.UserSecurityDetails;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return new UserSecurityDetails(userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("There is no user with email = " + email + " in DB")));
    }

}
