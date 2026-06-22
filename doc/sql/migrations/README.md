# Database migrations

For fresh installs use `doc/sql/init.sql` (it contains the full latest schema).

For existing installs running an older version, apply the files in this
directory **in filename order** to upgrade. Each file is idempotent
(`IF NOT EXISTS` guards), so re-running a migration is safe.

```bash
# example: apply against a running compose stack
docker compose exec -T postgres \
  psql -U postgres -d simple < doc/sql/migrations/20260622_001_push_config_hmac.sql
```

## Index

| Date       | File                                       | Description                            |
|------------|--------------------------------------------|----------------------------------------|
| 2026-06-22 | `20260622_001_push_config_hmac.sql`        | Add HMAC signing columns to push cfg.  |
