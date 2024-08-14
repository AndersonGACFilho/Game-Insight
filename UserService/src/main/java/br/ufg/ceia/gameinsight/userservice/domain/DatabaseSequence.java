package br.ufg.ceia.gameinsight.userservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class represents a database sequence.
 */
@Document(collection = "database_sequences")
public class DatabaseSequence {

    /**
     * The unique identifier of the sequence.
     */
    @Id
    private String id;

    /**
     * The sequence number.
     */
    private Long seq;

    public DatabaseSequence() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
}