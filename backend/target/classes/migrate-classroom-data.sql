-- 教室表数据迁移脚本（补充）
-- 执行时间: 2026-01-18

USE teaching_employment_platform;

-- 更新现有数据，为缺失的字段填充默认值
UPDATE t_classroom
SET
  classroom_name = IFNULL(classroom_name, CONCAT_WS('-', building, classroom_no)),
  floor = IFNULL(floor, 'F1'),
  has_projector = IFNULL(has_projector, 1),
  has_multimedia = IFNULL(has_multimedia, 1),
  has_computer = IFNULL(has_computer, 0);

-- 验证迁移结果
SELECT '=== 迁移后的数据示例 ===' AS '';
SELECT
  id,
  school_id,
  classroom_no,
  classroom_name,
  building,
  floor,
  capacity,
  classroom_type,
  has_projector,
  has_computer,
  has_multimedia,
  status
FROM t_classroom
LIMIT 10;

SELECT '=== 统计信息 ===' AS '';
SELECT
  COUNT(*) AS '教室总数',
  SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS '可用教室',
  SUM(CASE WHEN has_projector = 1 THEN 1 ELSE 0 END) AS '有投影仪',
  SUM(CASE WHEN has_computer = 1 THEN 1 ELSE 0 END) AS '有电脑',
  SUM(CASE WHEN has_multimedia = 1 THEN 1 ELSE 0 END) AS '多媒体教室'
FROM t_classroom;
