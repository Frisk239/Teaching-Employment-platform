# System Management Module API Integration - Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Fix the system management module (Users, Roles, Menus) by removing mock data and integrating with real backend APIs to enable full CRUD operations.

**Architecture:** Replace hardcoded mock data in Vue components with real API calls. Backend APIs are already implemented in UserController, RoleController, and MenuController. Frontend needs to update API functions and remove TODO comments.

**Tech Stack:**
- Frontend: Vue 3 + TypeScript + Element Plus + Axios
- Backend: Spring Boot 2.7.18 + MyBatis Plus
- API Base Path: `/api`

---

## Task 1: Update User API Functions

**Files:**
- Modify: `frontend/src/api/user.ts`

**Context:** Current `user.ts` lacks create, update, and batch delete functions. Backend already has these endpoints implemented.

**Step 1: Read current user.ts to understand existing structure**

```bash
cat frontend/src/api/user.ts
```

Expected: See existing functions like `getUserPageApi`, `deleteUserApi`

**Step 2: Add missing API functions**

Add these functions to `frontend/src/api/user.ts`:

```typescript
/**
 * 创建用户
 */
export function createUserApi(data: User) {
  return http.post('/user', data)
}

/**
 * 更新用户
 */
export function updateUserApi(id: number, data: User) {
  return http.put(`/user/${id}`, data)
}

/**
 * 批量删除用户
 */
export function batchDeleteUserApi(ids: number[]) {
  return http.delete('/user/batch', { data: ids })
}

/**
 * 导出用户
 */
export function exportUsersApi(params?: any) {
  return http.get('/user/export', {
    params,
    responseType: 'blob'
  })
}

/**
 * 重置密码
 */
export function resetPasswordApi(id: number, newPassword: string) {
  return http.put(`/user/${id}/password`, { newPassword })
}
```

**Step 3: Commit user.ts changes**

```bash
git add frontend/src/api/user.ts
git commit -m "feat: add missing user management API functions (create, update, batch delete, export, reset password)"
```

---

## Task 2: Replace Mock Data in Users.vue with Real API

**Files:**
- Modify: `frontend/src/views/system/Users.vue:424-474`

**Context:** Current implementation uses hardcoded mock data starting around line 424. Need to replace with real API calls.

**Step 1: Read Users.vue to locate loadUsers function**

```bash
grep -n "loadUsers\|TODO.*API\|模拟数据" frontend/src/views/system/Users.vue | head -20
```

Expected: Find lines with TODO comments and mock data

**Step 2: Update loadUsers function to use real API**

Replace the mock data implementation (around line 424-474) with:

```typescript
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      roleId: searchForm.value.roleId || undefined
    }
    const data = await getUserPageApi(params)
    users.value = data.records || []
    filteredUsers.value = users.value

    // Update pagination
    pagination.total = data.total || 0
  } catch (error: any) {
    console.error('加载用户列表失败:', error)
    ElMessage.error(error.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}
```

**Step 3: Update handleSave function to use real API**

Replace the mock save implementation (around line 640-649) with:

```typescript
const handleSave = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (!valid) return false

    saving.value = true
    try {
      const formData = { ...userForm.value }

      if (formData.id) {
        // Update existing user
        await updateUserApi(formData.id, formData)
        ElMessage.success('用户更新成功')
      } else {
        // Create new user
        await createUserApi(formData)
        ElMessage.success('用户创建成功')
      }

      dialogVisible.value = false
      loadUsers() // Reload list
      return true
    } catch (error: any) {
      console.error('保存用户失败:', error)
      ElMessage.error(error.message || '保存用户失败')
      return false
    } finally {
      saving.value = false
    }
  })
}
```

**Step 4: Update handleDelete function to use real API**

Replace the mock delete implementation with:

```typescript
const handleDelete = async (row: User) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
      type: 'warning'
    })

    await deleteUserApi(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error(error.message || '删除用户失败')
    }
  }
}
```

**Step 5: Add batch delete and export functions**

Add these functions to handle batch operations:

```typescript
// Batch delete
const handleBatchDelete = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
      '批量删除',
      { type: 'warning' }
    )

    const ids = selectedUsers.value.map(u => u.id)
    await batchDeleteUserApi(ids)
    ElMessage.success('批量删除成功')
    selectedUsers.value = []
    loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error(error.message || '批量删除失败')
    }
  }
}

// Export users
const handleExport = async () => {
  try {
    const blob = await exportUsersApi({
      keyword: searchKeyword.value,
      roleId: searchForm.value.roleId
    })

    // Create download link
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = `users_${Date.now()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error: any) {
    console.error('导出失败:', error)
    ElMessage.error(error.message || '导出失败')
  }
}
```

**Step 6: Update template to add batch delete and export buttons**

Add these buttons to the toolbar section (after the search button):

```vue
<el-button
  type="danger"
  :disabled="selectedUsers.length === 0"
  @click="handleBatchDelete"
