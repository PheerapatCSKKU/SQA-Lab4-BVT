package com.health.fitness;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// ชื่อ-นามสกุล: พีรพัฒน์ ป้องกันยา  รหัสนักศึกษา: 673380053-3
// Lab 4.2 - Robustness Testing
class HealthIndexScoreRobustnessTest {

    // ---- ค่านอกช่วง -> ต้องโยน IllegalArgumentException ----
    @ParameterizedTest(name = "invalid: vo2={0} rhr={1} hrr={2}")
    @DisplayName("Robustness - invalid values must throw")
    @CsvSource({
        "-1,   70, 20",   // VO2 min-  (<0)
        "45,   39, 20",   // RHR min-  (<40)
        "45,  221, 20",   // RHR max+  (>220)
        "45,   70, -1"    // HRR min-  (<0)
    })
    void testInvalidInputsThrow(double vo2, int rhr, int hrr) {
        assertThrows(IllegalArgumentException.class,
            () -> new HealthIndexScore(vo2, rhr, hrr));
    }

    // ---- ค่า valid ที่ขอบ -> ไม่ throw + คะแนน/เกรดถูกต้อง ----
    @ParameterizedTest(name = "valid: vo2={0} rhr={1} hrr={2} -> {3}")
    @DisplayName("Robustness - valid boundary values")
    @CsvSource({
        "45,  70, 20, 10, STANDARD",
        "0,   70, 20,  7, STANDARD",
        "1,   70, 20,  7, STANDARD",
        "89,  70, 20, 12, EXCELLENT",
        "90,  70, 20, 12, EXCELLENT",
        "45,  40, 20, 12, EXCELLENT",
        "45,  41, 20, 12, EXCELLENT",
        "45, 219, 20,  8, STANDARD",
        "45, 220, 20,  8, STANDARD",
        "45,  70,  0,  7, STANDARD",
        "45,  70,  1,  7, STANDARD",
        "45,  70, 59, 11, STANDARD",
        "45,  70, 60, 11, STANDARD"
    })
    void testValidBoundaries(double vo2, int rhr, int hrr,
                             int expectedTotal,
                             HealthIndexScore.FitnessLevel expectedLevel) {
        HealthIndexScore h = new HealthIndexScore(vo2, rhr, hrr);
        assertEquals(expectedTotal, h.getTotalScore());
        assertEquals(expectedLevel, h.getFitnessLevel());
    }

    // ---- VO2/HRR ไม่มีเพดานบน -> ค่าเกินไม่ถูก reject (จุดสังเกต DEF-02) ----
    @ParameterizedTest(name = "no upper bound: vo2={0} rhr={1} hrr={2}")
    @DisplayName("Robustness - VO2/HRR accept over-range values")
    @CsvSource({
        "91, 70, 20",   // VO2 max+
        "45, 70, 61"    // HRR max+
    })
    void testNoUpperBound(double vo2, int rhr, int hrr) {
        assertDoesNotThrow(() -> new HealthIndexScore(vo2, rhr, hrr));
    }
}
