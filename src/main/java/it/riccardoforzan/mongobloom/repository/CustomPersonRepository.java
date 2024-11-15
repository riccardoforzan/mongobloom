package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPersonRepository {

    Page<Person> findByNameOrLastNameContainingIgnoreCase(String searchString, Pageable pageable);

    Page<Person> findBySkillAndCity(String skill, String city, Pageable pageable);

    Page<Document> getCountBySkill(Pageable pageable);

}