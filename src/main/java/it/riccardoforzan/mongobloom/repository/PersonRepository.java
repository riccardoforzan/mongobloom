package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends MongoRepository<Person, String>, CustomPersonRepository {

    /**
     * Spring Data uses dynamic proxies to automatically create instances of repository interfaces.
     * When the application context starts, Spring detects repository interfaces
     * and generates proxy classes that implement these interfaces.
     *
     * @param firstName last name of the person
     * @return list of person having that exact last name
     */
    List<Person> findByFirstName(String firstName);

    List<Person> findByLastName(String lastName);

    List<Person> findByLastNameAndFirstName(String lastName, String firstName);

}
