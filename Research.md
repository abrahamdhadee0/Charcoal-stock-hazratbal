# Charcoal stock helper for Mohsin (Hazratbal, Srinagar)

**Place:** Mohsin's charcoal and kangri stall near Dargah Hazratbal, Srinagar
**Person:** Mohsin, charcoal and kangri seller
**Date of fieldwork:** [22 Nov 2026, 3:20  PM]

## The problem
Last week Mohsin ran out of charcoal. He decides how much to bring each day from memory.
This tool reads one session of his real sales and tells him how fast charcoal sold,
what people actually paid per kg, how many people he turned away, and how much to bring tomorrow.

See `research.md`, `fieldwork.md` and `data.csv`.

## How to run
Needs Java 17 or newer. No build tool needed.

```
java StockHelper.java data.csv
java StockHelperTest.java      # checks that bad rows are rejected
```

## data.csv columns
`time,item,qty,unit,price_rs,customer,type,note`

- `time`: 24-hour HH:mm
- `item`: charcoal or kangri
- `qty`: kg for charcoal, pieces for kangri (for turned_away: how much they wanted)
- `price_rs`: total rupees paid (blank for turned_away)
- `customer`: regular, new, or blank if I couldn't tell
- `type`: sale or turned_away
- `note`: free text, no commas

## Settings
All at the top of `StockHelper.java`:
`START_STOCK_KG`, `SAFETY_BUFFER_PERCENT`, `CLOSING_TIME`, `MAX_KG_PER_SALE`, `MAX_PRICE_RS`.



## What it gets wrong
- It uses one session of sales. One day can't show how demand changes with cold, power cuts, or Fridays at the Dargah.
- "Tomorrow" is just today's demand plus a buffer. If tomorrow is much colder, it will be too low.
- Selling speed assumes sales are steady between the first and last sale. Real sales come in bursts.
- Start stock is an estimate ("about 2 sacks"), so the run-out time is only rough.
- Turned-away demand is what people asked for, which I may have misheard or missed.
- Bad rows are skipped, not fixed. If many rows are bad, the numbers will be too low.

## Don't trust it for
- Prices or decisions over a whole season
- Ordering from the wholesaler more than one day ahead
