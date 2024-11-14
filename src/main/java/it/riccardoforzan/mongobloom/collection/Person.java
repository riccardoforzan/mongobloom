package it.riccardoforzan.mongobloom.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Person(@Id String personId, String firstName, String lastName, Integer age, List<String> skills,
                     List<Address> addresses) {
}
