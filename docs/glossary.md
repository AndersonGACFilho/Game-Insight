# Glossary

Title: Glossary (Canonical Terms) <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

| Term | Definition |
|------|------------|
| Activation | User linked Steam and completed quiz |
| Cold Start | Lack of history for new user or new item |
| Coverage | % of items with at least one recommendation impression |
| CTR | Click-through rate on recommendations |
| NDCG | Normalized Discounted Cumulative Gain ranking metric (higher = better ordering relevance) |
| Feature Store | Central repo of features for training/inference |
| Novelty | Degree of non-obviousness / relative low popularity |
| Serendipity | Beneficial unexpected relevance |
| DLQ | Dead Letter Queue/Topic for failed messages |
| Idempotency Key | Key to avoid duplicate command execution |
| p95 | 95th percentile latency (value under which 95% of requests fall) |
| Lag | Difference between event production time and consumer processing (ms or seconds) |
| Backfill | Reprocessing historical data to rebuild state or features |
| Idempotent | Operation safe to repeat without changing the final result beyond the first application |

## Revision History
- v0.1.0 (2025-08-15): Initial version.