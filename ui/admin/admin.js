// ── Page protection — redirect if not admin ──────────────────
guardAdmin();

var editType = "";
var editId = null;
var editLabel = "";

// ── Admin service URL ────────────────────────────────────────
function apiUrl() {
  var input = document.getElementById("apiUrl");

  if (input && input.value.trim()) {
    return input.value.trim();
  }

  return ADMIN_SERVICE;
}

function adminUserId() {
  return localStorage.getItem("adminUserId") || "";
}

function showMessage(text) {
  var box = document.getElementById("message");

  if (box) {
    box.innerText = text;
  }
}

function showToast(message, type) {
  var toast = document.getElementById("toast");
  var toastBox = document.getElementById("toastBox");

  toastBox.className = "toast-box " + (type || "success");
  toastBox.innerText = message;

  toast.classList.remove("hidden");

  setTimeout(function () {
    toast.classList.add("hidden");
  }, 1800);
}

function setButtonLoading(buttonId, isLoading) {
  var button = document.getElementById(buttonId);

  if (!button) return;

  var text = button.querySelector(".btn-text");
  var spinner = button.querySelector(".spinner");

  if (isLoading) {
    button.disabled = true;
    button.classList.add("loading");

    if (text) text.classList.add("hidden");
    if (spinner) spinner.classList.remove("hidden");
  } else {
    button.disabled = false;
    button.classList.remove("loading");

    if (text) text.classList.remove("hidden");
    if (spinner) spinner.classList.add("hidden");
  }
}

function showToast(message, type) {
  var toast = document.getElementById("toast");
  var toastBox = document.getElementById("toastBox");

  toastBox.className = "toast-box " + (type || "success");
  toastBox.innerText = message;

  toast.classList.remove("hidden");

  setTimeout(function () {
    toast.classList.add("hidden");
  }, 1800);
}

function toggleMenu(id) {
  document.getElementById(id).classList.toggle("hidden");
}

function showPage(id) {
  document.querySelectorAll(".page").forEach(function (p) {
    p.classList.add("hidden");
  });

  document.getElementById(id).classList.remove("hidden");
  showMessage("");

  if (id === "categoryPage") loadCategories();
  if (id === "productPage") loadProducts();
}

function isEmpty(value) {
  return value === null || value === undefined || String(value).trim() === "";
}

function setButtonLoading(buttonId, isLoading) {
  var button = document.getElementById(buttonId);

  if (!button) return;

  var text = button.querySelector(".btn-text");
  var spinner = button.querySelector(".spinner");

  if (isLoading) {
    button.disabled = true;
    button.classList.add("loading");
    if (spinner) spinner.classList.remove("hidden");
    if (text) text.classList.add("hidden");
  } else {
    button.disabled = false;
    button.classList.remove("loading");
    if (spinner) spinner.classList.add("hidden");
    if (text) text.classList.remove("hidden");
  }
}

// ── API helpers ──────────────────────────────────────────────
// getHeaders() from utils.js adds Authorization and X-User-Name.

async function apiGet(path) {
  var response = await fetch(apiUrl() + path, {
    method: "GET",
    headers: getHeaders()
  });

  if (handleUnauthorized(response.status)) return null;

  var text = await response.text();

  if (!response.ok) {
    throw new Error(text || "Request failed");
  }

  if (!text) return null;

  return JSON.parse(text);
}

async function apiSend(path, method, data) {
  var response = await fetch(apiUrl() + path, {
    method: method,
    headers: getHeaders(),
    body: JSON.stringify(data)
  });

  if (handleUnauthorized(response.status)) return null;

  var text = await response.text();

  if (!response.ok) {
    throw new Error(text || "Request failed");
  }

  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
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
   API paths unchanged:
   GET    /admin/categories
   POST   /admin/categories
   PUT    /admin/categories
   DELETE /admin/categories
   =================================================== */

var categoryData = [];

document.getElementById("categoryForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  var name = document.getElementById("categoryName").value.trim();
  var slug = document.getElementById("categorySlug").value.trim();
  var parentId = document.getElementById("categoryParentId").value;

  if (isEmpty(name)) {
    showToast("Category name is required.", "error");
    return;
  }

  if (isEmpty(slug)) {
    showToast("Category slug is required.", "error");
    return;
  }

  var data = {
    name: name,
    slug: slug,
    parentId: parentId ? Number(parentId) : null
  };

  try {
    await apiSend("/admin/categories", "POST", data);
    this.reset();
    loadCategories();
    showToast(name + " category created successfully.", "success");
  } catch (error) {
    showToast(error.message || name + " category was not created.", "error");
  }
});