>
  批量删除
</el-button>

<el-button @click="handleExport">
  导出用户
</el-button>
```

**Step 7: Commit Users.vue changes**

```bash
git add frontend/src/views/system/Users.vue
git commit -m "fix: replace mock data with real API calls in Users.vue

- Remove hardcoded mock data
- Implement real API calls for CRUD operations
- Add batch delete functionality
- Add export functionality
- Fix user save (create/update) to use backend API
- Fix user delete to use backend API
"
```

---

## Task 3: Update Role API Functions

**Files:**
- Modify: `frontend/src/api/role.ts`

**Step 1: Read current role.ts**

```bash
cat frontend/src/api/role.ts
```

**Step 2: Add missing API functions**

Add to `frontend/src/api/role.ts`:

```typescript
/**
 * 创建角色
 */
export function createRoleApi(data: Role) {
  return http.post('/role', data)
}

/**
 * 更新角色
 */
export function updateRoleApi(id: number, data: Role) {
  return http.put(`/role/${id}`, data)
}

/**
 * 批量删除角色
 */
export function batchDeleteRoleApi(ids: number[]) {
  return http.delete('/role/batch', { data: ids })
}
```

**Step 3: Commit role.ts changes**

```bash
git add frontend/src/api/role.ts
git commit -m "feat: add missing role management API functions (create, update, batch delete)"
```

---

## Task 4: Replace Mock Data in Roles.vue

**Files:**
- Modify: `frontend/src/views/system/Roles.vue`

**Step 1: Find TODO and mock data in Roles.vue**

```bash
grep -n "TODO\|模拟数据" frontend/src/views/system/Roles.vue
```

**Step 2: Replace mock data with real API calls**

Similar pattern to Users.vue:
- Update `loadRoles` to use `getRolePageApi`
- Update `handleSave` to use `createRoleApi`/`updateRoleApi`
- Update `handleDelete` to use `deleteRoleApi`

**Step 3: Commit Roles.vue changes**

```bash
git add frontend/src/views/system/Roles.vue
git commit -m "fix: replace mock data with real API calls in Roles.vue

- Remove hardcoded mock data
- Implement real API calls for CRUD operations
- Fix role save (create/update) to use backend API
- Fix role delete to use backend API
"
```

---

## Task 5: Update Menu API Functions

**Files:**
- Modify: `frontend/src/api/menu.ts`

**Step 1: Read current menu.ts**

```bash
cat frontend/src/api/menu.ts
```

**Step 2: Add missing API functions**

Add to `frontend/src/api/menu.ts`:

```typescript
/**
 * 创建菜单
 */
export function createMenuApi(data: Menu) {
  return http.post('/menu', data)
}

/**
 * 更新菜单
 */
export function updateMenuApi(id: number, data: Menu) {
  return http.put(`/menu/${id}`, data)
}

/**
 * 批量删除菜单
 */
export function batchDeleteMenuApi(ids: number[]) {
  return http.delete('/menu/batch', { data: ids })
}

/**
 * 展开/折叠全部菜单
 */
export function expandAllMenusApi() {
  return http.post('/menu/expand-all')
}

export function collapseAllMenusApi() {
  return http.post('/menu/collapse-all')
}
```

**Step 3: Commit menu.ts changes**

```bash
git add frontend/src/api/menu.ts
git commit -m "feat: add missing menu management API functions (create, update, batch delete, expand/collapse)"
```

---

## Task 6: Replace Mock Data in Menus.vue and Fix Buttons

**Files:**
- Modify: `frontend/src/views/system/Menus.vue`

**Step 1: Find TODO and mock data in Menus.vue**

```bash
grep -n "TODO\|模拟数据\|展开全部\|折叠全部" frontend/src/views/system/Menus.vue
```

**Step 2: Replace mock data with real API calls**

Similar pattern to Users.vue:
- Update `loadMenus` to use `getMenuTreeApi`
- Update `handleSave` to use `createMenuApi`/`updateMenuApi`
- Update `handleDelete` to use `deleteMenuApi`

**Step 3: Fix expand/collapse buttons**

Ensure buttons have proper click handlers:

```vue
<el-button @click="handleExpandAll">展开全部</el-button>
<el-button @click="handleCollapseAll">折叠全部</el-button>
```

Add handler functions:

```typescript
const handleExpandAll = () => {
  // Expand all tree nodes
  const expandNode = (nodes: any[]) => {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        expandedKeys.value.push(node.id)
        expandNode(node.children)
      }
    })
  }
  expandNode(menuTree.value)
}

