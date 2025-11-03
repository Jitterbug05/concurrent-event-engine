# Concurrent Event Engine

This is a benchmarking system to learn and experiment with core Java concurrency, memory management, GC behaviour and performance tuning.

### 1. Event Stream Layer

The Events Generator and creates a stream of events. These are published to a blocking queue. Worker threads consume events from the queue and pass to handlers. Handlers aggregate results by counting the number of events per user.

### 2. Concurrency Layer

- Two queue implementations: basic java linked blocking queue and manually implemented blocking queue with read and write lock.
- Thread pool size for concurrency scaling. Increased throughput may increase contention.
- Lock-based and batching aggregators.

### 3. Metrics Layer

For each event, start and end timestamps are recorded. Average latency, events per second and memory usage are periodically written to CSV for later visualisation.

### 4. Visualisation Layer

Data is visualised using JFreeChart to show:

- Throughput (events processed per second)
- Latency (average processing time per event)
- Memory usage (heap size and GC behaviour)

### 5. Experimentation and GC Tuning

The following can be varied to experiment with trade-offs between throughput and latency

- Worker thread count
- GC algorithm (Serial, Parallel, G1GC, ZGC). STW event and heap marking/compaction
- Heap size (xmx, xms). Affects garbage collection frequency and length
- Queue strategy (single lock vs dual lock)
- Aggregator type (lock-based vs batching)
