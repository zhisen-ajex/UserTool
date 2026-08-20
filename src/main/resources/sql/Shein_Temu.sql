SELECT
    DATE_FORMAT(FROM_UNIXTIME(c.created_at/1000+ 8 *3600), '%m-%d') AS `Date`,
    c.customer_account as `account`,
    # CONCAT_WS(' - ', cc.`name`, substring(c.product_code, -3), c.destination_country) as `name`,
    CASE cc.`name`
        WHEN 'WHALECO TECHNOLOGY LIMITED' THEN CONCAT('Temu', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3), c.destination_country))
        WHEN 'XIYIN E COMMERCE FZE' THEN CONCAT('Shein', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3), c.destination_country))
        ELSE 'Hardy'
        END AS `name`,
    CONCAT_WS(' - ', substring(c.product_code, -3), c.destination_country) as `type`,
    c.product_code AS `product_code`,
    c.destination_country as `des`,
    SUM(1) AS `Data Register`,
    COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END) as `pickps`,
    COUNT(CASE WHEN c.cod_amount < 0 OR c.cod_amount IS NULL THEN 1 END) AS `PPD`,
    COUNT(CASE WHEN c.cod_amount > 0 THEN 1 END) AS `COD`,
    CONCAT(ROUND(SUM(CASE WHEN c.first_ofd_date IS NOT NULL AND c.first_ofd_date - c.pickup_at <= 3 * 86400000 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END), 0), 2), '%') AS `3 days`,
    CONCAT(ROUND(SUM(CASE WHEN c.first_ofd_date IS NOT NULL AND c.first_ofd_date - c.pickup_at <= 5 * 86400000 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END), 0), 2), '%') AS `5 days`,
    CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' THEN 1 ELSE 0 END) * 100.0 / NULLIF(SUM(1), 0), 2), '%') AS `SDR`,
    CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' AND c.cod_amount > 0 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.cod_amount > 0 THEN 1 END), 0), 2), '%') AS `COD SDR`,
    CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' AND (c.cod_amount < 0 OR c.cod_amount IS NULL) THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.cod_amount < 0 OR c.cod_amount IS NULL THEN 1 END), 0), 2), '%') AS `PPD SDR`,
    CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' AND (c.last_ofd_date IS NULL OR c.last_ofd_date = c.first_ofd_date) AND c.cod_amount > 0  THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.cod_amount > 0  AND c.first_ofd_date IS NOT NULL  THEN 1 END), 0), 2), '%') AS `1st OFD SDR`,
    SUM(GREATEST(COALESCE(c.chargeable_weight, c.weight),COALESCE(c.weight, c.chargeable_weight))) AS `chargeable_weight`,
    CASE cc.`name`
        WHEN 'WHALECO TECHNOLOGY LIMITED' THEN 'Temu'
        WHEN 'XIYIN E COMMERCE FZE' THEN 'Shein'
        ELSE 'Hardy'
        END AS `2name`
FROM
    `aone-ops`.consignments c
        LEFT JOIN
    `aone-customers`.customer_account cc
    ON c.customer_account = cc.account_number
WHERE
    c.created_at  BETWEEN (UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY)) - 8 * 3600) *1000
        AND (UNIX_TIMESTAMP(CURDATE()) - 8 * 3600 - 1) * 1000
  and c.rto=0
  AND c.status_code != 102
    AND c.destination_country IN ('ARE', 'SAU', 'KWT', 'BHR')
    AND c.customer_account IN (
                               'AJEX850','AJEX1770'
    )
GROUP BY
    `Date`, c.customer_account, cc.`name`, c.destination_country, c.product_code
ORDER BY
    `Date`, c.destination_country, c.customer_account;