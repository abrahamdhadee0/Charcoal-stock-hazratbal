import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads Mohsin's sales from data.csv and tells him:
 *  - how fast charcoal sold and when today's stock runs out
 *  - what people actually paid per kg
 *  - how much charcoal to bring tomorrow
 *
 * Run: java StockHelper.java data.csv
 */
public class StockHelper {

    // ===================== SETTINGS (change these live) =====================
    static double START_STOCK_KG = 0;          // charcoal he had when I started counting (from my notes)
    static double SAFETY_BUFFER_PERCENT = 20;  // extra on top of today's demand for tomorrow
    static LocalTime CLOSING_TIME = LocalTime.of(20, 0); // when he usually packs up
    static double MAX_KG_PER_SALE = 50;        // bigger than this is probably a typo
    static int MAX_PRICE_RS = 5000;            // same idea for price
    // ========================================================================

    // One line of data.csv
    record Row(int lineNo, LocalTime time, String item, double qty, String unit,
               int priceRs, String customer, String type, String note) {}

    public static void main(String[] args) throws IOException {
        Path file = Path.of(args.length > 0 ? args[0] : "data.csv");
        if (!Files.exists(file)) {
            System.out.println("Could not find " + file + ". Put data.csv next to this file.");
            return;
        }

        List<String> lines = Files.readAllLines(file);
        List<Row> rows = new ArrayList<>();
        int skipped = 0;

        // line 1 is the header, so start from index 1
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                rows.add(parseRow(line, i + 1));
            } catch (IllegalArgumentException e) {
                skipped++;
                System.out.println("Skipping line " + (i + 1) + ": " + e.getMessage());
                System.out.println("   -> " + line);
            }
        }

        System.out.println();
        System.out.println("Read " + rows.size() + " good rows, skipped " + skipped + " bad rows.");
        if (rows.isEmpty()) {
            System.out.println("No good rows, nothing to report.");
            return;
        }
        printReport(rows);
    }

    // Turns one CSV line into a Row, or throws with a clear reason
    static Row parseRow(String line, int lineNo) {
        String[] p = line.split(",", -1);
        if (p.length != 8) {
            throw new IllegalArgumentException("expected 8 columns, found " + p.length);
        }

        LocalTime time;
        try {
            time = LocalTime.parse(p[0].trim()); // HH:mm, e.g. 17:42
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("time '" + p[0] + "' is not HH:mm");
        }

        String item = p[1].trim().toLowerCase();
        if (!item.equals("charcoal") && !item.equals("kangri")) {
            throw new IllegalArgumentException("item must be charcoal or kangri, got '" + p[1] + "'");
        }

        double qty;
        try {
            qty = Double.parseDouble(p[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("qty '" + p[2] + "' is not a number");
        }
        if (qty <= 0 || qty > MAX_KG_PER_SALE) {
            throw new IllegalArgumentException("qty " + qty + " is outside 0 to " + MAX_KG_PER_SALE);
        }

        String unit = p[3].trim().toLowerCase();
        if (item.equals("charcoal") && !unit.equals("kg")) {
            throw new IllegalArgumentException("charcoal should be in kg, got '" + p[3] + "'");
        }
        if (item.equals("kangri") && !unit.equals("piece")) {
            throw new IllegalArgumentException("kangri should be in piece, got '" + p[3] + "'");
        }

        String type = p[6].trim().toLowerCase();
        if (!type.equals("sale") && !type.equals("turned_away")) {
            throw new IllegalArgumentException("type must be sale or turned_away, got '" + p[6] + "'");
        }

        // a turned-away customer paid nothing, so price can be blank
        int price = 0;
        if (type.equals("sale")) {
            try {
                price = Integer.parseInt(p[4].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("price '" + p[4] + "' is not a whole number of rupees");
            }
            if (price <= 0 || price > MAX_PRICE_RS) {
                throw new IllegalArgumentException("price " + price + " is outside 1 to " + MAX_PRICE_RS);
            }
        }

        String customer = p[5].trim().toLowerCase();
        if (!customer.equals("regular") && !customer.equals("new") && !customer.isEmpty()) {
            throw new IllegalArgumentException("customer must be regular, new or blank, got '" + p[5] + "'");
        }

        return new Row(lineNo, time, item, qty, unit, price, customer, type, p[7].trim());
    }

    static void printReport(List<Row> rows) {
        double kgSold = 0, kgTurnedAway = 0;
        int charcoalSales = 0, turnedAwayCount = 0, kangrisSold = 0;
        double minRate = Double.MAX_VALUE, maxRate = 0;
        double regularKg = 0, regularRs = 0, newKg = 0, newRs = 0;
        LocalTime first = null, last = null;

        for (Row r : rows) {
            if (r.item().equals("kangri")) {
                if (r.type().equals("sale")) kangrisSold += (int) r.qty();
                continue;
            }
            // charcoal from here
            if (r.type().equals("turned_away")) {
                turnedAwayCount++;
                kgTurnedAway += r.qty();
                continue;
            }
            charcoalSales++;
            kgSold += r.qty();
            double ratePerKg = r.priceRs() / r.qty();
            minRate = Math.min(minRate, ratePerKg);
            maxRate = Math.max(maxRate, ratePerKg);
            if (r.customer().equals("regular")) { regularKg += r.qty(); regularRs += r.priceRs(); }
            if (r.customer().equals("new"))     { newKg += r.qty();     newRs += r.priceRs(); }
            if (first == null || r.time().isBefore(first)) first = r.time();
            if (last == null || r.time().isAfter(last)) last = r.time();
        }

        System.out.println();
        System.out.println("===== Charcoal report for Mohsin =====");
        System.out.printf("Sales: %d, total sold: %.1f kg%n", charcoalSales, kgSold);
        System.out.printf("Kangris sold: %d%n", kangrisSold);

        if (charcoalSales > 0) {
            System.out.printf("Price paid per kg: Rs %.0f to Rs %.0f (average Rs %.0f)%n",
                    minRate, maxRate, totalRs(rows) / kgSold);
            if (regularKg > 0) System.out.printf("  Regular customers average: Rs %.0f/kg%n", regularRs / regularKg);
            if (newKg > 0)     System.out.printf("  New customers average:     Rs %.0f/kg%n", newRs / newKg);
        }

        if (turnedAwayCount > 0) {
            System.out.printf("Turned away: %d people wanting %.1f kg%n", turnedAwayCount, kgTurnedAway);
        }

        // selling speed, only meaningful if sales are spread over some time
        double kgPerHour = 0;
        if (first != null && last != null && Duration.between(first, last).toMinutes() >= 30) {
            double hours = Duration.between(first, last).toMinutes() / 60.0;
            kgPerHour = kgSold / hours;
            System.out.printf("Selling speed: %.1f kg per hour (%s to %s)%n", kgPerHour, first, last);
        } else {
            System.out.println("Selling speed: not enough time between first and last sale (need 30+ min).");
        }

        // when does today's stock run out?
        if (START_STOCK_KG > 0 && kgPerHour > 0) {
            double left = START_STOCK_KG - kgSold;
            if (left <= 0) {
                System.out.println("Stock: he ran out today.");
            } else {
                long minutesLeft = (long) (left / kgPerHour * 60);
                LocalTime runOut = last.plusMinutes(minutesLeft);
                System.out.printf("Stock left: %.1f kg, at this speed it lasts until about %s%n", left, runOut);
                if (runOut.isBefore(CLOSING_TIME)) {
                    System.out.println("  WARNING: that is before closing time " + CLOSING_TIME);
                }
            }
        }

        // tomorrow = what people wanted today (sold + turned away) + buffer
        double wanted = kgSold + kgTurnedAway;
        double tomorrow = wanted * (1 + SAFETY_BUFFER_PERCENT / 100);
        System.out.println();
        System.out.printf("Suggestion for tomorrow: bring about %.0f kg charcoal%n", Math.ceil(tomorrow));
        System.out.printf("  (today's demand %.1f kg + %.0f%% buffer)%n", wanted, SAFETY_BUFFER_PERCENT);
    }

    static double totalRs(List<Row> rows) {
        double sum = 0;
        for (Row r : rows) {
            if (r.item().equals("charcoal") && r.type().equals("sale")) sum += r.priceRs();
        }
        return sum;
    }
}
