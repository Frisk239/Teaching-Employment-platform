-- 教室表结构迁移脚本
-- 从旧表结构迁移到新表结构（与实体类匹配）
-- 执行时间: 2026-01-18

USE teaching_employment_platform;

-- 检查当前表结构
DESC t_classroom;

-- 1. 添加新字段
ALTER TABLE t_classroom
  ADD COLUMN classroom_name VARCHAR(100) COMMENT '教室名称' AFTER room_number,
  ADD COLUMN floor VARCHAR(20) COMMENT '楼层' AFTER building_name,
  ADD COLUMN has_projector INT DEFAULT 0 COMMENT '是否有投影仪：1-是 0-否' AFTER room_type,
  ADD COLUMN has_computer INT DEFAULT 0 COMMENT '是否有电脑：1-是 0-否' AFTER has_projector,
  ADD COLUMN has_multimedia INT DEFAULT 0 COMMENT '是否多媒体教室：1-是 0-否' AFTER has_computer;

-- 2. 重命名字段以匹配实体类
ALTER TABLE t_classroom
  CHANGE COLUMN building_name building VARCHAR(50) COMMENT '所在楼栋',
  CHANGE COLUMN room_number classroom_no VARCHAR(50) COMMENT '教室编号',
  CHANGE COLUMN room_type classroom_type VARCHAR(50) COMMENT '教室类型';

-- 3. 添加索引
ALTER TABLE t_classroom
  ADD INDEX idx_school_id (school_id),
  ADD INDEX idx_classroom_no (classroom_no);

-- 4. 更新现有数据，为缺失的字段填充默认值
UPDATE t_classroom
SET
  classroom_name = IFNULL(classroom_name, CONCAT_WS('-', building, classroom_no)),
  floor = IFNULL(floor,
    CASE
      WHEN CAST(classroom_no AS UNSIGNED) BETWEEN 100 AND 199 THEN 'F1'
      WHEN CAST(classroom_no AS UNSIGNED) BETWEEN 200 AND 299 THEN 'F2'
      WHEN CAST(classroom_no AS UNSIGNED) BETWEEN 300 AND 399 THEN 'F3'
      WHEN CAST(classroom_no AS UNSIGNED) BETWEEN 400 AND 499 THEN 'F4'
      WHEN CAST(classroom_no AS UNSIGNED) BETWEEN 500 AND 599 THEN 'F5'
      ELSE 'F1'
    END
  ),
  has_projector = IFNULL(has_projector,
    IF(classroom_type IN ('多媒体教室', '阶梯教室', '计算机教室'), 1, 0)
  ),
  has_multimedia = IFNULL(has_multimedia,
    IF(classroom_type IN ('多媒体教室', '阶梯教室'), 1, 0)
  ),
  has_computer = IFNULL(has_computer,
    IF(classroom_type = '计算机教室', 1, 0)
  );

-- 5. 验证迁移结果
SELECT '=== 迁移后的表结构 ===' AS '';
DESC t_classroom;

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
