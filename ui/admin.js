/* ================= BASIC SETTINGS ================= */

let editType = "";
let editId = null;

function apiUrl() {
  return document.getElementById("apiUrl").value.trim();
}

function adminUserId() {
  return document.getElementById("adminUserId").value.trim();
}

function authToken() {
  return document.getElementById("authToken").value.trim();
}

function saveAuth() {
  localStorage.setItem("apiUrl", apiUrl());
  localStorage.setItem("adminUserId", adminUserId());
  localStorage.setItem("authToken", authToken());
  showMessage("Auth details saved");
}

function loadAuth() {
  if (localStorage.getItem("apiUrl")) {
    document.getElementById("apiUrl").value = localStorage.getItem("apiUrl");
  }

  if (localStorage.getItem("adminUserId")) {
    document.getElementById("adminUserId").value = localStorage.getItem("adminUserId");
  }

  if (localStorage.getItem("authToken")) {
    document.getElementById("authToken").value = localStorage.getItem("authToken");
  }
}

function showMessage(text) {
  document.getElementById("message").innerText = text;
}

function showPage(id) {
  document.querySelectorAll(".page").forEach(function(page) {
    page.classList.add("hidden");
  });

  document.getElementById(id).classList.remove("hidden");
  showMessage("");

  if (id === "categoryPage") loadCategories();
  if (id === "productPage") loadProducts();
}

/* ================= API HELPERS ================= */

function getHeaders() {
  let token = authToken();

  let headers = {
    "Content-Type": "application/json"
  };

  /*
    AUTHENTICATION / AUTHORIZATION:
    This supports both:
    Option A: Spring Boot JWT
    Option B: Supabase Auth JWT

    Both are sent like:
    Authorization: Bearer TOKEN_HERE

    Backend must verify this token and check admin role.
  */
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

  return headers;
}

