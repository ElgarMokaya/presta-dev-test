package com.elgar.walletsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(value = {"test"})
public class WalletSystemApplicationTests {
    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }
}
