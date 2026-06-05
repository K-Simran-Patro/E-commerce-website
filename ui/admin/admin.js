// ── Page protection — redirect if not ADMIN ──────────────────
guardAdmin();

var editType = "";
var editId   = null;

// ── Admin service URL (reads from sidebar input) ─────────────
function apiUrl() {
  var input = document.getElementById("apiUrl");
  if (input && input.value.trim()) return input.value.trim();
  return ADMIN_SERVICE;
}

// ── Save URL to localStorage (was missing before) ────────────
function saveAuth() {
  var input = document.getElementById("apiUrl");
  if (input && input.value.trim()) {
    localStorage.setItem("adminServiceUrl", input.value.trim());
    showMessage("Backend URL saved.");
  }
}

window.addEventListener("load", function () {
  var savedUrl = localStorage.getItem("adminServiceUrl");
  var input    = document.getElementById("apiUrl");
  if (savedUrl && input) input.value = savedUrl;
});

function adminUserId() { return localStorage.getItem("adminUserId") || ""; }

function showMessage(text) {
  var box = document.getElementById("message");
  if (box) box.innerText = text;
}

function showPage(id) {
  document.querySelectorAll(".page").forEach(function (p) { p.classList.add("hidden"); });
  document.getElementById(id).classList.remove("hidden");
  showMessage("");
  if (id === "categoryPage") loadCategories();
  if (id === "productPage")  loadProducts();
}

function isEmpty(value) {
  return value === null || value === undefined || String(value).trim() === "";
}

// ── API helpers ──────────────────────────────────────────────
// getHeaders() comes from utils.js
// Sends: Authorization: Bearer token + X-User-Name for audit log

async function apiGet(path) {
  var response = await fetch(apiUrl() + path, {
    method  : "GET",
    headers : getHeaders()
  });
  if (handleUnauthorized(response.status)) return null;
  var text = await response.text();
  if (!response.ok) throw new Error(text || "Request failed");
  if (!text) return null;
  return JSON.parse(text);
}

async function apiSend(path, method, data) {
  var response = await fetch(apiUrl() + path, {
    method  : method,
    headers : getHeaders(),
    body    : JSON.stringify(data)
  });
  if (handleUnauthorized(response.status)) return null;
  var text = await response.text();
  if (!response.ok) throw new Error(text || "Request failed");
  if (!text) return null;
  try { return JSON.parse(text); } catch { return text; }
}

async function apiDelete(path) {
  var response = await fetch(apiUrl() + path, {
    method  : "DELETE",
    headers : getHeaders()
  });
  if (handleUnauthorized(response.status)) return null;
  var text = await response.text();
  if (!response.ok) throw new Error(text || "Delete failed");
  return text;
}

function checkAuth() {
  if (!localStorage.getItem("authToken")) {
    showMessage("Token missing. Please login again.");
    window.location.href = "/auth/login.html";
    return false;
  }
  return true;
}

// ── CATEGORIES ───────────────────────────────────────────────
var categoryData = [];

document.getElementById("categoryForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var name     = document.getElementById("categoryName").value.trim();
  var slug     = document.getElementById("categorySlug").value.trim();
  var parentId = document.getElementById("categoryParentId").value;

  if (isEmpty(name) || isEmpty(slug)) {
    showMessage("Category name and slug are required.");
    return;
  }

  var data = {
    parentId   : parentId ? Number(parentId) : null,
    name       : name,
    slug       : slug,
    isActive   : document.getElementById("categoryIsActive").value === "true",
    createdBy  : adminUserId(),
    modifiedBy : adminUserId()
  };

  try {
    await apiSend("/admin/categories", "POST", data);
    showMessage("Category created successfully.");
    this.reset();
    loadCategories();
  } catch (error) { showMessage(error.message); }
});

async function loadCategories() {
  try {
    var data = await apiGet("/admin/categories");
    categoryData = (data || []).filter(function (c) { return c.isActive === true; });
    showCategories(categoryData);
  } catch (error) { showMessage(error.message); }
}

async function searchCategory() {
  var id = document.getElementById("categorySearchId").value;
  if (!id) { showMessage("Enter a category ID."); return; }
  try {
    var category = await apiGet("/admin/categories/" + id);
    if (category && category.isActive) showCategories([category]);
    else { showCategories([]); showMessage("Category not found or inactive."); }
  } catch (error) { showMessage(error.message); }
}

function showCategories(data) {
  var box = document.getElementById("categoryList");
  box.innerHTML = "";
  if (!data || data.length === 0) { box.innerHTML = "<p>No categories found.</p>"; return; }
  data.forEach(function (c) {
    box.innerHTML += `
      <div class="item">
        <h4>${c.name}</h4>
        <p><b>ID:</b> ${c.categoryId}</p>
        <p><b>Parent ID:</b> ${c.parentId || "-"}</p>
        <p><b>Slug:</b> ${c.slug}</p>
        <p><b>Created By:</b> ${c.createdBy || "-"}</p>
        <div class="item-actions">
          <button class="update-btn" onclick='openCategoryPopup(${JSON.stringify(c)})'>Update</button>
          <button class="delete-btn" onclick="deleteCategory(${c.categoryId})">Delete</button>
        </div>
      </div>`;
  });
}

