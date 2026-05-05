# Text Processing

A multithreaded Java word-frequency counter. It recursively scans a directory of text files, reads and cleans their contents in parallel, counts word occurrences across all files, and prints the top 10 most frequent words.

---

## How It Works

The program uses a [producer-consumer](https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem) pipeline with three stages connected by two shared bounded buffers, all running concurrently via Java threads.

```
[ File discovery ]
       │  FileNames (queue of paths)
       ▼
[ FileReader ×2 ]   ← reads & cleans lines from disk
       │  FileContents (bounded queue of text chunks)
       ▼
[ FileProcessor ×3 ] ← tokenises and counts words
       │
       ▼
[ WordFrequencies ]  ← merges partial counts
       │
       ▼
[ Top-10 output ]
```

### Stage 1 — File Discovery

`Tools.fileLocator` recursively walks the `datos/` directory and pushes every file path into a `FileNames` queue. Once all paths have been added, `noMoreNames()` signals that no more input is coming.

### Stage 2 — Reading (`FileReader`, ×2 threads)

Each `FileReader` pulls file paths from `FileNames`, reads the file contents (`ISO-8859-1` encoding), and splits each line into cleaned text chunks. Cleaning strips punctuation and extra whitespace using [regular expressions](https://en.wikipedia.org/wiki/Regular_expression) before pushing chunks into `FileContents`.

### Stage 3 — Processing (`FileProcessor`, ×3 threads)

Each `FileProcessor` pulls text chunks from `FileContents` and counts word occurrences into a local `HashMap`. When the input is exhausted, each processor merges its local counts into the shared `WordFrequencies` object via a `synchronized` method.

### Output

`Tools.wordSelector` sorts the combined frequency map by count (descending, then alphabetically) using a custom `Comparator` and a `TreeSet`, then returns the top 10 entries.

---

## Project Structure

```
text-processing/
└── src/
    ├── Main.java              # Entry point; wires up threads and buffers
    ├── FileNames.java         # Thread-safe queue of file paths
    ├── FileContents.java      # Bounded blocking queue of text chunks
    ├── FileReader.java        # Producer thread: reads files → FileContents
    ├── FileProcessor.java     # Consumer thread: counts words → WordFrequencies
    └── WordFrequencies.java   # Shared, synchronized word frequency accumulator
```

---

## Concurrency Design

| Class | Role | Thread-safety mechanism |
|---|---|---|
| `FileNames` | Path queue | `synchronized` on `getName()`; `volatile` flag |
| `FileContents` | Bounded text buffer | `synchronized` on `addData` / `getData`; `volatile` counters; busy-wait |
| `WordFrequencies` | Frequency accumulator | `synchronized` on `addFrequencies` |
| `FileReader` | Producer | Registers/unregisters with `FileContents` to signal shutdown |
| `FileProcessor` | Consumer | Reads until `getContents()` returns `null` |

> **Note:** The current busy-wait loops in `FileContents` and `FileNames` will spin on the CPU while waiting. A production implementation would replace these with [`wait()`/`notify()`](https://en.wikipedia.org/wiki/Monitor_(synchronization)) or a [`BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html) from `java.util.concurrent`.

---

## Configuration

Adjust these values in `Main.java` to tune performance:

| Parameter | Default | Description |
|---|---|---|
| `FileContents` max files | `30` | Max queued text chunks before readers block |
| `FileContents` max chars | `100 × 1024` | Max buffered characters before readers block |
| `FileReader` threads | `2` | Parallel file readers |
| `FileProcessor` threads | `3` | Parallel word counters |
| Input directory | `"datos"` | Directory scanned recursively for text files |

---

## Requirements & Build

- Java JDK 8 or above
- No external dependencies

```bash
javac -d out src/*.java
java -cp out textprocessing.Main
```

Place input `.txt` files (ISO-8859-1 encoded) inside a `datos/` directory next to where you run the program.

---

## License

MIT — see [LICENSE](LICENSE).