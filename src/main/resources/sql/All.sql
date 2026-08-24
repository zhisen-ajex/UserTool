SELECT DATE_FORMAT(FROM_UNIXTIME(c.pickup_at / 1000 + 8 * 3600), '%m-%d')                                        AS `Pickup Date`,
       c.customer_account as 'account', CASE cc.`name`
                                            WHEN 'AJEX China Cash' THEN CONCAT('Cash', ' - ', CONCAT_WS(' - ',
                                                                                                        substring(c.product_code, -3),
                                                                                                        c.destination_country))
                                            WHEN 'Changre Haishuo Culture Tourism Products Co Ltd.' THEN CONCAT(
                                                    'Haishuo', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3),
                                                                                c.destination_country))
                                            WHEN 'CIDER (SG) HOLDING PTE LTD' THEN CONCAT('Cider', ' - ',
                                                                                          CONCAT_WS(' - ',
                                                                                                    substring(c.product_code, -3),
                                                                                                    c.destination_country))
                                            WHEN 'DONGGUAN HENGHOLD TRADING CO., LTD' THEN CONCAT('Henghold', ' - ',
                                                                                                  CONCAT_WS(' - ',
                                                                                                            substring(c.product_code, -3),
                                                                                                            c.destination_country))
                                            WHEN 'ELESTOM LIMITED' THEN CONCAT('Elestom', ' - ', CONCAT_WS(' - ',
                                                                                                           substring(c.product_code, -3),
                                                                                                           c.destination_country))
                                            WHEN 'GUANGDONG ZHIHUI BAY INTERNATIONAL TRADE CO LTD' THEN CONCAT(
                                                    'Zhihuibay', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3),
                                                                                  c.destination_country))
                                            WHEN 'Guangzhou Chaoju Technology Co., Ltd.' THEN CONCAT('Chaoju', ' - ',
                                                                                                     CONCAT_WS(' - ',
                                                                                                               substring(c.product_code, -3),
                                                                                                               c.destination_country))
                                            WHEN 'Guangzhou Yiyan Trading Co., Ltd.' THEN CONCAT('Yiyan', ' - ',
                                                                                                 CONCAT_WS(' - ',
                                                                                                           substring(c.product_code, -3),
                                                                                                           c.destination_country))
                                            WHEN 'HANGZHOU SWORDFISH E-COMMERCE CO., LTD' THEN CONCAT('Swordfish',
                                                                                                      ' - ',
                                                                                                      CONCAT_WS(' - ',
                                                                                                                substring(c.product_code, -3),
                                                                                                                c.destination_country))
                                            WHEN 'HARBIN LUYA CROSS-BORDER E-COMMERCE CO., LTD' THEN CONCAT('Luya',
                                                                                                            ' - ',
                                                                                                            CONCAT_WS(
                                                                                                                    ' - ',
                                                                                                                    substring(c.product_code, -3),
                                                                                                                    c.destination_country))
                                            WHEN 'Hibobi Technology Limited' THEN CONCAT('Hibobi', ' - ',
                                                                                         CONCAT_WS(' - ',
                                                                                                   substring(c.product_code, -3),
                                                                                                   c.destination_country))
                                            WHEN 'HK JILIANG JIYING TRADING CO., LIMITED' THEN CONCAT('Jiliangjiying',
                                                                                                      ' - ',
                                                                                                      CONCAT_WS(' - ',
                                                                                                                substring(c.product_code, -3),
                                                                                                                c.destination_country))
                                            WHEN 'HONG KONG NEWBELLA TRADING CO LIMITED' THEN CONCAT('NewBella', ' - ',
                                                                                                     CONCAT_WS(' - ',
                                                                                                               substring(c.product_code, -3),
                                                                                                               c.destination_country))
                                            WHEN 'NEW CHIC WORLD (HK) TECHNOLOGY LIMITED1' THEN CONCAT('NewChic', ' - ',
                                                                                                       CONCAT_WS(' - ',
                                                                                                                 substring(c.product_code, -3),
                                                                                                                 c.destination_country))
                                            WHEN 'SAILEE NETWORK TECHNOLOGY CO LIMITED' THEN CONCAT('Sailee', ' - ',
                                                                                                    CONCAT_WS(' - ',
                                                                                                              substring(c.product_code, -3),
                                                                                                              c.destination_country))
                                            WHEN 'SHENZHEN JUZHIYUAN TECHNOLOGY CO., LTD' THEN CONCAT('Juzhiyuan',
                                                                                                      ' - ',
                                                                                                      CONCAT_WS(' - ',
                                                                                                                substring(c.product_code, -3),
                                                                                                                c.destination_country))
                                            WHEN 'Shenzhen Piaeon Intemational Logistics Co Ltd' THEN CONCAT('Piaeon',
                                                                                                             ' - ',
                                                                                                             CONCAT_WS(
                                                                                                                     ' - ',
                                                                                                                     substring(c.product_code, -3),
                                                                                                                     c.destination_country))
                                            WHEN 'SHENZHEN TIANJIN TRADING CO. LTD' THEN CONCAT('Tianjin', ' - ',
                                                                                                CONCAT_WS(' - ',
                                                                                                          substring(c.product_code, -3),
                                                                                                          c.destination_country))
                                            WHEN 'SHENZHEN YITENG SUPPLY CHAIN CO LTD' THEN CONCAT('Yiteng', ' - ',
                                                                                                   CONCAT_WS(' - ',
                                                                                                             substring(c.product_code, -3),
                                                                                                             c.destination_country))
                                            WHEN 'Shenzhen Youyijia ElectronicTechnology Co., Ltd.' THEN CONCAT(
                                                    'Youyijia', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3),
                                                                                 c.destination_country))
                                            WHEN 'Sinotrans Almajdouie Middle East Company' THEN CONCAT('Sinotrams',
                                                                                                        ' - ',
                                                                                                        CONCAT_WS(' - ',
                                                                                                                  substring(c.product_code, -3),
                                                                                                                  c.destination_country))
                                            WHEN 'WEIHAI MENGZHEN NETWORK TECHNOLOGY CO., LTD.' THEN CONCAT(
                                                    'Weihaimengzhen', ' - ',
                                                    CONCAT_WS(' - ', substring(c.product_code, -3),
                                                              c.destination_country))
                                            WHEN 'WHALECO TECHNOLOGY LIMITED' THEN CONCAT('Temu', ' - ',
                                                                                          CONCAT_WS(' - ',
                                                                                                    substring(c.product_code, -3),
                                                                                                    c.destination_country))
                                            WHEN 'XIYIN E COMMERCE FZE' THEN CONCAT('Shein', ' - ', CONCAT_WS(' - ',
                                                                                                              substring(c.product_code, -3),
                                                                                                              c.destination_country))
                                            WHEN 'YESSTYLE.COM LIMITED' THEN CONCAT('YesStyle', ' - ', CONCAT_WS(' - ',
                                                                                                                 substring(c.product_code, -3),
                                                                                                                 c.destination_country))
                                            WHEN 'YIBANG (SHENZHEN) INTERNATIONAL LOGISTICS CO.LTD' THEN CONCAT(
                                                    'Yibang', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3),
                                                                               c.destination_country))
                                            WHEN 'YINENGDA E-COMMERCE SERVICE (SHENZHEN) CO LTD' THEN CONCAT('Yinengda',
                                                                                                             ' - ',
                                                                                                             CONCAT_WS(
                                                                                                                     ' - ',
                                                                                                                     substring(c.product_code, -3),
                                                                                                                     c.destination_country))
                                            WHEN 'DONGGUAN ZHIWEI E-COMMERCE CO., LTD' THEN CONCAT('ZHIWEI', ' - ',
                                                                                                   CONCAT_WS(' - ',
                                                                                                             substring(c.product_code, -3),
                                                                                                             c.destination_country))
                                            WHEN 'NLP supply china Tech.CO.LTD' THEN CONCAT('NLP', ' - ',
                                                                                            CONCAT_WS(' - ',
                                                                                                      substring(c.product_code, -3),
                                                                                                      c.destination_country))
                                            WHEN 'Shenzhen Fengwang Supply Chain Technology Co., Ltd' THEN CONCAT(
                                                    'Fengwang', ' - ', CONCAT_WS(' - ', substring(c.product_code, -3),
                                                                                 c.destination_country))
                                            WHEN 'Guangzhou Yiyan Trading Co., Ltd.' THEN CONCAT('Yiyan', ' - ',
                                                                                                 CONCAT_WS(' - ',
                                                                                                           substring(c.product_code, -3),
                                                                                                           c.destination_country))
                                            WHEN 'Shenzhen Bitan Technology Co., Ltd.' THEN CONCAT('Bitan', ' - ',
                                                                                                   CONCAT_WS(' - ',
                                                                                                             substring(c.product_code, -3),
                                                                                                             c.destination_country))
                                            ELSE 'Hardy'
    END AS `name`,


       c.destination_country as 'des', c.product_code AS `product_code`,
       -- SUM(1) AS `Data Register`,
       COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END) as 'pickps',

    -- 计算 PPD 和 COD 的数量 COUNT(CASE WHEN c.cod_amount < 0 OR c.cod_amount IS NULL THEN 1 END) AS `PPD`,
       COUNT(CASE WHEN c.cod_amount > 0 THEN 1 END)                                                              AS `COD`,

       -- 计算 3 天内的 1st OFD
       CONCAT(ROUND(SUM(CASE
                            WHEN c.first_ofd_date IS NOT NULL AND c.first_ofd_date - c.pickup_at <= 3 * 86400000 THEN 1
                            ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END), 0), 2),
              '%')                                                                                               AS `3 days`,

       CONCAT(ROUND(SUM(CASE
                            WHEN c.first_ofd_date IS NOT NULL AND c.first_ofd_date - c.pickup_at <= 5 * 86400000 THEN 1
                            ELSE 0 END) * 100.0 / NULLIF(COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END), 0), 2),
              '%')                                                                                               AS `5 days`,
       -- 计算 SDR（整体）
       CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' THEN 1 ELSE 0 END) * 100.0 / NULLIF(SUM(1), 0), 2),
              '%')                                                                                               AS `SDR`,

       -- 计算 COD 的 SDR
       CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' AND c.cod_amount > 0 THEN 1 ELSE 0 END) * 100.0 /
                    NULLIF(COUNT(CASE WHEN c.cod_amount > 0 THEN 1 END), 0), 2),
              '%')                                                                                               AS `COD SDR`,

       -- 计算 PPD 的 SDR
       CONCAT(ROUND(SUM(CASE
                            WHEN c.status_code = '604' AND (c.cod_amount < 0 OR c.cod_amount IS NULL) THEN 1
                            ELSE 0 END) * 100.0 /
                    NULLIF(COUNT(CASE WHEN c.cod_amount < 0 OR c.cod_amount IS NULL THEN 1 END), 0), 2),
              '%')                                                                                               AS `PPD SDR`,
       -- 计算COD单次派送成功率（只派送一次 last_ofd_date 为空或不等于 first_ofd_date）
       CONCAT(ROUND(SUM(CASE
                            WHEN c.status_code = '604' AND
                                 (c.last_ofd_date IS NULL OR c.last_ofd_date = c.first_ofd_date) AND c.cod_amount > 0
                                THEN 1
                            ELSE 0 END) * 100.0 /
                    NULLIF(COUNT(CASE WHEN c.cod_amount > 0 AND c.first_ofd_date IS NOT NULL THEN 1 END), 0), 2),
              '%')                                                                                               AS `1st OFD SDR`,

       -- CONCAT(ROUND(SUM(CASE WHEN c.status_code = '604' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0), 2), '%') AS `签收率`
       -- 计算 3 天内的 1st OFD
       CONCAT(ROUND(SUM(CASE WHEN c.first_ofd_date IS NOT NULL THEN 1 ELSE 0 END) * 100.0 /
                    NULLIF(COUNT(CASE WHEN c.pickup_at IS NOT NULL THEN 1 END), 0), 2),
              '%')                                                                                               AS `OFD`