function openCategoryPopup(c) {
  editType = "category"; editId = c.categoryId;
  document.getElementById("popupTitle").innerText = "Update Category";
  document.getElementById("popupFields").innerHTML = `
    <label>Parent Category ID</label>
    <input id="editCategoryParentId" type="number" value="${c.parentId || ""}" />
    <label>Category Name</label>
    <input id="editCategoryName" value="${c.name || ""}" />
    <label>Slug</label>
    <input id="editCategorySlug" value="${c.slug || ""}" />
    <label>Is Active</label>
    <select id="editCategoryIsActive">
      <option value="true" ${c.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!c.isActive ? "selected" : ""}>false</option>
    </select>`;
  document.getElementById("popup").classList.remove("hidden");
}

async function deleteCategory(id) {
  if (!checkAuth()) return;
  var hasChild = categoryData.some(function (c) { return Number(c.parentId) === Number(id); });
  if (hasChild) { showMessage("Cannot delete: has sub-categories."); return; }
  if (!confirm("Delete this category?")) return;
  try {
    await apiDelete("/admin/categories/" + id);
    showMessage("Category deleted.");
    loadCategories();
  } catch (error) { showMessage(error.message); }
}

// ── PRODUCTS ─────────────────────────────────────────────────
var productData = [];

document.getElementById("productForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var name       = document.getElementById("productName").value.trim();
  var categoryId = document.getElementById("productCategoryId").value;

  if (isEmpty(name) || !categoryId) {
    showMessage("Product name and category ID are required.");
    return;
  }

  var data = {
    categoryId   : Number(categoryId),
    brandName    : document.getElementById("productBrandName").value.trim(),
    name         : name,
    description  : document.getElementById("productDescription").value,
    mainImageKey : document.getElementById("productImage").value,
    status       : document.getElementById("productStatus").value,
    isActive     : document.getElementById("productIsActive").value === "true",
    createdBy    : adminUserId(),
    modifiedBy   : adminUserId()
  };

  try {
    await apiSend("/admin/products", "POST", data);
    showMessage("Product created successfully.");
    this.reset();
    loadProducts();
  } catch (error) { showMessage(error.message); }
});

async function loadProducts() {
  try {
    var data = await apiGet("/admin/products");
    productData = (data || []).filter(function (p) { return p.isActive === true; });
    showProducts(productData);
  } catch (error) { showMessage(error.message); }
}

async function searchProduct() {
  var id = document.getElementById("productSearchId").value;
  if (!id) { showMessage("Enter a product ID."); return; }
  try {
    var product = await apiGet("/admin/products/" + id);
    if (product && product.isActive) showProducts([product]);
    else { showProducts([]); showMessage("Product not found or inactive."); }
  } catch (error) { showMessage(error.message); }
}

function showProducts(data) {
  var box = document.getElementById("productList");
  box.innerHTML = "";
  if (!data || data.length === 0) { box.innerHTML = "<p>No products found.</p>"; return; }
  data.forEach(function (p) {
    box.innerHTML += `
      <div class="item">
        <h4>${p.name}</h4>
        <p><b>ID:</b> ${p.productId}</p>
        <p><b>Category ID:</b> ${p.categoryId}</p>
        <p><b>Brand:</b> ${p.brandName || "-"}</p>
        <p><b>Status:</b> ${p.status}</p>
        <p><b>Created By:</b> ${p.createdBy || "-"}</p>
        <div class="item-actions">
          <button class="update-btn" onclick='openProductPopup(${JSON.stringify(p)})'>Update</button>
          <button class="delete-btn" onclick="deleteProduct(${p.productId})">Delete</button>
        </div>
      </div>`;
  });
}

function openProductPopup(p) {
  editType = "product"; editId = p.productId;
  document.getElementById("popupTitle").innerText = "Update Product";
  document.getElementById("popupFields").innerHTML = `
    <label>Product Name</label>
    <input id="editProductName" value="${p.name || ""}" />
    <label>Description</label>
    <textarea id="editProductDescription">${p.description || ""}</textarea>
    <label>Main Image Key</label>
    <input id="editProductImage" value="${p.mainImageKey || ""}" />
    <label>Status</label>
    <select id="editProductStatus">
      <option value="active"   ${p.status === "active"   ? "selected" : ""}>active</option>
      <option value="inactive" ${p.status === "inactive" ? "selected" : ""}>inactive</option>
      <option value="draft"    ${p.status === "draft"    ? "selected" : ""}>draft</option>
    </select>
    <label>Is Active</label>
    <select id="editProductIsActive">
      <option value="true"  ${p.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!p.isActive ? "selected" : ""}>false</option>
    </select>`;
  document.getElementById("popup").classList.remove("hidden");
}

async function deleteProduct(id) {
  if (!checkAuth()) return;
  if (!confirm("Delete this product?")) return;
  try {
    await apiDelete("/admin/products/" + id);
    showMessage("Product deleted.");
    loadProducts();
  } catch (error) { showMessage(error.message); }
}

