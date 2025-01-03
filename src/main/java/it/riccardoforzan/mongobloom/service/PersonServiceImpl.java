package it.riccardoforzan.mongobloom.service;

import it.riccardoforzan.mongobloom.collection.Person;
import it.riccardoforzan.mongobloom.repository.PersonRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Person> findByNameOrLastNameContainingIgnoreCase(String searchString, Pageable pageable) {
        return personRepository.findByNameOrLastNameContainingIgnoreCase(searchString, pageable);
    }

    @Override
    public Page<Person> findBySkillAndCity(String skill, String city, Pageable pageable) {
        return personRepository.findBySkillAndCity(skill, city, pageable);
    }

    @Override
    public Page<Document> getCountBySkill(Pageable pageable) {
        return personRepository.getCountBySkill(pageable);
    }

}
