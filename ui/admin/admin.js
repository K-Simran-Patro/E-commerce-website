// ── Page protection — redirect if not admin ──────────────────
guardAdmin();

var editType = "";
var editId   = null;

// ── Admin service URL ────────────────────────────────────────
function apiUrl() {
  var input = document.getElementById("apiUrl");
  if (input && input.value.trim()) return input.value.trim();
  return ADMIN_SERVICE;
}

// ── Save URL to localStorage ─────────────────────────────────
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
// getHeaders() from utils.js adds:
//   Authorization: Bearer token
//   X-User-Name: email  ← admin service reads this for audit log

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

function checkAuth() {
  if (!localStorage.getItem("authToken")) {
    showMessage("Token missing. Please login again.");
    window.location.href = "/auth/login.html";
    return false;
  }
  return true;
}


/* ===================================================
   CATEGORIES
   Fields accepted by backend:
   categoryId, name, slug, parentId

   NOTE: No path variable in admin service.
   Update and Delete send ID in request body.
   Search by ID filters from loaded data.
   =================================================== */

var categoryData = [];

document.getElementById("categoryForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var name     = document.getElementById("categoryName").value.trim();
  var slug     = document.getElementById("categorySlug").value.trim();
  var parentId = document.getElementById("categoryParentId").value;

  if (isEmpty(name)) { showMessage("Category name is required."); return; }
  if (isEmpty(slug)) { showMessage("Category slug is required."); return; }

  // Only send fields that backend DTO accepts
  var data = {
    name     : name,
    slug     : slug,
    parentId : parentId ? Number(parentId) : null
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
    categoryData = data || [];
    showCategories(categoryData);
  } catch (error) { showMessage(error.message); }
}

// Search filters from already loaded data — no separate API call
// (admin service has no GET /admin/categories/{id})
function searchCategory() {
  var id = document.getElementById("categorySearchId").value;
  if (!id) { showMessage("Enter a category ID."); return; }

  var found = categoryData.filter(function (c) {
    return String(c.categoryId) === String(id);
  });

  if (found.length > 0) {
    showCategories(found);
  } else {
    showCategories([]);
    showMessage("Category not found. Click View All to reload.");
  }
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
  editType = "category";
  editId   = c.categoryId;
  document.getElementById("popupTitle").innerText = "Update Category";
  document.getElementById("popupFields").innerHTML = `
    <label>Parent Category ID</label>
    <input id="editCategoryParentId" type="number" value="${c.parentId || ""}" />
    <label>Category Name</label>
    <input id="editCategoryName" value="${c.name || ""}" />
    <label>Slug</label>
    <input id="editCategorySlug" value="${c.slug || ""}" />`;
  document.getElementById("popup").classList.remove("hidden");
}

// FIX: Delete sends ID in request body, not path variable
async function deleteCategory(id) {
  if (!checkAuth()) return;
  if (!confirm("Delete this category?")) return;

  try {
    await apiSend("/admin/categories", "DELETE", { categoryId: id });
    showMessage("Category deleted.");
    loadCategories();
  } catch (error) { showMessage(error.message); }
}


/* ===================================================
   PRODUCTS
   Fields accepted by backend:
   productId, categoryId, brandName, name,
   description, mainImageKey, isActive

   NOTE: status field removed — not in product DTO.
   =================================================== */

var productData = [];

document.getElementById("productForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var name       = document.getElementById("productName").value.trim();
  var categoryId = document.getElementById("productCategoryId").value;

  if (isEmpty(name))       { showMessage("Product name is required."); return; }
  if (isEmpty(categoryId)) { showMessage("Category ID is required."); return; }

  // Only send fields that backend DTO accepts
  // status is NOT included — product DTO doesn't have it
  var data = {
    categoryId   : Number(categoryId),
    brandName    : document.getElementById("productBrandName").value.trim(),
    name         : name,
    description  : document.getElementById("productDescription").value,
    mainImageKey : document.getElementById("productImage").value,
    isActive     : document.getElementById("productIsActive").value === "true"
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
    productData = data || [];
    showProducts(productData);
  } catch (error) { showMessage(error.message); }
}