async function loadCategories() {
  setButtonLoading("categoryLoadBtn", true);

  try {
    var data = await apiGet("/admin/categories");

    categoryData = (data || []).filter(function (c) {
      return c.isActive === true || c.isActive === undefined;
    });

    showCategories(categoryData);
  } catch (error) {
    showToast(error.message, "error");
  }

  setButtonLoading("categoryLoadBtn", false);
}

function searchCategory() {
  var id = document.getElementById("categorySearchId").value;

  if (!id) {
    showToast("Enter a category ID.", "error");
    return;
  }

  var found = categoryData.filter(function (c) {
    return String(c.categoryId) === String(id);
  });

  if (found.length > 0) {
    showCategories(found);
  } else {
    showCategories([]);
    showToast(id + " id not found.", "error");
  }
}

function showCategories(data) {
  var box = document.getElementById("categoryList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active categories found.</p>";
    return;
  }

  var table = `
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Parent ID</th>
          <th>Category Name</th>
          <th>Slug</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
  `;

  data.forEach(function (c) {
    table += `
      <tr>
        <td>${c.categoryId || "-"}</td>
        <td>${c.parentId || "-"}</td>
        <td>${c.name || "-"}</td>
        <td>${c.slug || "-"}</td>
        <td>
          <div class="action-buttons">
            <button class="update-btn" onclick='openCategoryPopup(${JSON.stringify(c)})'>
              Update
            </button>
            <button class="delete-btn" onclick="deleteCategory(${c.categoryId})">
              Delete
            </button>
          </div>
        </td>
      </tr>
    `;
  });

  table += `
      </tbody>
    </table>
  `;

  box.innerHTML = table;
}

function hasChildCategory(categoryId) {
  return categoryData.some(function (c) {
    return Number(c.parentId) === Number(categoryId);
  });
}

