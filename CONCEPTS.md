# MongoDB Aggregation Concepts and Examples

## 1. **`unwind`**
### Purpose
- Splits an array field into multiple documents, each containing one element of the array.
- Useful for filtering or processing individual elements of an array.

### Example Input Document
```json
{
  "_id": 1,
  "name": "John",
  "addresses": [
    { "city": "New York", "zip": "10001" },
    { "city": "Los Angeles", "zip": "90001" }
  ]
}
```

### Aggregation Operation
```java
Aggregation.unwind("addresses")
```

### Output
```json
[
  {
    "_id": 1,
    "name": "John",
    "addresses": {
      "city": "New York",
      "zip": "10001"
    }
  },
  {
    "_id": 1,
    "name": "John",
    "addresses": {
      "city": "Los Angeles",
      "zip": "90001"
    }
    ]
```

### Notes
- Necessary when filtering array fields (e.g., `addresses.city`).

---

## 2. **`group`**
### Purpose
- Reconstructs documents after they’ve been split by `unwind`.
- Groups documents by a field and allows for aggregation of array fields.

### Example Input After `unwind`
```json
[
  {
    "_id": 1,
    "name": "John",
    "addresses": {
      "city": "New York"
    },
    "skills": "Java"
  },
  {
    "_id": 1,
    "name": "John",
    "addresses": {
      "city": "Los Angeles"
    },
    "skills": "Java"
  },
  {
    "_id": 1,
    "name": "John",
    "addresses": {
      "city": "Los Angeles"
    },
    "skills": "Python"
  }
]
```

### Aggregation Operation
```java
Aggregation.group("_id")
    .first("name").as("name")
    .first("email").as("email")
    .addToSet("addresses").as("addresses")
    .addToSet("skills").as("skills")
```

### Output
```json
{
  "_id": 1,
  "name": "John",
  "addresses": [
    { "city": "New York" },
    { "city": "Los Angeles" }
  ],
  "skills": ["Java", "Python"]
}
```

### Notes
- `first` retains the first occurrence of scalar fields.
- `addToSet` reconstructs arrays while removing duplicates.

---

## 3. **Pagination**
### Purpose
- Skip and limit results for paginated queries.
- Separate count query is often required to calculate the total number of matching documents.

### Aggregation Operations
```java
operations.add(Aggregation.skip(pageable.getOffset()));
operations.add(Aggregation.limit(pageable.getPageSize()));
```

### Why Count Query Is Separate
- `personList.size()` only represents the current page size, not the total count of all matching documents.
- Use `mongoTemplate.count(Query.query(...))` to calculate total matching documents for proper pagination metadata.

---

## 4. **Combining All Concepts**

### Use Case
Find people with a specific skill in a specific city. Both `skills` and `addresses` are arrays.

### Aggregation Pipeline
```java
public Page<Person> findBySkillAndCity(String skill, String city, Pageable pageable) {
    List<AggregationOperation> operations = new ArrayList<>();

    // Unwind arrays
    operations.add(Aggregation.unwind("addresses"));
    operations.add(Aggregation.unwind("skills"));

    // Match skill and city
    Criteria skillCriteria = Criteria.where("skills").is(skill);
    Criteria cityCriteria = Criteria.where("addresses.city").is(city);
    operations.add(Aggregation.match(new Criteria().andOperator(skillCriteria, cityCriteria)));

    // Group to reconstruct document
    operations.add(Aggregation.group("_id")
            .first("name").as("name")
            .first("email").as("email")
            .addToSet("addresses").as("addresses")
            .addToSet("skills").as("skills"));

    // Add pagination
    operations.add(Aggregation.skip(pageable.getOffset()));
    operations.add(Aggregation.limit(pageable.getPageSize()));

    // Execute aggregation
    Aggregation aggregation = Aggregation.newAggregation(operations);
    AggregationResults<Person> results = mongoTemplate.aggregate(aggregation, "person", Person.class);

    // Count for total pagination metadata
    long totalCount = mongoTemplate.count(Query.query(new Criteria().andOperator(skillCriteria, cityCriteria)), "person");

    return new PageImpl<>(results.getMappedResults(), pageable, totalCount);
}
```

---

## Summary Table

| Concept     | Purpose                                      | Key Methods                                       | Notes                                        |
|-------------|----------------------------------------------|---------------------------------------------------|----------------------------------------------|
| `unwind`    | Flatten arrays into multiple documents       | `Aggregation.unwind("arrayField")`                | Needed for filtering individual array items  |
| `group`     | Reconstruct documents after `unwind`         | `Aggregation.group("_id")`, `.addToSet`, `.first` | Ensures consistent document structure        |
| Pagination  | Skip and limit results for paging            | `Aggregation.skip`, `Aggregation.limit`           | Requires separate total count query          |
| Total Count | Calculate total matching documents for pages | `mongoTemplate.count(Query.query(...))`           | Avoids incorrect totals from paginated lists |
