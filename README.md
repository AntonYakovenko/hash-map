# Hash map with open addressing
This repository contains hash table implementation with open addressing using linear probing probe sequence. See more about linear probing [here](https://en.wikipedia.org/wiki/Linear_probing)
### Features

- Hash table does not allow `null` keys
- Capacity of hash table is always a power of 2. So that it is always relatively prime with linear probing coefficient (31) and never produces a cycle when resolving collisions
- An instance of `HashMapOpenAddressing` has two parameters that affect its performance: <i>initial capacity</i> and <i>load factor</i>. Their influence is the same as in `java.util.HashMap` implementation
- This implementation is not synchronized

### Test info

To ensure everything works correctly run `mvn clean test`

- Implementation is covered by unit tests for structural modifications and `MapOpenAddressing` interface operations
- There are tests for comparing performance with `java.util.HashMap`