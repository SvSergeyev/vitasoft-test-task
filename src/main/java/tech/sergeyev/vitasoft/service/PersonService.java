package tech.sergeyev.vitasoft.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.sergeyev.vitasoft.persistence.dao.PersonRepository;
import tech.sergeyev.vitasoft.persistence.dao.RoleRepository;
import tech.sergeyev.vitasoft.persistence.model.users.Person;
import tech.sergeyev.vitasoft.persistence.model.users.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonService implements UserDetailsService {

    final PersonRepository personRepository;
    static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person user = personRepository.findByEmail(username);
        LOGGER.info("\nFind this username: " + username);
        if (user != null) {
            LOGGER.info("Find this user: " + user);
            return new User(
                    user.getEmail(),
                    user.getPassword(),
                    true,
                    true,
                    true,
                    true,
                    getAuthorities(user.getRoles())
            );
        }
        else throw new UsernameNotFoundException("User not found");
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public Person getPersonById(int id) {
        return personRepository.findById(id);
    }

    public Person getPersonByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public List<Person> getAllPeople() {
        List<Person> list = personRepository.findAll();
        for (Person person : list) {
            LOGGER.info("\nROLE: " + person.getRoles() + "\n");
        }
        return personRepository.findAll();
    }

}