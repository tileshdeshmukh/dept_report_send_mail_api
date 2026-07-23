requireAuth();

document.getElementById("welcomeText").textContent = `Signed in as ${getUsername()}`;

// ===== Logout =====
document.getElementById("logoutBtn").addEventListener("click", async () => {
  try {
    await apiCall("/api/user/logout", "POST");
  } catch (e) {
    // even if the call fails, still clear the local session below
  }
  clearSession();
  window.location.href = "login.html";
});

// ===== Dynamic employee rows (Add Department & Employees) =====
const employeeRowsHost = document.getElementById("employeeRows");
let employeeRowCount = 0;

function addEmployeeRow(existingEmployee) {
  employeeRowCount++;
  const rowId = `emp-row-${employeeRowCount}`;

  const name = existingEmployee ? (existingEmployee.emp_name || "") : "";
  const salary = existingEmployee && existingEmployee.salary != null ? existingEmployee.salary : "";
  const shift = existingEmployee ? (existingEmployee.shift || "Day") : "Day";

  const wrapper = document.createElement("div");
  wrapper.className = "employee-row";
  wrapper.id = rowId;
  wrapper.innerHTML = `
    <div class="row g-2 align-items-center">
      <div class="col-md-4">
        <input type="text" class="form-control form-control-sm emp-name" placeholder="Employee name" value="${name}" />
      </div>
      <div class="col-md-3">
        <input type="number" class="form-control form-control-sm emp-salary" placeholder="Salary" min="0" value="${salary}" />
      </div>
      <div class="col-md-3">
        <select class="form-select form-select-sm emp-shift">
          <option value="Day">Day</option>
          <option value="Night">Night</option>
          <option value="Rotational">Rotational</option>
        </select>
      </div>
      <div class="col-md-2 text-end">
        <button type="button" class="btn btn-sm btn-outline-danger remove-emp-btn">Remove</button>
      </div>
    </div>`;

  wrapper.querySelector(".emp-shift").value = shift;
  wrapper.querySelector(".remove-emp-btn").addEventListener("click", () => wrapper.remove());
  employeeRowsHost.appendChild(wrapper);
}

document.getElementById("addEmployeeBtn").addEventListener("click", addEmployeeRow);
addEmployeeRow(); // start with one row so the form isn't empty

// ===== Save Department form (handles both Add and Edit) =====
const deptForm = document.getElementById("deptForm");
const deptAlertHost = document.getElementById("deptAlertHost");
const saveDeptBtn = document.getElementById("saveDeptBtn");
const formTitle = document.getElementById("formTitle");
const cancelEditBtn = document.getElementById("cancelEditBtn");
const editingDeptIdInput = document.getElementById("editingDeptId");

function showDeptAlert(message, variant = "danger") {
  deptAlertHost.innerHTML = `<div class="alert alert-${variant} py-2">${message}</div>`;
}

function resetDeptForm() {
  deptForm.reset();
  editingDeptIdInput.value = "";
  employeeRowsHost.innerHTML = "";
  employeeRowCount = 0;
  addEmployeeRow();
  formTitle.textContent = "Add Department & Employees";
  saveDeptBtn.textContent = "Save department";
  cancelEditBtn.classList.add("d-none");
}

cancelEditBtn.addEventListener("click", resetDeptForm);

// Populates the form with an existing department's data, ready to edit.
async function startEditDepartment(deptId) {
  deptAlertHost.innerHTML = "";
  try {
    const result = await apiCall(`/api/departments/${deptId}`, "GET");
    const dept = (result && result.data) || result;

    editingDeptIdInput.value = dept.id;
    document.getElementById("deptName").value = dept.name || "";

    employeeRowsHost.innerHTML = "";
    employeeRowCount = 0;

    if (dept.employees && dept.employees.length > 0) {
      dept.employees.forEach((emp) => addEmployeeRow(emp));
    } else {
      addEmployeeRow();
    }

    formTitle.textContent = `Editing: ${dept.name}`;
    saveDeptBtn.textContent = "Update department";
    cancelEditBtn.classList.remove("d-none");

    window.scrollTo({ top: 0, behavior: "smooth" });
  } catch (err) {
    showDeptAlert(`Couldn't load department for editing: ${err.message}`);
  }
}

deptForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  deptAlertHost.innerHTML = "";

  const deptName = document.getElementById("deptName").value.trim();
  if (!deptName) {
    showDeptAlert("Please enter a department name.");
    return;
  }

  const employees = [];
  document.querySelectorAll("#employeeRows .employee-row").forEach((row) => {
    const emp_name = row.querySelector(".emp-name").value.trim();
    const salary = row.querySelector(".emp-salary").value;
    const shift = row.querySelector(".emp-shift").value;
    if (emp_name) {
      employees.push({
        emp_name,
        salary: salary ? Number(salary) : 0,
        shift,
      });
    }
  });

  const payload = { name: deptName, employees };
  const editingId = editingDeptIdInput.value;
  const isEditing = !!editingId;

  saveDeptBtn.disabled = true;
  saveDeptBtn.textContent = isEditing ? "Updating..." : "Saving...";

  try {
    if (isEditing) {
      await apiCall(`/api/departments/${editingId}`, "PUT", payload);
      showToast("Department updated successfully");
    } else {
      await apiCall("/api/departments/addDeptEmployee", "POST", payload);
      showToast("Department saved successfully");
    }
    resetDeptForm();
    loadDepartments();
  } catch (err) {
    showDeptAlert(err.message);
  } finally {
    saveDeptBtn.disabled = false;
    saveDeptBtn.textContent = isEditing ? "Update department" : "Save department";
  }
});

// ===== Send Report form =====
const reportForm = document.getElementById("reportForm");
const reportAlertHost = document.getElementById("reportAlertHost");
const sendReportBtn = document.getElementById("sendReportBtn");

function showReportAlert(message, variant = "danger") {
  reportAlertHost.innerHTML = `<div class="alert alert-${variant} py-2">${message}</div>`;
}

reportForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  reportAlertHost.innerHTML = "";

  const email = document.getElementById("reportEmail").value.trim();
  if (!email) {
    showReportAlert("Please enter a recipient email.");
    return;
  }

  sendReportBtn.disabled = true;
  sendReportBtn.textContent = "Sending...";

  try {
    await apiCall(`/api/departments/sendReport?email=${encodeURIComponent(email)}`, "GET");
    showToast(`Report sent to ${email}`);
    reportForm.reset();
  } catch (err) {
    showReportAlert(err.message);
  } finally {
    sendReportBtn.disabled = false;
    sendReportBtn.textContent = "Send report";
  }
});

// ===== Department list table =====
const deptTableBody = document.getElementById("deptTableBody");
const deptEmptyState = document.getElementById("deptEmptyState");

async function loadDepartments() {
  try {
    // Requires a GET /api/departments/all endpoint on the backend — see setup note in README.
    const result = await apiCall("/api/departments/all", "GET");
    const departments = (result && result.data) || result || [];
    renderDepartments(departments);
  } catch (err) {
    deptTableBody.innerHTML = "";
    deptEmptyState.classList.remove("d-none");
    deptEmptyState.textContent =
      "Couldn't load departments. Make sure a GET /api/departments/all endpoint exists on the backend.";
  }
}

function renderDepartments(departments) {
  deptTableBody.innerHTML = "";

  if (!departments || departments.length === 0) {
    deptEmptyState.classList.remove("d-none");
    deptEmptyState.textContent = "No departments yet — add one using the form above.";
    return;
  }

  deptEmptyState.classList.add("d-none");

  departments.forEach((dept) => {
    const employees = dept.employees && dept.employees.length ? dept.employees : [null];
    employees.forEach((emp, index) => {
      const tr = document.createElement("tr");
      const actionsCell =
        index === 0
          ? `<button class="btn btn-sm btn-outline-primary edit-dept-btn" data-id="${dept.id}">Edit</button>
             <button class="btn btn-sm btn-outline-danger delete-dept-btn" data-id="${dept.id}" data-name="${escapeHtml(dept.name)}">Delete</button>`
          : "";

      tr.innerHTML = `
        <td>${index === 0 ? dept.id : ""}</td>
        <td>${index === 0 ? escapeHtml(dept.name) : ""}</td>
        <td>${emp ? escapeHtml(emp.emp_name || "") : "<span class='text-muted'>—</span>"}</td>
        <td>${emp ? (emp.salary ?? "") : ""}</td>
        <td>${emp ? escapeHtml(emp.shift || "") : ""}</td>
        <td class="text-end">${actionsCell}</td>`;
      deptTableBody.appendChild(tr);
    });
  });

  deptTableBody.querySelectorAll(".edit-dept-btn").forEach((btn) => {
    btn.addEventListener("click", () => startEditDepartment(btn.dataset.id));
  });

  deptTableBody.querySelectorAll(".delete-dept-btn").forEach((btn) => {
    btn.addEventListener("click", () => deleteDepartment(btn.dataset.id, btn.dataset.name));
  });
}

async function deleteDepartment(deptId, deptName) {
  const confirmed = confirm(`Delete "${deptName}" and all its employees? This can't be undone.`);
  if (!confirmed) return;

  try {
    await apiCall(`/api/departments/${deptId}`, "DELETE");
    showToast(`Department "${deptName}" deleted`);

    // If the department being deleted was open in the edit form, reset the form
    if (editingDeptIdInput.value === String(deptId)) {
      resetDeptForm();
    }

    loadDepartments();
  } catch (err) {
    showToast(err.message, "danger");
  }
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}

document.getElementById("refreshBtn").addEventListener("click", loadDepartments);

loadDepartments();