// ── VARIANTS ─────────────────────────────────────────────────
document.getElementById("variantForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var productId = document.getElementById("variantProductId").value;
  var sku       = document.getElementById("variantSku").value.trim();

  if (!productId || isEmpty(sku)) {
    showMessage("Product ID and SKU are required.");
    return;
  }

  var price = document.getElementById("variantPrice").value;

  var data = {
    productId  : Number(productId),
    sku        : sku,
    color      : document.getElementById("variantColor").value,
    size       : document.getElementById("variantSize").value,
    price      : price ? Number(price) : null,
    isActive   : document.getElementById("variantIsActive").value === "true",
    createdBy  : adminUserId(),
    modifiedBy : adminUserId()
  };

  try {
    await apiSend("/admin/variants", "POST", data);
    showMessage("Variant created successfully.");
    this.reset();
  } catch (error) { showMessage(error.message); }
});

// FIX: now correctly calls /admin/variants/product/{id}
async function searchVariantsByProduct() {
  var productId = document.getElementById("variantSearchProductId").value;
  if (!productId) { showMessage("Enter a product ID."); return; }
  try {
    var data = await apiGet("/admin/variants/product/" + productId);
    var active = (data || []).filter(function (v) { return v.isActive === true; });
    showVariants(active);
  } catch (error) { showMessage(error.message); }
}

function showVariants(data) {
  var box = document.getElementById("variantList");
  box.innerHTML = "";
  if (!data || data.length === 0) { box.innerHTML = "<p>No variants found.</p>"; return; }
  data.forEach(function (v) {
    box.innerHTML += `
      <div class="item">
        <h4>${v.sku}</h4>
        <p><b>ID:</b> ${v.variantId}</p>
        <p><b>Color:</b> ${v.color || "-"}</p>
        <p><b>Size:</b> ${v.size || "-"}</p>
        <p><b>Price:</b> ${v.price || "-"}</p>
        <p><b>Created By:</b> ${v.createdBy || "-"}</p>
        <div class="item-actions">
          <button class="update-btn" onclick='openVariantPopup(${JSON.stringify(v)})'>Update</button>
          <button class="delete-btn" onclick="deleteVariant(${v.variantId})">Delete</button>
        </div>
      </div>`;
  });
}

function openVariantPopup(v) {
  editType = "variant"; editId = v.variantId;
  document.getElementById("popupTitle").innerText = "Update Variant";
  document.getElementById("popupFields").innerHTML = `
    <label>SKU</label>
    <input id="editVariantSku" value="${v.sku || ""}" />
    <label>Color</label>
    <input id="editVariantColor" value="${v.color || ""}" />
    <label>Size</label>
    <input id="editVariantSize" value="${v.size || ""}" />
    <label>Price</label>
    <input id="editVariantPrice" type="number" step="0.01" value="${v.price || ""}" />
    <label>Is Active</label>
    <select id="editVariantIsActive">
      <option value="true"  ${v.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!v.isActive ? "selected" : ""}>false</option>
    </select>`;
  document.getElementById("popup").classList.remove("hidden");
}

async function deleteVariant(id) {
  if (!checkAuth()) return;
  if (!confirm("Delete this variant?")) return;
  try {
    await apiDelete("/admin/variants/" + id);
    showMessage("Variant deleted.");
  } catch (error) { showMessage(error.message); }
}

// ── POPUP ────────────────────────────────────────────────────
function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

document.getElementById("popupForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  try {
    if (editType === "category") {
      var parentId = document.getElementById("editCategoryParentId").value;
      var data = {
        parentId   : parentId ? Number(parentId) : null,
        name       : document.getElementById("editCategoryName").value.trim(),
        slug       : document.getElementById("editCategorySlug").value.trim(),
        isActive   : document.getElementById("editCategoryIsActive").value === "true",
        modifiedBy : adminUserId()
      };
      await apiSend("/admin/categories/" + editId, "PUT", data);
      loadCategories();
    }

    if (editType === "product") {
      var data = {
        name         : document.getElementById("editProductName").value.trim(),
        description  : document.getElementById("editProductDescription").value,
        mainImageKey : document.getElementById("editProductImage").value,
        status       : document.getElementById("editProductStatus").value,
        isActive     : document.getElementById("editProductIsActive").value === "true",
        modifiedBy   : adminUserId()
      };
      await apiSend("/admin/products/" + editId, "PUT", data);
      loadProducts();
    }

    if (editType === "variant") {
      var price = document.getElementById("editVariantPrice").value;
      var data = {
        sku        : document.getElementById("editVariantSku").value.trim(),
        color      : document.getElementById("editVariantColor").value,
        size       : document.getElementById("editVariantSize").value,
        price      : price ? Number(price) : null,
        isActive   : document.getElementById("editVariantIsActive").value === "true",
        modifiedBy : adminUserId()
      };
      await apiSend("/admin/variants/" + editId, "PUT", data);
    }

    closePopup();
    showMessage("Updated successfully.");
  } catch (error) { showMessage(error.message); }
});