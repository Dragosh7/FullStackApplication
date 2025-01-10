package com.example.monitoringbackend.security;


import com.example.monitoringbackend.entities.Person;
import com.example.monitoringbackend.repositories.PersonRepository;
import com.example.monitoringbackend.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

