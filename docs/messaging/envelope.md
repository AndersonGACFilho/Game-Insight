# Message Envelope

Title: Messaging Envelope Standard <br>
Version: 0.1.0 <br>
Last Updated: 2025-08-15 <br>
Owner: Anderson (Sole Maintainer) <andersonfilho09@gmail.com> <br>
Status: Accepted <br>
Decision: Final <br>

## Objective
Standardize metadata for traceability and versioning.

## Envelope (Base JSON / RabbitMQ)
```json
{
  "trace_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "correlation_id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "schema_version": "1.0.0",
  "occurred_at": "2025-08-14T12:34:56Z",
  "message_type": "user_profile.rebuild.command",
  "idempotency_key": "9e3d8d3c-1f1a-4c5a-9e2e-111111111111",
  "retry_count": 0,
  "payload": { }
}
```

## JSON Schema (Draft 2020-12)
```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://game-insight.io/schemas/envelope-1.0.0.json",
  "title": "Messaging Envelope",
  "type": "object",
  "required": [
    "trace_id",
    "schema_version",
    "occurred_at",
    "message_type",
    "idempotency_key",
    "payload"
  ],
  "properties": {
    "trace_id": {"type": "string", "format": "uuid"},
    "correlation_id": {"type": "string", "format": "uuid"},
    "schema_version": {"type": "string", "pattern": "^\\d+\\.\\d+\\.\\d+$"},
    "occurred_at": {"type": "string", "format": "date-time"},
    "message_type": {"type": "string", "pattern": "^[a-z0-9_]+(\\.[a-z0-9_]+)*$"},
    "idempotency_key": {"type": "string", "format": "uuid"},
    "retry_count": {"type": "integer", "minimum": 0},
    "compression": {"type": "string", "enum": ["gzip", "zstd"]},
    "payload": {"type": "object"},
    "source": {"type": "string"},
    "replay": {"type": "boolean", "default": false}
  },
  "additionalProperties": false
}
```

## Common Avro (Kafka) Fields
- trace_id (string)
- correlation_id (string; may match trace_id if single span)
- schema_version (string)
- occurred_at (long - epoch millis)
- source (string) e.g. steam.etl
- payload (record) topic-specific
- compression (enum, optional)

### Avro Envelope Wrapper (Example)
```json
{
  "type": "record",
  "name": "Envelope",
  "namespace": "io.gameinsight.messaging",
  "fields": [
    {"name": "trace_id", "type": "string"},
    {"name": "correlation_id", "type": ["null", "string"], "default": null},
    {"name": "schema_version", "type": "string"},
    {"name": "occurred_at", "type": "long", "doc": "Epoch millis UTC"},
    {"name": "source", "type": "string"},
    {"name": "compression", "type": ["null", {"type": "enum", "name": "Compression", "symbols": ["gzip", "zstd"]}], "default": null},
    {"name": "payload", "type": {"type": "record", "name": "Payload", "fields": []}}
  ]
}
```
> NOTE: Concrete topics SHOULD define a specific schema for `payload` and register it; do not publish with empty fields array in production.

### Example Topic Schema (GamePlayedEvent Payload)
```json
{
  "type": "record",
  "name": "GamePlayedEvent",
  "namespace": "io.gameinsight.events.game",
  "fields": [
    {"name": "user_id", "type": "string"},
    {"name": "game_id", "type": "string"},
    {"name": "playtime_minutes", "type": "int"},
    {"name": "session_started_at", "type": "long", "doc": "Epoch millis UTC"},
    {"name": "session_duration_sec", "type": "int", "default": 0}
  ]
}
```
> Register payload schema separately; reference by subject `<topic>-value` in schema registry.

## Conventions
- message_type: dot-separated domain.action
- schema_version: SemVer
- occurred_at: origin time (not processing time)
- Idempotency: required for re-runnable commands
- trace_id: propagate from HTTP or previous message; generate if absent
- correlation_id: REQUIRED for multi-step workflows / sagas; MAY be omitted (null) for fire-and-forget events
- retry_count: incremented by infrastructure (not business code)
- compression: set only if payload bytes compressed

## Size Guidance
- Commands: target < 256 KB uncompressed. Larger payloads -> store object (e.g. S3) and send reference `store_url`.
- Events: avoid oversized payloads; prefer normalization.
- Hard limit (enforced): 512 KB (JSON) / 1 MB (Avro) — CI linter will flag larger schemas or fixture examples.

## Versioning
- Compatible additions: new optional fields with defaults
- Removals: MAJOR
- JSON `schema_version` MUST match envelope schema version for breaking changes
- Follow ADR 0003 rules

## DLQ
- `original_headers` field (raw)
- `error_reason` field (string)
- `failed_at` timestamp (UTC)

## Validation Rules (CI Gate Targets)
| Rule | Action if Violated |
|------|--------------------|
| schema_version pattern invalid | Reject publish / fail build |
| message_type pattern invalid | Reject publish |
| Missing required field | Reject publish |
| Avro incompatible change (non-backward) | Block merge |

## Examples
### Command (RabbitMQ)
```json
{
  "trace_id": "ed3b9f41-4fa5-4a23-a7f9-0f7c6f6c9b11",
  "correlation_id": "ed3b9f41-4fa5-4a23-a7f9-0f7c6f6c9b11",
  "schema_version": "1.0.0",
  "occurred_at": "2025-08-14T10:00:00Z",
  "message_type": "user_profile.rebuild.command",
  "idempotency_key": "b5a3e4dc-a3c1-4e3a-b6f2-111111111112",
  "retry_count": 0,
  "payload": {"user_id": "u_123", "reason": "steam_sync"},
  "source": "api.gateway",
  "replay": false
}
```

### Event (Kafka)
```json
{
  "trace_id": "4fe9b2b4-9f5a-4a5d-8fd1-222222222222",
  "correlation_id": null,
  "schema_version": "1.0.0",
  "occurred_at": 1755165600000,
  "source": "steam.etl",
  "payload": {"user_id": "u_123", "game_id": "g_456", "playtime_minutes": 120},
  "compression": null
}
```

## Tooling
- Pre-commit hook: JSON Schema validation (`npm run validate:envelope` placeholder)
- CI step: Avro compatibility check (backward) + size limit check
- CLI (future): `gi-msg lint <file>` to output contract diff vs registry

## Future
- Encryption-at-rest marker for sensitive sub-documents
- Payload chunking manifest format
- Automated schema evolution linter
- Envelope-level signature for tamper detection

## Revision History
- v0.1.0 (2025-08-15): Initial version.
