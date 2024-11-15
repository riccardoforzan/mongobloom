package it.riccardoforzan.mongobloom.service;

import it.riccardoforzan.mongobloom.collection.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonService {
    String save(Person person);

    List<Person> findByFirstName(String firstName);

    List<Person> findByLastName(String lastName);

    List<Person> findByLastNameAndFirstName(String lastName, String firstName);

    Page<Person> findByNameOrLastNameContainingIgnoreCase(String searchString, Pageable pageable);

}
