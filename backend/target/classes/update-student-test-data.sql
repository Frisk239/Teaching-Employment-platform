-- 更新学员基本信息测试数据
UPDATE t_student SET
  gender = 1,
  birth_date = '2003-05-15',
  id_card = '110101200305151234',
  phone = '13800002001',
  email = 'zhangsan@student.com',
  qq = '123456789',
  wechat = 'zhangsan_wx',
  address = 'Beijing Chaoyang',
  bio = 'Love programming and good team player',
  political_status = 'mass',
  nation = 'Han'
WHERE id = 1;
