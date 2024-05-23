package angeelya.inPic.recommedation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SlopeOneTest {
    @InjectMocks
    SlopeOne slopeOne;

    @Test
    void beginSlopeOne() {
        Map<Long, Map<Long, Double>> data = new HashMap<>();
        Map<Long, Double> user1 = new HashMap<>(), user2 = new HashMap<>();
        user1.put(1L, 0.75);
        user2.put(1L, 0.5);
        user2.put(2L, 0.25);
        data.put(1L, user1);
        data.put(2L, user2);
        Map<Long, Double> recommendationsMap = new HashMap<>();
        recommendationsMap.put(1L, 0.75);
        recommendationsMap.put(2L, 0.5);
        assertEquals(recommendationsMap,slopeOne.beginSlopeOne(data,1L));
    }
}