async function apiGet(path) {
  let response = await fetch(apiUrl() + path, {
    method: "GET",
    headers: getHeaders()
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  return response.json();
}

async function apiSend(path, method, data) {
  let response = await fetch(apiUrl() + path, {
    method: method,
    headers: getHeaders(),
    body: JSON.stringify(data)
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  try {
    return await response.json();
  } catch {
    return await response.text();
  }
}

async function apiDelete(path) {
  let response = await fetch(apiUrl() + path, {
    method: "DELETE",
    headers: getHeaders()
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  return response.text();
}

/*
  IMPORTANT:
  Your admin-service API paths are not created yet.
  Once ready, change these paths here if needed.

  Current assumed paths:
  Categories:
  GET    /api/admin/categories
  GET    /api/admin/categories/{id}
  POST   /api/admin/categories
  PUT    /api/admin/categories/{id}
  DELETE /api/admin/categories/{id}

  Products:
  GET    /api/admin/products
  GET    /api/admin/products/{id}
  POST   /api/admin/products
  PUT    /api/admin/products/{id}
  DELETE /api/admin/products/{id}

  Variants:
  GET    /api/admin/products/{productId}/variants
  POST   /api/admin/products/{productId}/variants
  PUT    /api/admin/variants/{variantId}
  DELETE /api/admin/variants/{variantId}
*/

/* ================= VALIDATION ================= */

function checkAuth() {
  if (!adminUserId()) {
    showMessage("Please enter Admin User ID");
    return false;
  }

  if (!authToken()) {
    showMessage("Please enter auth token");
    return false;
  }

  return true;
}

function isEmpty(value) {
  return value === null || value === undefined || value.trim() === "";
}

/* ================= CATEGORIES ================= */

let categoryData = [];

document.getElementById("categoryForm").addEventListener("submit", async function(e) {
  e.preventDefault();

  if (!checkAuth()) return;

  let name = document.getElementById("categoryName").value;
  let slug = document.getElementById("categorySlug").value;
  let parentId = document.getElementById("categoryParentId").value;

  if (isEmpty(name) || isEmpty(slug)) {
    showMessage("Category name and slug are required");
    return;
  }

  let data = {
    parentId: parentId ? Number(parentId) : null,
    name: name,
    slug: slug,
    isActive: document.getElementById("categoryIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    await apiSend("/api/admin/categories", "POST", data);
    showMessage("Category created");
    this.reset();
    loadCategories();
  } catch (error) {
    showMessage(error.message);
  }
});

async function loadCategories() {
  try {
    let data = await apiGet("/api/admin/categories");

    categoryData = data.filter(function(category) {
      return category.isActive === true;
    });

    showCategories(categoryData);
  } catch (error) {
    showMessage(error.message);
  }
}

async function searchCategory() {
  let id = document.getElementById("categorySearchId").value;

  if (!id) {
    showMessage("Enter category ID to search");
    return;
  }

  try {
    let category = await apiGet("/api/admin/categories/" + id);

    if (category.isActive === true) {
      showCategories([category]);
    } else {
      showCategories([]);
      showMessage("Category is inactive or not visible");
    }
  } catch (error) {
    showMessage(error.message);
  }
}

function showCategories(data) {
  let box = document.getElementById("categoryList");
  box.innerHTML = "";

  if (data.length === 0) {
    box.innerHTML = "<p>No active categories found</p>";
    return;
  }

  data.forEach(function(category) {
    box.innerHTML += `
      <div class="item">
        <h4>${category.name}</h4>
        <p><b>ID:</b> ${category.categoryId}</p>
        <p><b>Parent ID:</b> ${category.parentId || "-"}</p>
        <p><b>Slug:</b> ${category.slug}</p>
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
  return categoryData.some(function(category) {
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
    <input id="editCategoryName" value="${category.name}" />

    <label>Slug</label>
    <input id="editCategorySlug" value="${category.slug}" />

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
    await apiDelete("/api/admin/categories/" + id);
    showMessage("Category deleted");
    loadCategories();
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= PRODUCTS ================= */

let productData = [];

document.getElementById("productForm").addEventListener("submit", async function(e) {
  e.preventDefault();

  if (!checkAuth()) return;

  let name = document.getElementById("productName").value;
  let categoryId = document.getElementById("productCategoryId").value;

  if (isEmpty(name) || !categoryId) {
    showMessage("Product name and category ID are required");
    return;
  }

  let data = {
    categoryId: Number(categoryId),
    brandName: document.getElementById("productBrandName").value,
    name: name,
    description: document.getElementById("productDescription").value,
    mainImageKey: document.getElementById("productImage").value,
    status: document.getElementById("productStatus").value,
    isActive: document.getElementById("productIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    await apiSend("/api/admin/products", "POST", data);
    showMessage("Product created");
    this.reset();
    loadProducts();
  } catch (error) {
    showMessage(error.message);
  }
});

async function loadProducts() {
  try {
    let data = await apiGet("/api/admin/products");

    productData = data.filter(function(product) {
      return product.isActive === true;
    });

    showProducts(productData);
  } catch (error) {
    showMessage(error.message);
  }
}

async function searchProduct() {
  let id = document.getElementById("productSearchId").value;

  if (!id) {
    showMessage("Enter product ID to search");
    return;
  }

  try {
    let product = await apiGet("/api/admin/products/" + id);

    if (product.isActive === true) {
      showProducts([product]);
    } else {
      showProducts([]);
      showMessage("Product is inactive or not visible");
    }
  } catch (error) {
    showMessage(error.message);
  }
}

function showProducts(data) {
  let box = document.getElementById("productList");
  box.innerHTML = "";

  if (data.length === 0) {
    box.innerHTML = "<p>No active products found</p>";
    return;
  }

  data.forEach(function(product) {
    box.innerHTML += `
      <div class="item">
        <h4>${product.name}</h4>
        <p><b>ID:</b> ${product.productId}</p>
        <p><b>Category ID:</b> ${product.categoryId || "-"}</p>
        <p><b>Brand:</b> ${product.brandName || "-"}</p>
        <p><b>Status:</b> ${product.status}</p>
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
    <input id="editProductName" value="${product.name}" />

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
    await apiDelete("/api/admin/products/" + id);
    showMessage("Product deleted");
    loadProducts();
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= VARIANTS ================= */

document.getElementById("variantForm").addEventListener("submit", async function(e) {
  e.preventDefault();

  if (!checkAuth()) return;

  let productId = document.getElementById("variantProductId").value;
  let sku = document.getElementById("variantSku").value;
  let price = document.getElementById("variantPrice").value;

  if (!productId || isEmpty(sku)) {
    showMessage("Product ID and SKU are required");
    return;
  }

  let data = {
    sku: sku,
    color: document.getElementById("variantColor").value,
    size: document.getElementById("variantSize").value,
    price: price ? Number(price) : null,
    isActive: document.getElementById("variantIsActive").value === "true",
    createdBy: adminUserId(),
    modifiedBy: adminUserId()
  };

  try {
    await apiSend("/api/admin/products/" + productId + "/variants", "POST", data);
    showMessage("Variant created");
    this.reset();
  } catch (error) {
    showMessage(error.message);
  }
});

async function searchVariantsByProduct() {
  let productId = document.getElementById("variantSearchProductId").value;

  if (!productId) {
    showMessage("Enter product ID");
    return;
  }

  try {
    let data = await apiGet("/api/admin/products/" + productId + "/variants");

    let activeData = data.filter(function(variant) {
      return variant.isActive === true;
    });

    showVariants(activeData);
  } catch (error) {
    showMessage(error.message);
  }
}

function showVariants(data) {
  let box = document.getElementById("variantList");
  box.innerHTML = "";

  if (data.length === 0) {
    box.innerHTML = "<p>No active variants found</p>";
    return;
  }

  data.forEach(function(variant) {
    box.innerHTML += `
      <div class="item">
        <h4>${variant.sku}</h4>
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
    <input id="editVariantSku" value="${variant.sku}" />

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
    await apiDelete("/api/admin/variants/" + id);
    showMessage("Variant deleted");
  } catch (error) {
    showMessage(error.message);
  }
}

/* ================= POPUP UPDATE ================= */

function closePopup() {
  document.getElementById("popup").classList.add("hidden");
}

document.getElementById("popupForm").addEventListener("submit", async function(e) {
  e.preventDefault();

  if (!checkAuth()) return;

  try {
    if (editType === "category") {
      let parentId = document.getElementById("editCategoryParentId").value;

      let data = {
        parentId: parentId ? Number(parentId) : null,
        name: document.getElementById("editCategoryName").value,
        slug: document.getElementById("editCategorySlug").value,
        isActive: document.getElementById("editCategoryIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/api/admin/categories/" + editId, "PUT", data);
      loadCategories();
    }

    if (editType === "product") {
      let data = {
        name: document.getElementById("editProductName").value,
        description: document.getElementById("editProductDescription").value,
        mainImageKey: document.getElementById("editProductImage").value,
        status: document.getElementById("editProductStatus").value,
        isActive: document.getElementById("editProductIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/api/admin/products/" + editId, "PUT", data);
      loadProducts();
    }

    if (editType === "variant") {
      let price = document.getElementById("editVariantPrice").value;

      let data = {
        sku: document.getElementById("editVariantSku").value,
        color: document.getElementById("editVariantColor").value,
        size: document.getElementById("editVariantSize").value,
        price: price ? Number(price) : null,
        isActive: document.getElementById("editVariantIsActive").value === "true",
        modifiedBy: adminUserId()
      };

      await apiSend("/api/admin/variants/" + editId, "PUT", data);
    }

    closePopup();
    showMessage("Updated successfully");

  } catch (error) {
    showMessage(error.message);
  }
});

loadAuth();