package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;

import java.util.List;


public interface CustomPersonRepository {
    List<Person> findByNameOrLastNameContainingIgnoreCase(String searchString);
}