-- Insert system permissions
INSERT INTO t_permission (permission_code, permission_name, resource_type, description) VALUES
-- User management permissions
('user:view', 'View Users', 'menu', 'View user list and details'),
('user:create', 'Create User', 'button', 'Create new user'),
('user:update', 'Update User', 'button', 'Update user information'),
('user:delete', 'Delete User', 'button', 'Delete user'),
('user:export', 'Export Users', 'button', 'Export user data'),

-- Role management permissions
('role:view', 'View Roles', 'menu', 'View role list and details'),
('role:create', 'Create Role', 'button', 'Create new role'),
('role:update', 'Update Role', 'button', 'Update role information'),
('role:delete', 'Delete Role', 'button', 'Delete role'),
('role:assign', 'Assign Permissions', 'button', 'Assign permissions to role'),

-- Menu management permissions
('menu:view', 'View Menus', 'menu', 'View menu list and details'),
('menu:create', 'Create Menu', 'button', 'Create new menu'),
('menu:update', 'Update Menu', 'button', 'Update menu information'),
('menu:delete', 'Delete Menu', 'button', 'Delete menu'),

-- School management permissions
('school:view', 'View Schools', 'menu', 'View school list'),
('school:create', 'Create School', 'button', 'Create new school'),
('school:update', 'Update School', 'button', 'Update school information'),
('school:delete', 'Delete School', 'button', 'Delete school'),

-- Teacher management permissions
('teacher:view', 'View Teachers', 'menu', 'View teacher list'),
('teacher:create', 'Create Teacher', 'button', 'Create new teacher'),
('teacher:update', 'Update Teacher', 'button', 'Update teacher information'),
('teacher:delete', 'Delete Teacher', 'button', 'Delete teacher'),

-- Student management permissions
('student:view', 'View Students', 'menu', 'View student list'),
('student:create', 'Create Student', 'button', 'Create new student'),
('student:update', 'Update Student', 'button', 'Update student information'),
('student:delete', 'Delete Student', 'button', 'Delete student'),

-- Company management permissions
('company:view', 'View Companies', 'menu', 'View company list'),
('company:create', 'Create Company', 'button', 'Create new company'),
('company:update', 'Update Company', 'button', 'Update company information'),
('company:delete', 'Delete Company', 'button', 'Delete company'),

-- Position management permissions
('position:view', 'View Positions', 'menu', 'View position list'),
('position:create', 'Create Position', 'button', 'Create new position'),
('position:update', 'Update Position', 'button', 'Update position information'),
('position:delete', 'Delete Position', 'button', 'Delete position');
