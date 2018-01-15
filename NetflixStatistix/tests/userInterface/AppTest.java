package userInterface;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @DisplayName("getKeyFromValue should return a valid key")
    @org.junit.jupiter.api.Test
    void getKeyFromValue() {
            HashMap<Integer, String> map = new HashMap<>();
            map.put(1,"test");
            App.getKeyFromValue(map, "test");
            assertTrue((int) App.getKeyFromValue(map, "test") == 1);
        }
    }

