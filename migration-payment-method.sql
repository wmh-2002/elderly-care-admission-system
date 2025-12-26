-- 迁移脚本：将bill表的pay_type字段重命名为payment_method

-- 1. 检查是否存在旧字段
-- 2. 如果存在旧字段，将其重命名为新字段名

-- 注意：这个脚本假设数据库是MySQL
-- 如果表中已经存在payment_method字段，这将失败

-- 首先检查是否存在payment_method字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'bill'
    AND COLUMN_NAME = 'payment_method'
);

-- 如果payment_method字段不存在，则重命名pay_type字段
SET @sql = IF(@column_exists = 0,
    'ALTER TABLE bill CHANGE COLUMN pay_type payment_method ENUM(\'现金\', \'微信\', \'支付宝\', \'银行卡\', \'其它\') DEFAULT \'现金\' COMMENT \'支付方式\'',
    'SELECT "payment_method column already exists" as message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证结果
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_DEFAULT, COLUMN_COMMENT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'bill'
AND COLUMN_NAME IN ('pay_type', 'payment_method')
ORDER BY COLUMN_NAME;
