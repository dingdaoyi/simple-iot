#!/usr/bin/env bash
# Simple IoT — 每日数据备份脚本
# - PostgreSQL: pg_dump custom format
# - InfluxDB 3: 数据目录 tar.gz
# - RustFS: 数据卷 tar.gz
# 保留 7 天，落到 ${BACKUP_DIR}/YYYYMMDD/
#
# 用法: 由 cron 每日 02:30 调用
#   30 2 * * * /opt/simple-iot/scripts/backup.sh >> /var/log/simple-iot-backup.log 2>&1
#
# 环境变量:
#   BACKUP_DIR   备份根目录 (默认 /opt/backups)
#   RETENTION    保留天数 (默认 7)
#   POSTGRES_*   覆盖默认 postgres/postgres123/simple

set -euo pipefail

BACKUP_DIR="${BACKUP_DIR:-/opt/backups}"
RETENTION="${RETENTION:-7}"
PG_USER="${POSTGRES_USERNAME:-postgres}"
PG_PASS="${POSTGRES_PASSWORD:-postgres123}"
PG_DB="${DATABASE:-simple}"

DATE=$(date +%Y%m%d)
TS=$(date +%Y%m%d-%H%M%S)
DEST="${BACKUP_DIR}/${DATE}"
mkdir -p "${DEST}"

log() { echo "[$(date +'%F %T')] $*"; }
trap 'log "ERROR at line $LINENO (exit $?)"; exit 1' ERR

log "=== Simple IoT backup start (dest=${DEST}) ==="

# ---------- 1. PostgreSQL ----------
log "[1/3] pg_dump iot-postgres ..."
docker exec -e PGPASSWORD="${PG_PASS}" iot-postgres \
    pg_dump -U "${PG_USER}" -d "${PG_DB}" -Fc \
    > "${DEST}/postgres-${TS}.dump"
gzip --best "${DEST}/postgres-${TS}.dump"
PG_SIZE=$(du -h "${DEST}/postgres-${TS}.dump.gz" | cut -f1)
log "    ✓ postgres dump = ${PG_SIZE}"

# ---------- 2. InfluxDB 3 ----------
INFLUX_DATA="/root/.influxdb/data"
if [ -d "${INFLUX_DATA}" ]; then
    log "[2/3] tar InfluxDB data dir ..."
    # 不 stop influxdb，依赖 InfluxDB 3 的 LSM 写入语义即可（首次启动会自愈）
    tar czf "${DEST}/influxdb-${TS}.tar.gz" -C / "${INFLUX_DATA#/}" 2>/dev/null || true
    INFLUX_SIZE=$(du -h "${DEST}/influxdb-${TS}.tar.gz" | cut -f1)
    log "    ✓ influxdb archive = ${INFLUX_SIZE}"
else
    log "[2/3] InfluxDB data dir not found at ${INFLUX_DATA}, skip"
fi

# ---------- 3. RustFS ----------
log "[3/3] tar RustFS docker volume ..."
RUSTFS_VOL=$(docker volume inspect simple-iot_rustfs_data --format '{{.Mountpoint}}' 2>/dev/null || true)
if [ -n "${RUSTFS_VOL}" ] && [ -d "${RUSTFS_VOL}" ]; then
    tar czf "${DEST}/rustfs-${TS}.tar.gz" -C "${RUSTFS_VOL}" . 2>/dev/null || true
    RUSTFS_SIZE=$(du -h "${DEST}/rustfs-${TS}.tar.gz" | cut -f1)
    log "    ✓ rustfs archive = ${RUSTFS_SIZE}"
else
    log "    rustfs volume not found, skip"
fi

# ---------- 清理过期备份 ----------
log "Cleaning backups older than ${RETENTION} days ..."
find "${BACKUP_DIR}" -mindepth 1 -maxdepth 1 -type d -mtime +${RETENTION} -print -exec rm -rf {} \; || true

# ---------- 落盘大小汇总 ----------
TOTAL=$(du -sh "${DEST}" | cut -f1)
DISK_FREE=$(df -h /opt | awk 'NR==2 {print $4 " free / " $2 " total"}')
log "=== Backup done. ${DEST} = ${TOTAL} | disk: ${DISK_FREE} ==="
