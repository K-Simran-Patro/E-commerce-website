/* ================= ADMIN PAGE PROTECTION ================= */

// Only ADMIN users should open admin.html.
// Login page should store userRole in localStorage after successful login.
if (localStorage.getItem("userRole") !== "ADMIN") {
  window.location.href = "login.html";
}

/* ================= BASIC SETTINGS ================= */

let editType = "";
let editId = null;

const DEFAULT_ADMIN_SERVICE_URL = "https://e-commerce-website-admin-service.onrender.com";

function apiUrl() {
  const input = document.getElementById("apiUrl");

  if (input && input.value.trim()) {
    return input.value.trim();
  }

  return DEFAULT_ADMIN_SERVICE_URL;
}

function adminUserId() {
  return localStorage.getItem("adminUserId") || "";
}

function authToken() {
  return localStorage.getItem("authToken") || "";
}

function showMessage(text) {
  const messageBox = document.getElementById("message");

  if (messageBox) {
    messageBox.innerText = text;
  }
}

function showPage(id) {
  document.querySelectorAll(".page").forEach(function (page) {
    page.classList.add("hidden");
  });

  document.getElementById(id).classList.remove("hidden");
  showMessage("");

  if (id === "categoryPage") {
    loadCategories();
  }

  if (id === "productPage") {
    loadProducts();
  }
}

function logout() {
  localStorage.clear();
  window.location.href = "login.html";
}

/* ================= API HELPERS ================= */

function getHeaders() {
  const token = authToken();

  const headers = {
    "Content-Type": "application/json"
  };

  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  return headers;
}

async function apiGet(path) {
  const response = await fetch(apiUrl() + path, {
    method: "GET",
    headers: getHeaders()
  });

  const responseText = await response.text();

  if (!response.ok) {
    throw new Error(responseText || "Request failed");
  }

  if (!responseText) {
    return null;
  }

  return JSON.parse(responseText);
}

async function apiSend(path, method, data) {
  const response = await fetch(apiUrl() + path, {
    method: method,
    headers: getHeaders(),
    body: JSON.stringify(data)
  });

  const responseText = await response.text();

  if (!response.ok) {
    throw new Error(responseText || "Request failed");
  }

  if (!responseText) {
    return null;
  }

  try {
    return JSON.parse(responseText);
  } catch {
    return responseText;
  }
}

async function apiDelete(path) {
  const response = await fetch(apiUrl() + path, {
    method: "DELETE",
    headers: getHeaders()
  });

  const responseText = await response.text();

  if (!response.ok) {
    throw new Error(responseText || "Delete failed");
  }

  return responseText;
}

/* ================= VALIDATION ================= */

function checkAuth() {
  if (!authToken()) {
    showMessage("Please login again. Auth token is missing.");
    window.location.href = "login.html";
    return false;
  }

  if (!adminUserId()) {
    showMessage("User ID missing. Please login again.");
    window.location.href = "login.html";
    return false;
  }

  return true;
}

function isEmpty(value) {
  return value === null || value === undefined || String(value).trim() === "";
}

/* ================= CATEGORIES ================= */

let categoryData = [];

document.getElementById("categoryForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  const name = document.getElementById("categoryName").value.trim();
  const slug = document.getElementById("categorySlug").value.trim();
  const parentId = document.getElementById("categoryParentId").value;

  if (isEmpty(name) || isEmpty(slug)) {
    showMessage("Category name and slug are required.");
    return;
  }

  const data = {
    parentId: parentId ? Number(parentId) : null,
    name: name,
    slug: slug,
    isActive: document.getElementById("categoryIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    await apiSend("/admin/categories", "POST", data);
    showMessage("Category created successfully.");
    this.reset();
    loadCategories();
  } catch (error) {
    showMessage(error.message);
  }
});

async function loadCategories() {
  try {
    const data = await apiGet("/admin/categories");

    categoryData = (data || []).filter(function (category) {
      return category.isActive === true;
    });

    showCategories(categoryData);
  } catch (error) {
    showMessage(error.message);
  }
}

async function searchCategory() {
  const id = document.getElementById("categorySearchId").value;

  if (!id) {
    showMessage("Enter category ID to search.");
    return;
  }

  try {
    const category = await apiGet("/admin/categories/" + id);

    if (category && category.isActive === true) {
      showCategories([category]);
    } else {
      showCategories([]);
      showMessage("Category is inactive or not found.");
    }
  } catch (error) {
    showMessage(error.message);
  }
}

function showCategories(data) {
  const box = document.getElementById("categoryList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active categories found.</p>";
    return;
  }

  data.forEach(function (category) {
    box.innerHTML += `
      <div class="item">
        <h4>${category.name || "-"}</h4>
        <p><b>ID:</b> ${category.categoryId}</p>
        <p><b>Parent ID:</b> ${category.parentId || "-"}</p>
        <p><b>Slug:</b> ${category.slug || "-"}</p>
        <p><b>Created By:</b> ${category.createdBy || "-"}</p>
        <p><b>Modified By:</b> ${category.modifiedBy || "-"}</p>

        <div class="item-actions">
          <button class="update-btn" onclick='openCategoryPopup(${JSON.stringify(category)})'>
            Update
          </button>

          <button class="delete-btn" onclick="deleteCategory(${category.categoryId})">
            Delete
          </button>
        </div>
      </div>
    `;
  });
}

