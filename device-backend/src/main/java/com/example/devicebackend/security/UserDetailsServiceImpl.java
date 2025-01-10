package com.example.devicebackend.security;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.devicebackend.entities.Person;
import com.example.devicebackend.repositories.PersonRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    public UserDetailsServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("Not Found"));

        return UserDetailsImpl.builder()
                .id(person.getId())
                .username(person.getName())
                .build();
    }

}

