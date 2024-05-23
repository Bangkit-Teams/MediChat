from csv_jsonl import JSONLinesDictWriter
import csv
with open('output.csv', 'r', encoding='utf-8') as f:
    l = [row for row in csv.DictReader(f)]
    with open("foo.jsonl", "w", encoding="utf-8") as _fh:
        writer = JSONLinesDictWriter(_fh)
        writer.writerows(l)