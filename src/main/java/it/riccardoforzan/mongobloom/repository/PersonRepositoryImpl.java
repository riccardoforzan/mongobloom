package it.riccardoforzan.mongobloom.repository;

import it.riccardoforzan.mongobloom.collection.Person;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        Query query = new Query().addCriteria(new Criteria().orOperator(Criteria.where("firstName").regex(searchString, "i"), // Case-insensitive regex for firstName
                Criteria.where("lastName").regex(searchString, "i")   // Case-insensitive regex for lastName
        )).with(pageable); // Apply pagination and sorting from Pageable

        // Fetch results
        List<Person> matches = mongoTemplate.find(query, Person.class);

        // Count total documents matching the criteria (ignoring pagination)
        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Person.class);

        // Return a Page object with results, pageable, and total count
        return PageableExecutionUtils.getPage(matches, pageable, () -> total);
    }

    @Override
    public Page<Person> findBySkillAndCity(String skill, String city, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.unwind("addresses"));
        operations.add(Aggregation.unwind("skills"));

        Criteria skillCriteria = Criteria.where("skills").is(skill);
        Criteria cityCriteria = Criteria.where("addresses.city").is(city);
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(skillCriteria, cityCriteria));
        operations.add(matchOperation);

        // Group documents back to avoid fragmented results
        operations.add(Aggregation.group("_id")
                .first("firstName").as("firstName")
                .first("lastName").as("lastName")
                .addToSet("addresses").as("addresses")
                .addToSet("skills").as("skills"));

        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Person> results = mongoTemplate.aggregate(aggregation, "person", Person.class);
        List<Person> personList = results.getMappedResults();

        Criteria countCriteria = new Criteria().andOperator(skillCriteria, cityCriteria);
        long totalCount = mongoTemplate.count(Query.query(countCriteria), "person");

        return new PageImpl<>(personList, pageable, totalCount);
    }

    @Override
    public Page<Document> getCountBySkill(Pageable pageable) {
        UnwindOperation unwindOperation = Aggregation.unwind("skills");
        GroupOperation groupOperation = Aggregation.group("skills")
                .count().as("populationCount");

        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(unwindOperation);
        operations.add(groupOperation);

        operations.add(Aggregation.skip(pageable.getOffset()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        if (pageable.getSort().isSorted()) {
            operations.add(Aggregation.sort(pageable.getSort()));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<Document> results = mongoTemplate.aggregate(aggregation, "person", Document.class).getMappedResults();
        return new PageImpl<>(results, pageable, results.size());
    }

}
