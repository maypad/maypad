package de.fraunhofer.iosb.maypadbackend.model.person;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AuthorTest {

    @Test
    public void compareAndUpdateValid() {
        Author author = new Author("Peter", new Mail("test@mail.de"));
        Author newauthor = new Author("Max", new Mail("new@mail.de"));
        author.compareAndUpdate(newauthor);
        assertThat(author.getName()).isEqualTo("Max");
        assertThat(author.getMail().getMailAddress()).isEqualTo("new@mail.de");
    }

    @Test
    public void compareAndUpdateWithNullAuthor() {
        Author author = new Author("Peter", new Mail("test@mail.de"));
        author.compareAndUpdate(null);
        assertThat(author.getName()).isEqualTo("Peter");
        assertThat(author.getMail().getMailAddress()).isEqualTo("test@mail.de");
    }

    @Test
    public void compareAndUpdateValidWithExistingNullMail() {
        Author author = new Author("Peter", null);
        Author newauthor = new Author("Max", new Mail("new@mail.de"));
        author.compareAndUpdate(newauthor);
        assertThat(author.getName()).isEqualTo("Max");
        assertThat(author.getMail().getMailAddress()).isEqualTo("new@mail.de");
    }

    @Test
    public void compareAndUpdateValidWithNullMail() {
        Author author = new Author("Peter", new Mail("test@mail.de"));
        Author newauthor = new Author("Max", null);
        author.compareAndUpdate(newauthor);
        assertThat(author.getName()).isEqualTo("Max");
        assertThat(author.getMail()).isNull();
    }
}