function openCategoryPopup(c) {
  editType = "category";
  editId = c.categoryId;
  editLabel = c.name || "Category";

  document.getElementById("popupTitle").innerText = "Update Category";

  document.getElementById("popupFields").innerHTML = `
    <label>Parent Category ID</label>
    <input id="editCategoryParentId" type="number" value="${c.parentId || ""}" />

    <label>Category Name</label>
    <input id="editCategoryName" value="${c.name || ""}" />

    <label>Slug</label>
    <input id="editCategorySlug" value="${c.slug || ""}" />
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteCategory(id) {
  if (!checkAuth()) return;

  var category = categoryData.find(function (c) {
    return Number(c.categoryId) === Number(id);
  });

  if (!category) {
    showToast(id + " id not found.", "error");
    return;
  }

  if (hasChildCategory(id)) {
    showToast(category.name + " category has sub categories. It cannot be deleted.", "error");
    return;
  }

  if (!confirm("Delete " + category.name + " category?")) return;

  try {
    await apiSend("/admin/categories", "DELETE", { categoryId: id });
    await loadCategories();
    showToast(category.name + " category deleted successfully.", "success");
  } catch (error) {
    showToast(error.message || category.name + " category was not deleted.", "error");
  }
}

/* ===================================================
   PRODUCTS
   API paths unchanged:
   GET    /admin/products
   POST   /admin/products
   PUT    /admin/products
   DELETE /admin/products
   =================================================== */

var productData = [];

document.getElementById("productForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  var name = document.getElementById("productName").value.trim();
  var categoryId = document.getElementById("productCategoryId").value;

  if (isEmpty(name)) {
    showToast("Product name is required.", "error");
    return;
  }

  if (isEmpty(categoryId)) {
    showToast("Category ID is required.", "error");
    return;
  }

  var data = {
    categoryId: Number(categoryId),
    brandName: document.getElementById("productBrandName").value.trim(),
    name: name,
    description: document.getElementById("productDescription").value,
    mainImageKey: document.getElementById("productImage").value,
    isActive: document.getElementById("productIsActive").value === "true"
  };

  try {
    await apiSend("/admin/products", "POST", data);
    this.reset();
    loadProducts();
    showToast(name + " product created successfully.", "success");
  } catch (error) {
    showToast(error.message || name + " product was not created.", "error");
  }
});

async function loadProducts() {
  setButtonLoading("productLoadBtn", true);

  try {
    var data = await apiGet("/admin/products");

    productData = (data || []).filter(function (p) {
      return p.isActive === true || p.isActive === undefined;
    });

    showProducts(productData);
  } catch (error) {
    showToast(error.message, "error");
  }

  setButtonLoading("productLoadBtn", false);
}

function searchProduct() {
  var id = document.getElementById("productSearchId").value;

  if (!id) {
    showToast("Enter a product ID.", "error");
    return;
  }

  var found = productData.filter(function (p) {
    return String(p.productId) === String(id);
  });

  if (found.length > 0) {
    showProducts(found);
  } else {
    showProducts([]);
    showToast(id + " id not found.", "error");
  }
}

function showProducts(data) {
  var box = document.getElementById("productList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active products found.</p>";
    return;
  }

  var table = `
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Category ID</th>
          <th>Product Name</th>
          <th>Brand</th>
          <th>Image Key</th>
          <th>Active</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
  `;

  data.forEach(function (p) {
    table += `
      <tr>
        <td>${p.productId || "-"}</td>
        <td>${p.categoryId || "-"}</td>
        <td>${p.name || "-"}</td>
        <td>${p.brandName || "-"}</td>
        <td>${p.mainImageKey || "-"}</td>
        <td>${p.isActive === true ? "Yes" : "No"}</td>
        <td>
          <div class="action-buttons">
            <button class="update-btn" onclick='openProductPopup(${JSON.stringify(p)})'>
              Update
            </button>
            <button class="delete-btn" onclick="deleteProduct(${p.productId})">
              Delete
            </button>
          </div>
        </td>
      </tr>
    `;
  });

  table += `
      </tbody>
    </table>
  `;

  box.innerHTML = table;
}

function openProductPopup(p) {
  editType = "product";
  editId = p.productId;
  editLabel = p.name || "Product";

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
      <option value="true" ${p.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!p.isActive ? "selected" : ""}>false</option>
    </select>
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteProduct(id) {
  if (!checkAuth()) return;

  var product = productData.find(function (p) {
    return Number(p.productId) === Number(id);
  });

  if (!product) {
    showToast(id + " id not found.", "error");
    return;
  }

  if (!confirm("Delete " + product.name + " product?")) return;

  try {
    await apiSend("/admin/products", "DELETE", { productId: id });
    await loadProducts();
    showToast(product.name + " product deleted successfully.", "success");
  } catch (error) {
    showToast(error.message || product.name + " product was not deleted.", "error");
  }
}

/* ===================================================
   VARIANTS
   API paths unchanged:
   GET    /admin/variants
   POST   /admin/variants
   PUT    /admin/variants
   DELETE /admin/variants
   =================================================== */

var variantData = [];

document.getElementById("variantForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  var productId = document.getElementById("variantProductId").value;
  var sku = document.getElementById("variantSku").value.trim();
  var price = document.getElementById("variantPrice").value;

  if (isEmpty(productId)) {
    showToast("Product ID is required.", "error");
    return;
  }

  if (isEmpty(sku)) {
    showToast("SKU is required.", "error");
    return;
  }

  if (isEmpty(price) || Number(price) <= 0) {
    showToast("Price is required and must be greater than 0.", "error");
    return;
  }

  var data = {
    productId: Number(productId),
    sku: sku,
    color: document.getElementById("variantColor").value,
    size: document.getElementById("variantSize").value,
    price: Number(price),
    isActive: document.getElementById("variantIsActive").value === "true"
  };

  try {
    await apiSend("/admin/variants", "POST", data);
    this.reset();
    showToast(sku + " variant created successfully.", "success");
  } catch (error) {
    showToast(error.message || sku + " variant was not created.", "error");
  }
});

async function searchVariantsByProduct() {
  var productId = document.getElementById("variantSearchProductId").value;

  if (!productId) {
    showToast("Enter a product ID.", "error");
    return;
  }

  setButtonLoading("variantLoadBtn", true);

  try {
    var data = await apiGet("/admin/variants");

    variantData = data || [];

    var filtered = variantData.filter(function (v) {
      var active = v.isActive === true || v.isActive === undefined;
      return active && String(v.productId) === String(productId);
    });

    showVariants(filtered);
  } catch (error) {
    showToast(error.message, "error");
  }

  setButtonLoading("variantLoadBtn", false);
}

function showVariants(data) {
  var box = document.getElementById("variantList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active variants found for this product.</p>";
    return;
  }

  var table = `
    <table class="data-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Product ID</th>
          <th>SKU</th>
          <th>Color</th>
          <th>Size</th>
          <th>Price</th>
          <th>Active</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
  `;

  data.forEach(function (v) {
    table += `
      <tr>
        <td>${v.variantId || "-"}</td>
        <td>${v.productId || "-"}</td>
        <td>${v.sku || "-"}</td>
        <td>${v.color || "-"}</td>
        <td>${v.size || "-"}</td>
        <td>${v.price || "-"}</td>
        <td>${v.isActive === true ? "Yes" : "No"}</td>
        <td>
          <div class="action-buttons">
            <button class="update-btn" onclick='openVariantPopup(${JSON.stringify(v)})'>
              Update
            </button>
            <button class="delete-btn" onclick="deleteVariant(${v.variantId})">
              Delete
            </button>
          </div>
        </td>
      </tr>
    `;
  });

  table += `
      </tbody>
    </table>
  `;

  box.innerHTML = table;
}

function openVariantPopup(v) {
  editType = "variant";
  editId = v.variantId;
  editLabel = v.sku || "Variant";

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
      <option value="true" ${v.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!v.isActive ? "selected" : ""}>false</option>
    </select>
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteVariant(id) {
  if (!checkAuth()) return;

  var variant = variantData.find(function (v) {
    return Number(v.variantId) === Number(id);
  });

  if (!variant) {
    showToast(id + " id not found.", "error");
    return;
  }

  if (!confirm("Delete " + variant.sku + " variant?")) return;

  try {
    await apiSend("/admin/variants", "DELETE", { variantId: id });
    showToast(variant.sku + " variant deleted successfully.", "success");
  } catch (error) {
    showToast(error.message || variant.sku + " variant was not deleted.", "error");
  }
}

/* ===================================================
   POPUP UPDATE
   API paths unchanged:
   PUT /admin/categories
   PUT /admin/products
   PUT /admin/variants
   =================================================== */

function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

document.getElementById("popupForm").addEventListener("submit", async function (e) {
document.getElementById("popupForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  setButtonLoading("saveUpdateBtn", true);

  try {
    if (editType === "category") {
      var parentId = document.getElementById("editCategoryParentId").value;
      var name = document.getElementById("editCategoryName").value.trim();

      var data = {
        categoryId: editId,
        name: name,
        slug: document.getElementById("editCategorySlug").value.trim(),
        parentId: parentId ? Number(parentId) : null
      };

      await apiSend("/admin/categories", "PUT", data);
      await loadCategories();

      closePopup();
      showToast(name + " category updated successfully.", "success");
    }

    if (editType === "product") {
      var productName = document.getElementById("editProductName").value.trim();

      var productDataToUpdate = {
        productId: editId,
        name: productName,
        description: document.getElementById("editProductDescription").value,
        mainImageKey: document.getElementById("editProductImage").value,
        isActive: document.getElementById("editProductIsActive").value === "true"
      };

      await apiSend("/admin/products", "PUT", productDataToUpdate);
      await loadProducts();

      closePopup();
      showToast(productName + " product updated successfully.", "success");
    }

    if (editType === "variant") {
      var price = document.getElementById("editVariantPrice").value;
      var sku = document.getElementById("editVariantSku").value.trim();

      if (isEmpty(price) || Number(price) <= 0) {
        showToast("Price is required and must be greater than 0.", "error");
        setButtonLoading("saveUpdateBtn", false);
        return;
      }

      var variantDataToUpdate = {
        variantId: editId,
        sku: sku,
        color: document.getElementById("editVariantColor").value,
        size: document.getElementById("editVariantSize").value,
        price: Number(price),
        isActive: document.getElementById("editVariantIsActive").value === "true"
      };

      await apiSend("/admin/variants", "PUT", variantDataToUpdate);

      closePopup();
      showToast(sku + " variant updated successfully.", "success");
    }

  } catch (error) {
    showToast(error.message || editLabel + " update failed.", "error");
  }

  setButtonLoading("saveUpdateBtn", false);
});
});
