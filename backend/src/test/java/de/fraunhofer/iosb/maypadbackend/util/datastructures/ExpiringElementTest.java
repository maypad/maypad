package de.fraunhofer.iosb.maypadbackend.util.datastructures;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpiringElementTest {

    @Test
    public void isElementExpired() throws Exception {
        ExpiringElement element = new ExpiringElement(50, TimeUnit.MILLISECONDS);
        Thread.sleep(100);
        assertThat(element.isExpired()).isTrue();
    }

    @Test(timeout = 10000)
    public void isElementRemoved() throws Exception {
        List<ExpiringElement> elements = new ArrayList<>();
        elements.add(new ExpiringElement(50, TimeUnit.MILLISECONDS));
        elements.add(new ExpiringElement(42, TimeUnit.DAYS));
        ExpiredElementRemover expiredElementRemover = new ExpiredElementRemover(elements);
        Thread.sleep(100);
        expiredElementRemover.run();
        assertThat(elements.size()).isEqualTo(1);

    }

}