FROM `aone-ops`.consignments c
         LEFT JOIN
     `aone-customers`.customer_account cc
     ON c.customer_account = cc.account_number

WHERE c.pickup_at BETWEEN (UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY)) - 8 * 3600) * 1000
    AND (UNIX_TIMESTAMP(CURDATE()) - 8 * 3600 - 1) * 1000
  and c.rto = 0
  AND c.destination_country IN ('ARE', 'SAU', 'KWT', 'BHR')
  AND c.customer_account IN (
                             'AJEX850',
                             'AJCN63', 'AJEX0155', 'AJCN711', 'AJ402787000004', 'AJ402787000005',
                             'AJCN650', 'AJCN59', 'AJCN62', 'AJCN61', 'AJ402789000001', 'AJ288445000001',
                             'AJEX838', 'AJEX707', 'AJEX784', 'AJCN862', 'AJEX457', 'AJEX0129', 'AJCN35',
                             'AJEX0277', 'AJCN537', 'AJEX0283', 'AJEX0170', 'AJEX584', 'AJEX1542', 'AJCN77', 'AJEX1578',
                             'AJCN79', 'AJCN80', 'AJCN81', 'AJCN514', 'AJEX1616', 'AJEX1515', 'AJCN589', 'AJCN88',
                             'AJCN83', 'AJCN91', 'AJCN86', 'AJCN93', 'AJEX0218', 'AJEX1770', 'AJCN96', 'AJCN55',
                             'AJCN95', 'AJCN68', 'AJCN78', 'AJCN93'
    )
GROUP BY `Pickup Date`,
         c.customer_account,
         cc.`name`,
         c.destination_country,
         c.product_code
ORDER BY `Pickup Date`, c.destination_country;