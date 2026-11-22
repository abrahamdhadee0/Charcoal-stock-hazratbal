# Charcoal Stock Helper for Mohsin (Hazratbal, Srinagar)

**Place:** Mohsin's charcoal and kangri stall near Dargah Hazratbal, Srinagar  
**Person:** Mohsin, charcoal and kangri seller  
**Date of fieldwork:** 22 November 2026, 3:20 PM – 4:35 PM  
**Screen recording:** Pending

## The Problem

Last week, Mohsin ran out of charcoal. He currently decides how much charcoal to bring each day based on memory and personal experience.

This tool analyses a session of charcoal and kangri sales to help him understand:

- How quickly charcoal is selling.
- What customers are actually paying per kg.
- How many customers were turned away due to unavailable stock.
- How much charcoal he should consider bringing the next day.

See `research.md`, `fieldwork.md`, and `data.csv` for the supporting project information.

## How to Run

**Requirements:** Java 17 or newer. No build tool required.

Open PowerShell or a terminal in the project directory and run:

```bash
javac StockHelper.java StockHelperTest.java
java StockHelper data.csv
java StockHelperTest
```

## Results

The following is the output from running the program with `demo-data.csv`:

```text
Read 25 good rows, skipped 0 bad rows.

===== Charcoal report for Mohsin =====
Sales: 18, total sold: 36.8 kg
Kangris sold: 5
Price paid per kg: Rs 43 to Rs 56 (average Rs 46)
  Regular customers average: Rs 45/kg
  New customers average:     Rs 49/kg
Turned away: 2 people wanting 4.0 kg
Selling speed: 4.1 kg per hour (03:00 to  6:00)
Stock left: 83.3 kg, at this speed it lasts until about 14:41
  (that is past midnight at this speed)

Suggestion for tomorrow: bring about 49 kg charcoal
  (today's demand 40.8 kg + 20% buffer)
```

## Project Files

| File | Purpose |
|---|---|
| `StockHelper.java` | Main Java application |
| `StockHelperTest.java` | Tests for CSV validation and calculations |
| `data.csv` | Sales data |
| `demo-data.csv` | Demonstration dataset |
| `research.md` | Problem research |
| `fieldwork.md` | Fieldwork observations and interview notes |

## Limitations

- The tool uses one recorded session to estimate demand.
- Weather conditions and unexpected changes in customer demand are not considered.
- Supplier delivery can take 1–2 days, so ordering based only on tomorrow's estimate may be too late.
- The tool does not account for additional items such as free *kani* (wood shavings) given to regular customers.
- The recommendation is an estimate, not a guaranteed prediction.

## Future Improvements

- Include weather information in demand estimation.
- Account for supplier delivery time.
- Maintain historical sales records.
- Improve stock recommendations using multiple days of sales data.
- Track additional products and customer preferences.

## Testing

Run the test class using:

```bash
java StockHelperTest
```

All tests should pass before submitting the project.
