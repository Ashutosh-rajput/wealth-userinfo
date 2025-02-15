package com.WealthManager.UserInfo.util.counter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "counters")
public class Counter {
    @Id
    private String id;
    private String collectionName;
    private long value;

    public Counter(String collectionName) {
        this.collectionName = collectionName;
        this.value = 0; // Initialize the counter
    }

    public void increment() {
        this.value++;
    }


}