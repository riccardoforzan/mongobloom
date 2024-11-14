package it.riccardoforzan.mongobloom.service;

import it.riccardoforzan.mongobloom.collection.Person;
import it.riccardoforzan.mongobloom.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public String save(Person person) {
        return personRepository.save(person).personId();
    }

    @Override
    public List<Person> findByFirstName(String firstName) {
        return personRepository.findByFirstName(firstName);
    }

    @Override
    public List<Person> findByLastName(String lastName) {
        return personRepository.findByLastName(lastName);
    }

    @Override
    public List<Person> findByLastNameAndFirstName(String lastName, String firstName) {
        return personRepository.findByLastNameAndFirstName(lastName, firstName);
    }

    @Override
    public List<Person> findByNameOrLastNameContainingIgnoreCase(String searchString) {
        return personRepository.findByNameOrLastNameContainingIgnoreCase(searchString);
    }
}
