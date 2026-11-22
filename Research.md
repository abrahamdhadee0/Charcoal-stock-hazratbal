# Charcoal stock helper for Mohsin (Hazratbal, Srinagar)

**Place:** Mohsin's charcoal and kangri stall near Dargah Hazratbal, Srinagar
**Person:** Mohsin, charcoal and kangri seller
**Date of fieldwork:** [22 Nov 2026, 3:20–4:35 PM]
**Screen recording:** []

## The problem
Last week Mohsin ran out of charcoal. He decides how much to bring each day from memory.
This tool reads one session of his real sales and tells him how fast charcoal sold,
what people actually paid per kg, how many people he turned away, and how much to bring tomorrow.

See `research.md`, `fieldwork.md` and `data.csv`.

## How to run
Needs Java 17 or newer. No build tool needed.

#Real Results
PS C:\Users\abrah\Downloads\Charcoal-stock-hazratbal-main\Charcoal-stock-hazratbal-main> java StockHelper demo-data.csv

Read 25 good rows, skipped 0 bad rows.

===== Charcoal report for Mohsin =====
Sales: 18, total sold: 36.8 kg
Kangris sold: 5
Price paid per kg: Rs 43 to Rs 56 (average Rs 46)
  Regular customers average: Rs 45/kg
  New customers average:     Rs 49/kg
Turned away: 2 people wanting 4.0 kg
Selling speed: 4.1 kg per hour (09:12 to 18:14)
Stock left: 83.3 kg, at this speed it lasts until about 14:41
  (that is past midnight at this speed)

Suggestion for tomorrow: bring about 49 kg charcoal
  (today's demand 40.8 kg + 20% buffer)


```bash
javac StockHelper.java StockHelperTest.java
java StockHelper data.csv
java StockHelperTest
