# Emp & Dept — Bootstrap UI

Plain HTML + Bootstrap 5 + vanilla JS. No build step — just static files.

## 1. Install into your project

Copy this whole `static` folder into:
```
src/main/resources/static
```
Spring Boot serves everything in that folder automatically at the root URL, e.g.
`http://localhost:8080/login.html`.

## 2. Backend endpoints you need to add

The dashboard now supports listing, editing, and deleting departments. It calls:

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/departments/all` | List all departments (for the table) |
| `GET` | `/api/departments/{id}` | Fetch one department (prefills the edit form) |
| `PUT` | `/api/departments/{id}` | Update a department + fully replace its employees |
| `DELETE` | `/api/departments/{id}` | Delete a department (and its employees) |

Add these to `DepartmentService`:
```java
public Department getDepartmentById(Long id) {
    return departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
}

public Department updateDepartmentWithEmployees(Long id, Department updatedDept) {
    Department existing = departmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

    existing.setName(updatedDept.getName());
    existing.getEmployees().clear(); // requires orphanRemoval = true, see below

    if (updatedDept.getEmployees() != null) {
        for (Employee emp : updatedDept.getEmployees()) {
            emp.setId(null);
            emp.setDepartment(existing);
            existing.getEmployees().add(emp);
        }
    }
    return departmentRepository.save(existing);
}

public void deleteDepartment(Long id) {
    if (!departmentRepository.existsById(id)) {
        throw new RuntimeException("Department not found with id: " + id);
    }
    departmentRepository.deleteById(id);
}
```

Add these to `EmpoyeeController`:
```java
@GetMapping("/all")
public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
    List<Department> departments = departmentRepository.findAll();
    return ResponseEntity.ok(ApiResponse.success("Departments fetched successfully", departments));
}

@GetMapping("/{id}")
public ResponseEntity<ApiResponse<Department>> getDepartment(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(ApiResponse.success("Department fetched successfully",
                departmentService.getDepartmentById(id)));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
    }
}

@PutMapping("/{id}")
public ResponseEntity<ApiResponse<Department>> updateDepartment(
        @PathVariable Long id, @RequestBody Department department) {
    try {
        return ResponseEntity.ok(ApiResponse.success("Department updated successfully",
                departmentService.updateDepartmentWithEmployees(id, department)));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
    try {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted successfully"));
    } catch (RuntimeException e) {
        return ResponseEntity.status(404).body(ApiResponse.error(404, e.getMessage()));
    }
}
```

**Important:** your `Department` entity's employee mapping must have `orphanRemoval = true`,
otherwise the "clear and replace" logic in `updateDepartmentWithEmployees` won't delete
old employee rows from the database:
```java
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Employee> employees;
```

Also make sure your `AuthFilter` allow-list only excludes `/login` and `/userRegistor` —
`/api/departments/all` should stay protected like your other endpoints (it already will,
since the filter only allows those two paths through).

## 3. Pages

| File | Purpose |
|---|---|
| `login.html` | Sign in, stores the JWT token in `localStorage` |
| `register.html` | Create a new user |
| `dashboard.html` | Add department + employees, send Excel report by email, view department list, logout |
| `index.html` | Redirects to dashboard or login depending on whether a token is saved |

## 4. How auth is wired up

- `js/api.js` has an `apiCall()` helper used by every page. It automatically adds the
  `Authorization` header with the raw token (no `Bearer ` prefix), matching your current
  `AuthFilter`.
- Token + username are stored in `localStorage` after login, cleared on logout.
- Any API response with `401` automatically clears the session and redirects to `login.html`.

## 5. If you later switch `AuthFilter` to expect `Authorization: Bearer <token>`

Change one line in `js/api.js`:
```js
headers["Authorization"] = token;
```
to:
```js
headers["Authorization"] = "Bearer " + token;
```

## 6. Notes / things you may want to adjust

- `sendReport` expects a plain success message back; if your controller's return type
  changes to `ApiResponse<...>` or a JSON error, the UI already handles both since
  `apiCall()` falls back gracefully when the response isn't valid JSON.
- The department list groups multiple employees under one department row visually
  (department cells only print on the first employee row) — purely a display choice,
  change `renderDepartments()` in `js/dashboard.js` if you'd prefer one row per department
  with employees repeated, or a nested sub-table.