// Search filters from already loaded data
function searchProduct() {
  var id = document.getElementById("productSearchId").value;
  if (!id) { showMessage("Enter a product ID."); return; }

  var found = productData.filter(function (p) {
    return String(p.productId) === String(id);
  });

  if (found.length > 0) {
    showProducts(found);
  } else {
    showProducts([]);
    showMessage("Product not found. Click View All to reload.");
  }
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
        <p><b>Created By:</b> ${p.createdBy || "-"}</p>
        <div class="item-actions">
          <button class="update-btn" onclick='openProductPopup(${JSON.stringify(p)})'>Update</button>
          <button class="delete-btn" onclick="deleteProduct(${p.productId})">Delete</button>
        </div>
      </div>`;
  });
}

function openProductPopup(p) {
  editType = "product";
  editId   = p.productId;
  document.getElementById("popupTitle").innerText = "Update Product";
  document.getElementById("popupFields").innerHTML = `
    <label>Product Name</label>
    <input id="editProductName" value="${p.name || ""}" />
    <label>Description</label>
    <textarea id="editProductDescription">${p.description || ""}</textarea>
    <label>Main Image Key</label>
    <input id="editProductImage" value="${p.mainImageKey || ""}" />
    <label>Is Active</label>
    <select id="editProductIsActive">
      <option value="true"  ${p.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!p.isActive ? "selected" : ""}>false</option>
    </select>`;
  document.getElementById("popup").classList.remove("hidden");
}

// FIX: Delete sends ID in request body
async function deleteProduct(id) {
  if (!checkAuth()) return;
  if (!confirm("Delete this product?")) return;

  try {
    await apiSend("/admin/products", "DELETE", { productId: id });
    showMessage("Product deleted.");
    loadProducts();
  } catch (error) { showMessage(error.message); }
}


/* ===================================================
   VARIANTS
   Fields accepted by backend:
   variantId, productId, sku, color, size, price, isActive

   NOTE: price is required and must be > 0
   Search: GET /admin/variants then filter by productId
   =================================================== */

document.getElementById("variantForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  var productId = document.getElementById("variantProductId").value;
  var sku       = document.getElementById("variantSku").value.trim();
  var price     = document.getElementById("variantPrice").value;

  if (isEmpty(productId)) { showMessage("Product ID is required."); return; }
  if (isEmpty(sku))       { showMessage("SKU is required."); return; }
  if (isEmpty(price) || Number(price) <= 0) {
    showMessage("Price is required and must be greater than 0.");
    return;
  }

  // Only send fields that backend DTO accepts
  var data = {
    productId : Number(productId),
    sku       : sku,
    color     : document.getElementById("variantColor").value,
    size      : document.getElementById("variantSize").value,
    price     : Number(price),
    isActive  : document.getElementById("variantIsActive").value === "true"
  };

  try {
    await apiSend("/admin/variants", "POST", data);
    showMessage("Variant created successfully.");
    this.reset();
  } catch (error) { showMessage(error.message); }
});

// FIX: Call GET /admin/variants then filter by productId on frontend
// (admin service has no GET /admin/variants/product/{id})
async function searchVariantsByProduct() {
  var productId = document.getElementById("variantSearchProductId").value;
  if (!productId) { showMessage("Enter a product ID."); return; }

  try {
    var data = await apiGet("/admin/variants");

    var filtered = (data || []).filter(function (v) {
      return String(v.productId) === String(productId);
    });

    showVariants(filtered);
  } catch (error) { showMessage(error.message); }
}

function showVariants(data) {
  var box = document.getElementById("variantList");
  box.innerHTML = "";
  if (!data || data.length === 0) { box.innerHTML = "<p>No variants found for this product.</p>"; return; }

  data.forEach(function (v) {
    box.innerHTML += `
      <div class="item">
        <h4>${v.sku}</h4>
        <p><b>ID:</b> ${v.variantId}</p>
        <p><b>Product ID:</b> ${v.productId}</p>
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
  editType = "variant";
  editId   = v.variantId;
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

// FIX: Delete sends ID in request body
async function deleteVariant(id) {
  if (!checkAuth()) return;
  if (!confirm("Delete this variant?")) return;

  try {
    await apiSend("/admin/variants", "DELETE", { variantId: id });
    showMessage("Variant deleted.");
  } catch (error) { showMessage(error.message); }
}


/* ===================================================
   POPUP — Update form
   FIX: All updates go to /admin/xxx (no path variable)
   ID is sent in request body
   =================================================== */

function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

document.getElementById("popupForm").addEventListener("submit", async function (e) {
  e.preventDefault();
  if (!checkAuth()) return;

  try {
    if (editType === "category") {
      var parentId = document.getElementById("editCategoryParentId").value;

      // Send categoryId in body — no path variable
      var data = {
        categoryId : editId,
        name       : document.getElementById("editCategoryName").value.trim(),
        slug       : document.getElementById("editCategorySlug").value.trim(),
        parentId   : parentId ? Number(parentId) : null
      };

      await apiSend("/admin/categories", "PUT", data);
      loadCategories();
    }

    if (editType === "product") {
      var price = document.getElementById("editVariantPrice") ?
                  document.getElementById("editVariantPrice").value : null;

      // Send productId in body — no path variable
      var data = {
        productId    : editId,
        name         : document.getElementById("editProductName").value.trim(),
        description  : document.getElementById("editProductDescription").value,
        mainImageKey : document.getElementById("editProductImage").value,
        isActive     : document.getElementById("editProductIsActive").value === "true"
      };

      await apiSend("/admin/products", "PUT", data);
      loadProducts();
    }

    if (editType === "variant") {
      var price = document.getElementById("editVariantPrice").value;

      if (isEmpty(price) || Number(price) <= 0) {
        showMessage("Price is required and must be greater than 0.");
        return;
      }

      // Send variantId in body — no path variable
      var data = {
        variantId : editId,
        sku       : document.getElementById("editVariantSku").value.trim(),
        color     : document.getElementById("editVariantColor").value,
        size      : document.getElementById("editVariantSize").value,
        price     : Number(price),
        isActive  : document.getElementById("editVariantIsActive").value === "true"
      };

      await apiSend("/admin/variants", "PUT", data);
    }

    closePopup();
    showMessage("Updated successfully.");
  } catch (error) { showMessage(error.message); }
});