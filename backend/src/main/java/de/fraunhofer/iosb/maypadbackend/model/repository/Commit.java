package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * A commit in a {@link Repository}.
 *
 * @author Lukas Brosch
 * @version 1.0
 */
@Data
@NoArgsConstructor
@Entity
public class Commit {

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Column
    private String message;
    @Column
    private String identifier;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Author author;

    /**
     * Constructor for Commit.
     * @param message the Commit-Message
     * @param identifier the Commit-Identifier (hash for git)
     * @param timestamp the exact time of the commit
     * @param author the commit author
     */
    public Commit(String message, String identifier, Date timestamp, Author author) {
        this.message = message;
        this.identifier = identifier;
        this.timestamp = timestamp;
        this.author = author;
    }

    /**
     * Compare this current commit with an other commit and update different data.
     *
     * @param commit Other commit
     */
    public void compareAndUpdate(Commit commit) {
        if (commit == null) {
            return;
        }
        setCommitMessage(commit.getCommitMessage());
        setCommitIdentifier(commit.getCommitIdentifier());
        author.compareAndUpdate(commit.author);
        if (timestamp == null) {
            timestamp = commit.getTimestamp();
        } else if (commit.getTimestamp() == null) {
            timestamp = null;
        } else if (timestamp.getTime() != commit.getTimestamp().getTime()) {
            timestamp.setTime(commit.getTimestamp().getTime());
        }
    }
}
