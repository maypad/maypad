package de.fraunhofer.iosb.maypadbackend.model.repository;

import de.fraunhofer.iosb.maypadbackend.model.person.Author;
import de.fraunhofer.iosb.maypadbackend.model.person.Mail;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitTest {

    @Test
    public void compareAndUpdate() {
        Commit commit = new Commit("message", "identifier123", new Date(42),
                new Author("Peter", new Mail("test@mail.de")));
        Commit newcommit = new Commit("messageNew", "identifier124", new Date(1337),
                new Author("Peter", new Mail("testNew@mail.de")));
        commit.compareAndUpdate(newcommit);
        assertThat(commit.getMessage()).isEqualTo("messageNew");
        assertThat(commit.getIdentifier()).isEqualTo("identifier124");
        assertThat(commit.getTimestamp().getTime()).isEqualTo(1337);
        assertThat(commit.getAuthor().getName()).isEqualTo("Peter");
        assertThat(commit.getAuthor().getMail().getMailAddress()).isEqualTo("testNew@mail.de");
    }

    @Test
    public void compareAndUpdateWithNull() {
        Commit commit = new Commit("message", "identifier123", new Date(42),
                new Author("Peter", new Mail("test@mail.de")));
        commit.compareAndUpdate(null);
        assertThat(commit.getMessage()).isEqualTo("message");
        assertThat(commit.getIdentifier()).isEqualTo("identifier123");
        assertThat(commit.getTimestamp().getTime()).isEqualTo(42);
        assertThat(commit.getAuthor().getName()).isEqualTo("Peter");
        assertThat(commit.getAuthor().getMail().getMailAddress()).isEqualTo("test@mail.de");
    }

    @Test
    public void compareAndUpdateWithNullTimestamp() {
        Commit commit = new Commit("message", "identifier123", null,
                new Author("Peter", new Mail("test@mail.de")));
        Commit newcommit = new Commit("messageNew", "identifier124", new Date(1337),
                new Author("Peter", new Mail("testNew@mail.de")));
        commit.compareAndUpdate(newcommit);
        assertThat(commit.getMessage()).isEqualTo("messageNew");
        assertThat(commit.getIdentifier()).isEqualTo("identifier124");
        assertThat(commit.getTimestamp().getTime()).isEqualTo(1337);
        assertThat(commit.getAuthor().getName()).isEqualTo("Peter");
        assertThat(commit.getAuthor().getMail().getMailAddress()).isEqualTo("testNew@mail.de");
    }

    @Test
    public void compareAndUpdateWithOtherNullTimestamp() {
        Commit commit = new Commit("message", "identifier123", new Date(42),
                new Author("Peter", new Mail("test@mail.de")));
        Commit newcommit = new Commit("messageNew", "identifier124", null,
                new Author("Peter", new Mail("testNew@mail.de")));
        commit.compareAndUpdate(newcommit);
        assertThat(commit.getMessage()).isEqualTo("messageNew");
        assertThat(commit.getIdentifier()).isEqualTo("identifier124");
        assertThat(commit.getTimestamp()).isNull();
        assertThat(commit.getAuthor().getName()).isEqualTo("Peter");
        assertThat(commit.getAuthor().getMail().getMailAddress()).isEqualTo("testNew@mail.de");
    }
}