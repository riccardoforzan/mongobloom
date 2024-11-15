package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

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
    public Page<Person> findByNameOrLastNameContainingIgnoreCase(String searchString, Pageable pageable) {
        // Create query with pagination and sorting
        Query query = new Query()
                .addCriteria(new Criteria().orOperator(
                        Criteria.where("firstName").regex(searchString, "i"), // Case-insensitive regex for firstName
                        Criteria.where("lastName").regex(searchString, "i")   // Case-insensitive regex for lastName
                ))
                .with(pageable); // Apply pagination and sorting from Pageable

        // Fetch results
        List<Person> matches = mongoTemplate.find(query, Person.class);

        // Count total documents matching the criteria (ignoring pagination)
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Person.class);

        // Return a Page object with results, pageable, and total count
        return PageableExecutionUtils.getPage(matches, pageable, () -> total);
    }

}
