package it.riccardoforzan.mongobloom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import it.riccardoforzan.mongobloom.collection.Person;
import it.riccardoforzan.mongobloom.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v01/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping(path = "/find/byFirstName")
    public List<Person> getByFirstName(@Parameter(description = "First name to match") @RequestParam String firstName) {
        return personService.findByFirstName(firstName);
    }

    @GetMapping(path = "/find/byLastName")
    public List<Person> findByLastName(@Parameter(description = "Last name to match") @RequestParam String lastName) {
        return personService.findByLastName(lastName);
    }

    @GetMapping(path = "/find/byLastNameAndFirstName")
    public List<Person> getByLastNameAndFirstName(@Parameter(description = "Last name to match") @RequestParam String lastName,
                                                  @Parameter(description = "First name to match") @RequestParam String firstName
    ) {
        return personService.findByLastNameAndFirstName(lastName, firstName);
    }

    @GetMapping(path = "/find/byPartialMatch")
    public Page<Person> findByNameOrLastNameContainingIgnoreCase(@Parameter(description = "Case insensitive string") @RequestParam String searchString,
                                                                 @RequestParam(defaultValue = "0") Integer page,
                                                                 @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return personService.findByNameOrLastNameContainingIgnoreCase(searchString, pageable);
    }

    @PostMapping
    @Operation(summary = "Save a person to the persistence layer")
    public String savePerson(@Parameter(description = "Object to save") @RequestBody Person person) {
        return personService.save(person);
    }
}
