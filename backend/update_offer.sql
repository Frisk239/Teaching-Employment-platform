-- 更新Offer记录，填充缺失的字段
UPDATE t_offer SET
  offer_no = 'OFFER-20260118-001',
  city = '杭州',
  salary_unit = 'year',
  accept_deadline = DATE_ADD(CURDATE(), INTERVAL 30 DAY),
  email_status = 'sent',
  email_send_time = NOW(),
  remark = '诚邀加入阿里巴巴，共创未来！'
WHERE id = 2;

-- 查看更新后的结果
SELECT id, offer_no, position_name, city, salary, salary_unit, accept_deadline, email_status FROM t_offer WHERE id = 2;