function hasChildCategory(categoryId) {
  return categoryData.some(function (category) {
    return Number(category.parentId) === Number(categoryId);
  });
}

function openCategoryPopup(category) {
  editType = "category";
  editId = category.categoryId;

  document.getElementById("popupTitle").innerText = "Update Category";

  document.getElementById("popupFields").innerHTML = `
    <label>Parent Category ID</label>
    <input id="editCategoryParentId" type="number" value="${category.parentId || ""}" />

    <label>Category Name</label>
    <input id="editCategoryName" value="${category.name || ""}" />

    <label>Slug</label>
    <input id="editCategorySlug" value="${category.slug || ""}" />

    <label>Is Active</label>
    <select id="editCategoryIsActive">
      <option value="true" ${category.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!category.isActive ? "selected" : ""}>false</option>
    </select>
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteCategory(id) {
  if (!checkAuth()) return;

  if (hasChildCategory(id)) {
    showMessage("Sub categories present in it. You cannot delete this category.");
    return;
  }

  if (!confirm("Delete this category?")) return;

  try {
    await apiDelete("/admin/categories/" + id);
    showMessage("Category deleted successfully.");
    loadCategories();
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= PRODUCTS ================= */

let productData = [];

document.getElementById("productForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  const name = document.getElementById("productName").value.trim();
  const categoryId = document.getElementById("productCategoryId").value;
  const brandName = document.getElementById("productBrandName").value.trim();

  if (isEmpty(name) || !categoryId) {
    showMessage("Product name and category ID are required.");
    return;
  }

  const data = {
    categoryId: Number(categoryId),
    brandName: brandName,
    name: name,
    description: document.getElementById("productDescription").value,
    mainImageKey: document.getElementById("productImage").value,
    status: document.getElementById("productStatus").value,
    isActive: document.getElementById("productIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    await apiSend("/admin/products", "POST", data);
    showMessage("Product created successfully.");
    this.reset();
    loadProducts();
  } catch (error) {
    showMessage(error.message);
  }
});

async function loadProducts() {
  try {
    const data = await apiGet("/admin/products");

    productData = (data || []).filter(function (product) {
      return product.isActive === true;
    });

    showProducts(productData);
  } catch (error) {
    showMessage(error.message);
  }
}

async function searchProduct() {
  const id = document.getElementById("productSearchId").value;

  if (!id) {
    showMessage("Enter product ID to search.");
    return;
  }

  try {
    const product = await apiGet("/admin/products/" + id);

    if (product && product.isActive === true) {
      showProducts([product]);
    } else {
      showProducts([]);
      showMessage("Product is inactive or not found.");
    }
  } catch (error) {
    showMessage(error.message);
  }
}

function showProducts(data) {
  const box = document.getElementById("productList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active products found.</p>";
    return;
  }

  data.forEach(function (product) {
    box.innerHTML += `
      <div class="item">
        <h4>${product.name || "-"}</h4>
        <p><b>ID:</b> ${product.productId}</p>
        <p><b>Category ID:</b> ${product.categoryId || "-"}</p>
        <p><b>Brand:</b> ${product.brandName || "-"}</p>
        <p><b>Status:</b> ${product.status || "-"}</p>
        <p><b>Created By:</b> ${product.createdBy || "-"}</p>
        <p><b>Modified By:</b> ${product.modifiedBy || "-"}</p>

        <div class="item-actions">
          <button class="update-btn" onclick='openProductPopup(${JSON.stringify(product)})'>
            Update
          </button>

          <button class="delete-btn" onclick="deleteProduct(${product.productId})">
            Delete
          </button>
        </div>
      </div>
    `;
  });
}

function openProductPopup(product) {
  editType = "product";
  editId = product.productId;

  document.getElementById("popupTitle").innerText = "Update Product";

  document.getElementById("popupFields").innerHTML = `
    <label>Product Name</label>
    <input id="editProductName" value="${product.name || ""}" />

    <label>Description</label>
    <textarea id="editProductDescription">${product.description || ""}</textarea>

    <label>Main Image Key</label>
    <input id="editProductImage" value="${product.mainImageKey || ""}" />

    <label>Status</label>
    <select id="editProductStatus">
      <option value="active" ${product.status === "active" ? "selected" : ""}>active</option>
      <option value="inactive" ${product.status === "inactive" ? "selected" : ""}>inactive</option>
      <option value="draft" ${product.status === "draft" ? "selected" : ""}>draft</option>
    </select>

    <label>Is Active</label>
    <select id="editProductIsActive">
      <option value="true" ${product.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!product.isActive ? "selected" : ""}>false</option>
    </select>
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteProduct(id) {
  if (!checkAuth()) return;

  if (!confirm("Delete this product?")) return;

  try {
    await apiDelete("/admin/products/" + id);
    showMessage("Product deleted successfully.");
    loadProducts();
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= VARIANTS ================= */

document.getElementById("variantForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  const productId = document.getElementById("variantProductId").value;
  const sku = document.getElementById("variantSku").value.trim();
  const price = document.getElementById("variantPrice").value;

  if (!productId || isEmpty(sku)) {
    showMessage("Product ID and SKU are required.");
    return;
  }

  const data = {
    sku: sku,
    color: document.getElementById("variantColor").value,
    size: document.getElementById("variantSize").value,
    price: price ? Number(price) : null,
    isActive: document.getElementById("variantIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    data.productId = Number(productId);
    await apiSend("/admin/variants", "POST", data);
    showMessage("Variant created successfully.");
    this.reset();
  } catch (error) {
    showMessage(error.message);
  }
});

async function searchVariantsByProduct() {
  const productId = document.getElementById("variantSearchProductId").value;

  if (!productId) {
    showMessage("Enter product ID.");
    return;
  }

  try {
    const data = await apiGet("/admin/variants");

    const activeData = (data || []).filter(function (variant) {
      return variant.isActive === true;
    });

    showVariants(activeData);
  } catch (error) {
    showMessage(error.message);
  }
}

function showVariants(data) {
  const box = document.getElementById("variantList");
  box.innerHTML = "";

  if (!data || data.length === 0) {
    box.innerHTML = "<p>No active variants found.</p>";
    return;
  }

  data.forEach(function (variant) {
    box.innerHTML += `
      <div class="item">
        <h4>${variant.sku || "-"}</h4>
        <p><b>ID:</b> ${variant.variantId}</p>
        <p><b>Color:</b> ${variant.color || "-"}</p>
        <p><b>Size:</b> ${variant.size || "-"}</p>
        <p><b>Price:</b> ${variant.price || "-"}</p>
        <p><b>Created By:</b> ${variant.createdBy || "-"}</p>
        <p><b>Modified By:</b> ${variant.modifiedBy || "-"}</p>

        <div class="item-actions">
          <button class="update-btn" onclick='openVariantPopup(${JSON.stringify(variant)})'>
            Update
          </button>

          <button class="delete-btn" onclick="deleteVariant(${variant.variantId})">
            Delete
          </button>
        </div>
      </div>
    `;
  });
}

function openVariantPopup(variant) {
  editType = "variant";
  editId = variant.variantId;

  document.getElementById("popupTitle").innerText = "Update Variant";

  document.getElementById("popupFields").innerHTML = `
    <label>SKU</label>
    <input id="editVariantSku" value="${variant.sku || ""}" />

    <label>Color</label>
    <input id="editVariantColor" value="${variant.color || ""}" />

    <label>Size</label>
    <input id="editVariantSize" value="${variant.size || ""}" />

    <label>Price</label>
    <input id="editVariantPrice" type="number" step="0.01" value="${variant.price || ""}" />

    <label>Is Active</label>
    <select id="editVariantIsActive">
      <option value="true" ${variant.isActive ? "selected" : ""}>true</option>
      <option value="false" ${!variant.isActive ? "selected" : ""}>false</option>
    </select>
  `;

  document.getElementById("popup").classList.remove("hidden");
}

async function deleteVariant(id) {
  if (!checkAuth()) return;

  if (!confirm("Delete this variant?")) return;

  try {
    await apiDelete("/admin/variants/" + id);
    showMessage("Variant deleted successfully.");
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= POPUP UPDATE ================= */

function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

document.getElementById("popupForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  if (!checkAuth()) return;

  try {
    if (editType === "category") {
      const parentId = document.getElementById("editCategoryParentId").value;

      const data = {
        parentId: parentId ? Number(parentId) : null,
        name: document.getElementById("editCategoryName").value.trim(),
        slug: document.getElementById("editCategorySlug").value.trim(),
        isActive: document.getElementById("editCategoryIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/admin/categories/" + editId, "PUT", data);
      loadCategories();
    }

    if (editType === "product") {
      const data = {
        name: document.getElementById("editProductName").value.trim(),
        description: document.getElementById("editProductDescription").value,
        mainImageKey: document.getElementById("editProductImage").value,
        status: document.getElementById("editProductStatus").value,
        isActive: document.getElementById("editProductIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/admin/products/" + editId, "PUT", data);
      loadProducts();
    }

    if (editType === "variant") {
      const price = document.getElementById("editVariantPrice").value;

      const data = {
        sku: document.getElementById("editVariantSku").value.trim(),
        color: document.getElementById("editVariantColor").value,
        size: document.getElementById("editVariantSize").value,
        price: price ? Number(price) : null,
        isActive: document.getElementById("editVariantIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/admin/variants/" + editId, "PUT", data);
    }

    closePopup();
    showMessage("Updated successfully.");
  } catch (error) {
    showMessage(error.message);
  }
});