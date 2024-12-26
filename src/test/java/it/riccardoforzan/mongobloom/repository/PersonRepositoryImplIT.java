package it.riccardoforzan.mongobloom.repository;


import it.riccardoforzan.mongobloom.collection.Address;
import it.riccardoforzan.mongobloom.collection.Person;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(PersonRepositoryImpl.class)
@Testcontainers
public class PersonRepositoryImplIT {

    private static MongoDBContainer mongoContainer;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomPersonRepository personRepository;

    @BeforeAll
    static void setUpContainer() {
        mongoContainer = new MongoDBContainer("mongo:8.0");
        mongoContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoContainer.getReplicaSetUrl());
    }

    @AfterAll
    static void tearDownContainer() {
        mongoContainer.stop();
    }

    @Test
    void testFindByNameOrLastNameContainingIgnoreCase() {

        Person person = buildFakePerson();
        String firstName = person.firstName();
        mongoTemplate.save(person);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> result = personRepository.findByNameOrLastNameContainingIgnoreCase(firstName, pageable);

        assertThat(result.getContent().getFirst().firstName()).isEqualTo(firstName);
    }


    @Test
    void testFindBySkillAndCity() {
        Person person = buildFakePerson();
        String skill = person.skills().getFirst();
        String city = person.addresses().getFirst().city();
        mongoTemplate.save(person);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Person> result = personRepository.findBySkillAndCity(skill, city, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    static Person buildFakePerson() {
        Faker faker = new Faker();
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < faker.number().numberBetween(0, 3); i++) {
            addresses.add(new Address(faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode()));
        }
        List<String> skills = Arrays.asList(faker.job().keySkills(), faker.job().keySkills());
        return new Person(UUID.randomUUID().toString(), faker.name().firstName(), faker.name().lastName(), faker.number().numberBetween(0, 100), skills, addresses);
    }
}
