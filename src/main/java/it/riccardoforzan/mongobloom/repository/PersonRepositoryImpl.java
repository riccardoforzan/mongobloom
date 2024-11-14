package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data will automatically recognize it as the custom implementation for CustomPersonRepository based on naming conventions.
 */
@Repository
public class PersonRepositoryImpl implements CustomPersonRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public PersonRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Person> findByNameOrLastNameContainingIgnoreCase(String searchString) {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(Criteria.where("firstName").regex(searchString, "i"), Criteria.where("lastName").regex(searchString, "i")));
        return mongoTemplate.find(query, Person.class);
    }
}
