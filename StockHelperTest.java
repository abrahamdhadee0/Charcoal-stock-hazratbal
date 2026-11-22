/**
 * Simple tests for the row checker. No JUnit, just run:
 *   java StockHelperTest.java
 *
 * These are DELIBERATELY BAD rows I typed to test validation.
 * They are not data and are not in data.csv.
 * After fieldwork, add one test that uses a real row copied from data.csv.
 */
public class StockHelperTest {

    static int passed = 0, failed = 0;

    public static void main(String[] args) {
        // each of these must be rejected
        expectBad("TEST-BAD: wrong column count", "17:00,charcoal,2,kg");
        expectBad("TEST-BAD: time not HH:mm", "5pm,charcoal,2,kg,100,regular,sale,");
        expectBad("TEST-BAD: unknown item", "17:00,firewood,2,kg,100,regular,sale,");
        expectBad("TEST-BAD: qty not a number", "17:00,charcoal,two,kg,100,regular,sale,");
        expectBad("TEST-BAD: qty zero", "17:00,charcoal,0,kg,100,regular,sale,");
        expectBad("TEST-BAD: qty too big", "17:00,charcoal,500,kg,100,regular,sale,");
        expectBad("TEST-BAD: kangri in kg", "17:00,kangri,1,kg,100,regular,sale,");
        expectBad("TEST-BAD: sale with no price", "17:00,charcoal,2,kg,,regular,sale,");
        expectBad("TEST-BAD: unknown type", "17:00,charcoal,2,kg,100,regular,gift,");

        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    static void expectBad(String name, String line) {
        try {
            StockHelper.parseRow(line, 0);
            failed++;
            System.out.println("FAIL " + name + " (row was accepted)");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("ok   " + name + " -> " + e.getMessage());
        }
    }
}
