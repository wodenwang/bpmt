#!/bin/sh
set -eu

APP_CLASSES="${APP_CLASSES:-/usr/local/tomcat/webapps/ROOT/WEB-INF/classes}"
mkdir -p "$APP_CLASSES" /data/files/attachments /data/files/base64

DB_HOST="${DB_HOST:-mariadb}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-kyq}"
DB_USER="${DB_USER:-root}"
DB_PASSWORD="${DB_PASSWORD:-123456}"
DATABASE_TYPE="${DATABASE_TYPE:-mysql}"
JDBC_DRIVER_CLASS_NAME="${JDBC_DRIVER_CLASS_NAME:-com.mysql.jdbc.Driver}"
HIBERNATE_DIALECT="${HIBERNATE_DIALECT:-org.hibernate.dialect.MySQL5InnoDBDialect}"
HIBERNATE_AUTOUPDATE="${HIBERNATE_AUTOUPDATE:-false}"
JDBC_POOL_PARTITION_MAX="${JDBC_POOL_PARTITION_MAX:-50}"
JDBC_POOL_PARTITION_MIN="${JDBC_POOL_PARTITION_MIN:-5}"
JDBC_POOL_PARTITION_COUNT="${JDBC_POOL_PARTITION_COUNT:-2}"
SQL_LOG="${SQL_LOG:-false}"
SQL_STAT_ENABLE="${SQL_STAT_ENABLE:-false}"
SQL_STAT_LIMITED="${SQL_STAT_LIMITED:-500}"

if [ -z "${JDBC_URL:-}" ]; then
  JDBC_URL="jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true"
fi

cat > "$APP_CLASSES/jdbc.properties" <<EOF
database.type=${DATABASE_TYPE}
jdbc.driverClassName=${JDBC_DRIVER_CLASS_NAME}
jdbc.url=${JDBC_URL}
jdbc.username=${DB_USER}
jdbc.password=${DB_PASSWORD}
jdbc.pool.partition.max=${JDBC_POOL_PARTITION_MAX}
jdbc.pool.partition.min=${JDBC_POOL_PARTITION_MIN}
jdbc.pool.partition.count=${JDBC_POOL_PARTITION_COUNT}
hibernate.dialect=${HIBERNATE_DIALECT}
hibernate.autoupdate=${HIBERNATE_AUTOUPDATE}
sql.log=${SQL_LOG}
sql.stat.enable=${SQL_STAT_ENABLE}
sql.stat.limited=${SQL_STAT_LIMITED}
EOF

cat > "$APP_CLASSES/db.properties" <<EOF
db.def.driverClassName=${JDBC_DRIVER_CLASS_NAME}
db.def.url=${JDBC_URL}
db.def.username=${DB_USER}
db.def.password=${DB_PASSWORD}
db.def.dialect=${HIBERNATE_DIALECT}
EOF

cat > "$APP_CLASSES/redis.properties" <<EOF
redis.flag=${REDIS_FLAG:-false}
redis.ip=${REDIS_HOST:-redis}
redis.port=${REDIS_PORT:-6379}
redis.maxTotal=${REDIS_MAX_TOTAL:-5}
EOF

cat > "$APP_CLASSES/file.properties" <<EOF
file.attachment.path=${FILE_ATTACHMENT_PATH:-/data/files/attachments}
file.base64.path=${FILE_BASE64_PATH:-/data/files/base64}
EOF

cat > "$APP_CLASSES/safe.properties" <<EOF
safe.role=${SAFE_ROLE:-LIGHT_WEIGHT}
safe.sync.threads=${SAFE_SYNC_THREADS:-10}
safe.admin=${SAFE_ADMIN:-admin}
EOF

cat > "$APP_CLASSES/quartz.properties" <<EOF
quartz.threadPool.threadCount=${QUARTZ_THREAD_POOL_THREAD_COUNT:-5}
quartz.jobStore.class=${QUARTZ_JOB_STORE_CLASS:-org.quartz.impl.jdbcjobstore.JobStoreTX}
quartz.jobStore.driverDelegateClass=${QUARTZ_JOB_STORE_DRIVER_DELEGATE_CLASS:-org.quartz.impl.jdbcjobstore.StdJDBCDelegate}
EOF

