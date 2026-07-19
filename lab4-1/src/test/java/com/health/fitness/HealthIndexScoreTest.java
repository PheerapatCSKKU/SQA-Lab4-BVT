package com.health.fitness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// ชื่อ-นามสกุล: พีรพัฒน์ ป้องกันยา   รหัสนักศึกษา: 673380053-3
// Lab 4.1 - Normal Boundary Value Testing
class HealthIndexScoreTest {

    // ---- VO2 Max: ขอบระหว่างแบนด์ (valid, ตรึง RHR=50, HRR=20) ----
    @ParameterizedTest(name = "TC{index}: vo2Max={0} -> {1}")
    @DisplayName("VO2 Max score - normal boundaries")
    @CsvSource({
        "0,0", "24.9,0", "25,1", "30,1", "31,2", "40,2",
        "41,3", "50,3", "51,4", "60,4", "61,5"
    })
    void testVo2MaxScore(double vo2, int expected) {
        HealthIndexScore h = new HealthIndexScore(vo2, 50, 20);
        assertEquals(expected, h.calculateVo2MaxScore());
    }

    // ---- VO2 Max: ค่าทศนิยมในช่องว่าง (คาดว่า FAIL = เจอ defect) ----
    @ParameterizedTest(name = "GAP: vo2Max={0} should be {1}")
    @DisplayName("VO2 Max decimal gaps - expected to FAIL (defect)")
    @CsvSource({ "30.5,1", "40.5,2", "50.5,3" })
    void testVo2MaxScore_decimalGap(double vo2, int expectedPerSpec) {
        HealthIndexScore h = new HealthIndexScore(vo2, 50, 20);
        assertEquals(expectedPerSpec, h.calculateVo2MaxScore());
    }

    // ---- RHR: ขอบระหว่างแบนด์ (ตรึง VO2=45, HRR=20) ----
    @ParameterizedTest(name = "TC{index}: rhr={0} -> {1}")
    @DisplayName("RHR score - normal boundaries")
    @CsvSource({ "40,5", "60,5", "61,3", "84,3", "85,1", "220,1" })
    void testRhrScore(int rhr, int expected) {
        HealthIndexScore h = new HealthIndexScore(45, rhr, 20);
        assertEquals(expected, h.calculateRhrScore());
    }

    // ---- HRR: ขอบระหว่างแบนด์ (ตรึง VO2=45, RHR=50) ----
    @ParameterizedTest(name = "TC{index}: hrr={0} -> {1}")
    @DisplayName("HRR score - normal boundaries")
    @CsvSource({ "0,1", "11,1", "12,3", "18,3", "19,4", "24,4", "25,5" })
    void testHrrScore(int hrr, int expected) {
        HealthIndexScore h = new HealthIndexScore(45, 50, hrr);
        assertEquals(expected, h.calculateHrrScore());
    }

    // ---- Output: total score + FitnessLevel ที่ขอบเกรด (5|6, 11|12) ----
    @ParameterizedTest(name = "vo2={0} rhr={1} hrr={2} -> total {3}, {4}")
    @DisplayName("Total score & fitness level - output boundaries")
    @CsvSource({
        "20,90,5,   2, POOR",
        "45,90,5,   5, POOR",
        "20,90,25,  6, STANDARD",
        "65,50,5,  11, STANDARD",
        "45,50,20, 12, EXCELLENT",
        "65,50,25, 15, EXCELLENT"
    })
    void testTotalScoreAndFitnessLevel(double vo2, int rhr, int hrr,
                                       int expectedTotal,
                                       HealthIndexScore.FitnessLevel expectedLevel) {
        HealthIndexScore h = new HealthIndexScore(vo2, rhr, hrr);
        assertEquals(expectedTotal, h.getTotalScore());
        assertEquals(expectedLevel, h.getFitnessLevel());
    }
}
