SELECT oc.pickup_at AS `Pickup Date`,
       oc.reference_number as 'Reference Number', oc.tracking_id as 'TrackingId', oc.customer_account as 'Account Number', cc.name as 'Account Name', oc.product_code,
       oc.origin_country,
       oc.origin_city,
       oc.destination_country,
       oc.destination_city,
       oc.destination_district,
       LEAST(
               COALESCE(oc.chargeable_weight, oc.weight),
               COALESCE(oc.weight, oc.chargeable_weight)
       )            AS chargeable_weight,
       oc.weight_unit,
       oc.cod_amount,
       oc.cod_currency,
       aoc.declared_value,
       aoc.declared_value_currency,
       oc.`status`
FROM `aone-ops`.consignments oc
         LEFT JOIN
     `aone-customers`.customer_account cc
     ON oc.customer_account = cc.account_number
         LEFT JOIN
     `aone-orders`.consignments aoc
     ON aoc.tracking_id = oc.tracking_id
WHERE oc.pickup_at BETWEEN (UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) - 8 * 3600) *
                           1000 AND (UNIX_TIMESTAMP(CURDATE()) - 8 * 3600 - 1) * 1000
  and oc.rto = 0
  AND oc.customer_account IN (
                              'AJEX850',
                              'AJCN63', 'AJEX0155', 'AJCN711', 'AJ402787000004', 'AJ402787000005',
                              'AJCN650', 'AJCN59', 'AJCN62', 'AJCN61', 'AJ402789000001', 'AJ288445000001',
                              'AJEX838', 'AJEX707', 'AJEX784', 'AJCN862', 'AJEX457', 'AJEX0129', 'AJCN35',
                              'AJEX0277', 'AJCN537', 'AJEX0283', 'AJEX0170', 'AJEX584', 'AJEX1542', 'AJCN77',
                              'AJEX1578', 'AJCN79', 'AJCN80', 'AJCN514', 'AJCN81', 'AJEX1515', 'AJEX1616', 'AJCN589',
                              'AJCN88', 'AJCN83', 'AJCN91', 'AJCN86', 'AJCN93', 'AJEX0218', 'AJEX1770', 'AJCN96',
                              'AJCN55', 'AJCN95',
                              'AJCN68', 'AJCN78', 'AJCN93'
    );