const handleCollapseAll = () => {
  expandedKeys.value = []
}
```

**Step 4: Commit Menus.vue changes**

```bash
git add frontend/src/views/system/Menus.vue
git commit -m "fix: replace mock data with real API calls in Menus.vue

- Remove hardcoded mock data
- Implement real API calls for CRUD operations
- Fix expand/collapse all buttons functionality
- Fix menu save (create/update) to use backend API
- Fix menu delete to use backend API
"
```

---

## Task 7: Add Missing Backend Endpoints

**Files:**
- Modify: `backend/src/main/java/com/teaching/employment/controller/UserController.java`
- Modify: `backend/src/main/java/com/teaching/employment/controller/RoleController.java`
- Modify: `backend/src/main/java/com/teaching/employment/controller/MenuController.java`

**Step 1: Add batch delete endpoint to UserController**

Add to `UserController.java`:

```java
/**
 * 批量删除用户
 */
@DeleteMapping("/batch")
@ApiOperation("批量删除用户")
public Result<Void> batchDelete(@RequestBody List<Long> ids) {
    userService.removeByIds(ids);
    return Result.success();
}
```

**Step 2: Add export endpoint to UserController**

```java
/**
 * 导出用户
 */
@GetMapping("/export")
@ApiOperation("导出用户")
public void export(HttpServletResponse response, HttpServletRequest request) {
    try {
        List<User> users = userService.list();
        ExcelUtil.exportExcel(response, users, "用户数据", User.class);
    } catch (IOException e) {
        throw new BusinessException("导出失败");
    }
}
```

**Step 3: Add batch delete to RoleController and MenuController**

Similar pattern as UserController.

**Step 4: Commit backend changes**

```bash
git add backend/src/main/java/com/teaching/employment/controller/
git commit -m "feat: add batch delete and export endpoints to system management controllers

- Add batch delete endpoint to UserController
- Add export endpoint to UserController
- Add batch delete endpoint to RoleController
- Add batch delete endpoint to MenuController
"
```

---

## Task 8: Test All CRUD Operations

**Step 1: Start backend server**

```bash
cd backend
mvn spring-boot:run
```

Verify server starts on http://localhost:8080

**Step 2: Start frontend server**

```bash
cd frontend
npm run dev
```

Verify server starts on http://localhost:8081

**Step 3: Manual testing checklist**

Test each module with the following operations:

**User Management:**
- [ ] View user list (pagination works)
- [ ] Search users by keyword
- [ ] Create new user
- [ ] Edit existing user
- [ ] Delete single user
- [ ] Batch delete users
- [ ] Export users to Excel

**Role Management:**
- [ ] View role list
- [ ] Create new role
- [ ] Edit existing role
- [ ] Delete role
- [ ] Assign permissions to role
- [ ] Verify permission count matches

**Menu Management:**
- [ ] View menu tree
- [ ] Create new menu
- [ ] Edit existing menu
- [ ] Delete menu
- [ ] Expand all menus button works
- [ ] Collapse all menus button works
- [ ] Menu names match actual sidebar menu

**Step 4: Verify data persistence**

Check that changes are saved to database:

```bash
mysql -u root -p teaching_employment_platform -e "
SELECT * FROM t_user ORDER BY update_time DESC LIMIT 5;
SELECT * FROM t_role ORDER BY update_time DESC LIMIT 5;
SELECT * FROM t_menu ORDER BY update_time DESC LIMIT 5;
"
```

Expected: See newly created/updated records

**Step 5: Fix any discovered issues**

If tests fail, use @superpowers:systematic-debugging to investigate root cause before applying fixes.

**Step 6: Final commit**

```bash
git add .
git commit -m "test: verify system management module API integration

- Tested all CRUD operations for Users, Roles, Menus
- Verified data persistence in database
- All tests passing
"
```

---

## Testing Strategy

### Unit Tests
No new unit tests required for this task (existing backend tests cover API endpoints).

### Integration Tests
Manual testing with real API calls using the checklist in Task 8.

### Verification Criteria
- ✅ All CRUD operations work for Users, Roles, Menus
- ✅ No mock data in frontend components
- ✅ All changes persist to database
- ✅ Expand/Collapse buttons work in Menu management
- ✅ Batch delete works
- ✅ Export functionality works
- ✅ No console errors during operations

---

## Rollback Plan

If issues arise:
```bash
git revert HEAD~8..HEAD  # Revert all commits from this plan
```

---

## Next Steps

After completing this plan:
1. Create plan for P0-2: Fix Student Exam Functionality
2. Create plan for P0-3: Add Code Lines Field to Daily Report
3. Proceed to P1 tasks

---

**Total Estimated Time:** 3-4 hours
**Number of Tasks:** 8
**Number of Commits:** ~12 commits (following TDD and frequent commits principle)