cat > "$APP_CLASSES/hazelcast.properties" <<EOF
hazelcast.group.name=${HAZELCAST_GROUP_NAME:-bpmt}
hazelcast.group.password=${HAZELCAST_GROUP_PASSWORD:-bpmt}
hazelcast.management.center.enable=${HAZELCAST_MANAGEMENT_CENTER_ENABLE:-false}
hazelcast.management.center.url=${HAZELCAST_MANAGEMENT_CENTER_URL:-http://localhost:8080/mancenter}
hazelcast.port=${HAZELCAST_PORT:-5701}
hazelcast.multicast=${HAZELCAST_MULTICAST:-false}
hazelcast.tcpip=${HAZELCAST_TCPIP:-false}
hazelcast.tcpip.members=${HAZELCAST_TCPIP_MEMBERS:-127.0.0.1}
EOF

cat > "$APP_CLASSES/log.properties" <<EOF
log.encoding=${LOG_ENCODING:-UTF-8}
log.level=${LOG_LEVEL:-info}
log.jolbox.level=${LOG_JOLBOX_LEVEL:-warn}
log.3pp.level=${LOG_3PP_LEVEL:-warn}
log.keepdays=${LOG_KEEP_DAYS:-30}
EOF

cat > "$APP_CLASSES/sms.properties" <<EOF
sms.ali.enable=${SMS_ALI_ENABLE:-false}
sms.verified.system=${SMS_VERIFIED_SYSTEM:-BPMT}
sms.verified.length=${SMS_VERIFIED_LENGTH:-6}
sms.verified.template.default=${SMS_VERIFIED_TEMPLATE_DEFAULT:-}
sms.ali.endpoint=${SMS_ALI_ENDPOINT:-https://eco.taobao.com/router/rest}
sms.ali.appKey=${SMS_ALI_APP_KEY:-}
sms.ali.appSecret=${SMS_ALI_APP_SECRET:-}
sms.ali.signName=${SMS_ALI_SIGN_NAME:-}
EOF

cat > "$APP_CLASSES/mail.properties" <<EOF
mail.notify.flag=${MAIL_NOTIFY_FLAG:-false}
mail.notify.user.setting=${MAIL_NOTIFY_USER_SETTING:-false}
mail.flow.subject.type=${MAIL_FLOW_SUBJECT_TYPE:-2}
mail.flow.subject.script=${MAIL_FLOW_SUBJECT_SCRIPT:-}
mail.flow.content.type=${MAIL_FLOW_CONTENT_TYPE:-2}
mail.flow.content.script=${MAIL_FLOW_CONTENT_SCRIPT:-}
EOF

cat > "$APP_CLASSES/office.properties" <<EOF
office.flag=${OFFICE_FLAG:-false}
office.prepare=${OFFICE_PREPARE:-false}
office.file.size=${OFFICE_FILE_SIZE:-10}
office.upload.size=${OFFICE_UPLOAD_SIZE:-100}
EOF

cat > "$APP_CLASSES/page.properties" <<EOF
page.title=${PAGE_TITLE:-BPMT}
page.copyright=${PAGE_COPYRIGHT:-copyright 2012-2016 Riversoft Designs}
page.browser.msg=${PAGE_BROWSER_MSG:-}
page.browser.url=${PAGE_BROWSER_URL:-}
page.frame.new=${PAGE_FRAME_NEW:-false}
page.randomcode=${PAGE_RANDOM_CODE:-3}
page.taskpanel=${PAGE_TASK_PANEL:-true}
EOF

cat > "$APP_CLASSES/wx.properties" <<EOF
wx.web.login.qrcode=${WX_WEB_LOGIN_QRCODE:-false}
wx.web.appId=${WX_WEB_APP_ID:-}
wx.web.appSecret=${WX_WEB_APP_SECRET:-}
wx.web.mp.appIds=${WX_WEB_MP_APP_IDS:-}
wx.open.flag=${WX_OPEN_FLAG:-false}
wx.open.appId=${WX_OPEN_APP_ID:-}
wx.open.appSecret=${WX_OPEN_APP_SECRET:-}
wx.open.table=${WX_OPEN_TABLE:-}
wx.net.https=${WX_NET_HTTPS:-false}
wx.net.domain=${WX_NET_DOMAIN:-localhost}
wx.qy.flag=${WX_QY_FLAG:-false}
wx.qy.corpId=${WX_QY_CORP_ID:-}
wx.qy.corpSecret=${WX_QY_CORP_SECRET:-}
wx.qy.default=${WX_QY_DEFAULT:-0}
wx.qy.contactmode=${WX_QY_CONTACT_MODE:-0}
wx.qy.pay.flag=${WX_QY_PAY_FLAG:-false}
EOF

exec "$@"
