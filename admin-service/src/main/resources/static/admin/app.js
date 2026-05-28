const apiInput = document.getElementById("apiBaseUrl");
const toast = document.getElementById("toast");

/* Get current API base URL from sidebar input */
function apiBase() {
  return apiInput.value.trim();
}

/* Show small success/error message(which is used to show pop up message like category created successfully) */
function showToast(message) {
  toast.textContent = message;
  toast.classList.remove("hidden");

  setTimeout(() => {
    toast.classList.add("hidden");
  }, 2500);
}

/* Show dash if value is empty */
function safeValue(value) {
  if (value === null || value === undefined || value === "") {
    return "-";
  }

  return value;
}

/* Fetch function(for calling backend from frontend) */
async function requestJson(url, options = {}) {
  const response = await fetch(url, {
    headers: {
      "Content-Type": "application/json"
    },
    ...options
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || "Request failed");
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

/* SIDEBAR NAVIGATION */

document.querySelectorAll(".nav-btn").forEach((button) => {
  button.addEventListener("click", () => {

    document.querySelectorAll(".nav-btn").forEach((btn) => {
      btn.classList.remove("active");
    });

    document.querySelectorAll(".content-section").forEach((section) => {
      section.classList.remove("active-section");
    });

    button.classList.add("active");

    const sectionId = button.dataset.section;
    document.getElementById(sectionId).classList.add("active-section");
  });
});

/* Refresh all tables */
document.getElementById("refreshAllBtn").addEventListener("click", loadAll);

/* CATEGORY CRUD */

const categoryForm = document.getElementById("categoryForm");

categoryForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const categoryId = document.getElementById("categoryId").value;
  const parentIdValue = document.getElementById("categoryParentId").value;

  /* Data is send to backend */
  const payload = {
    parentId: parentIdValue ? Number(parentIdValue) : null,
    name: document.getElementById("categoryName").value,
    slug: document.getElementById("categorySlug").value
  };

  /* It checks if the id exists it will update and if not it will create new id */
  try {
    if (categoryId) {
      await requestJson(`${apiBase()}/categories/${categoryId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
      });

      showToast("Category updated successfully");
    } else {
      await requestJson(`${apiBase()}/categories`, {
        method: "POST",
        body: JSON.stringify(payload)
      });

      showToast("Category created successfully");
    }

    clearCategoryForm();
    loadCategories();

  } catch (error) {
    showToast("Category error: " + error.message);
  }
});

document.getElementById("clearCategoryBtn").addEventListener("click", clearCategoryForm);

function clearCategoryForm() {
  categoryForm.reset();
  document.getElementById("categoryId").value = "";
}

/* It puts the data into the table*/
async function loadCategories() {
  try {
    const categories = await requestJson(`${apiBase()}/categories`);

    const tbody = document.getElementById("categoriesTableBody");

    tbody.innerHTML = categories.map((category) => {
      return `
        <tr>
          <td>${category.categoryId}</td>
          <td>${safeValue(category.parentId)}</td>
          <td>${safeValue(category.name)}</td>
          <td>${safeValue(category.slug)}</td>

          <td>
            <div class="actions">
              <button
                class="secondary-btn small-btn"
                onclick='editCategory(${JSON.stringify(category)})'
              >
                Edit
              </button>

              <button
                class="danger-btn small-btn"
                onclick='deleteCategory(${category.categoryId})'
              >
                Delete
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join("");

  } catch (error) {
    showToast("Could not load categories");
  }
}

function editCategory(category) {
  document.getElementById("categoryId").value = category.categoryId;
  document.getElementById("categoryParentId").value = category.parentId || "";
  document.getElementById("categoryName").value = category.name || "";
  document.getElementById("categorySlug").value = category.slug || "";

  showToast("Category loaded for editing");
}

async function deleteCategory(categoryId) {
  const confirmDelete = confirm("Delete this category?");

  if (!confirmDelete) {
    return;
  }

  try {
    await requestJson(`${apiBase()}/categories/${categoryId}`, {
      method: "DELETE"
    });

    showToast("Category deleted successfully");
    loadCategories();

  } catch (error) {
    showToast("Delete failed: " + error.message);
  }
}

/* PRODUCT CRUD */

const productForm = document.getElementById("productForm");

productForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const productId = document.getElementById("productId").value;
  const brandIdValue = document.getElementById("productBrandId").value;

  /* Number is used as HTML return value is text but the backend expects integer value so we convert them to match the id with the db */
  const payload = {
    categoryId: Number(document.getElementById("productCategoryId").value),
    brandId: brandIdValue ? Number(brandIdValue) : null,
    name: document.getElementById("productName").value,
    description: document.getElementById("productDescription").value || null,
    mainImageKey: document.getElementById("productMainImageKey").value || null,
    status: document.getElementById("productStatus").value
  };

  try {
    if (productId) {
      await requestJson(`${apiBase()}/products/${productId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
      });

      showToast("Product updated successfully");
    } else {
      await requestJson(`${apiBase()}/products`, {
        method: "POST",
        body: JSON.stringify(payload)
      });

      showToast("Product created successfully");
    }

    clearProductForm();
    loadProducts();

  } catch (error) {
    showToast("Product error: " + error.message);
  }
});

document.getElementById("clearProductBtn").addEventListener("click", clearProductForm);

function clearProductForm() {
  productForm.reset();
  document.getElementById("productId").value = "";
  document.getElementById("productStatus").value = "active";
}

async function loadProducts() {
  try {
    const products = await requestJson(`${apiBase()}/products`);

    const tbody = document.getElementById("productsTableBody");

    tbody.innerHTML = products.map((product) => {
      return `
        <tr>
          <td>${product.productId}</td>
          <td>${product.categoryId}</td>
          <td>${safeValue(product.brandId)}</td>
          <td>${safeValue(product.name)}</td>
          <td>${safeValue(product.status)}</td>
          <td>${safeValue(product.mainImageKey)}</td>

          <td>
            <div class="actions">
              <button
                class="secondary-btn small-btn"
                onclick='editProduct(${JSON.stringify(product)})'
              >
                Edit
              </button>

              <button
                class="danger-btn small-btn"
                onclick='deleteProduct(${product.productId})'
              >
                Delete
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join("");

  } catch (error) {
    showToast("Could not load products");
  }
}

function editProduct(product) {
  document.getElementById("productId").value = product.productId;
  document.getElementById("productCategoryId").value = product.categoryId || "";
  document.getElementById("productBrandId").value = product.brandId || "";
  document.getElementById("productName").value = product.name || "";
  document.getElementById("productDescription").value = product.description || "";
  document.getElementById("productMainImageKey").value = product.mainImageKey || "";
  document.getElementById("productStatus").value = product.status || "active";

  showToast("Product loaded for editing");
}

async function deleteProduct(productId) {
  const confirmDelete = confirm("Delete this product?");

  if (!confirmDelete) {
    return;
  }

  try {
    await requestJson(`${apiBase()}/products/${productId}`, {
      method: "DELETE"
    });

    showToast("Product deleted successfully");
    loadProducts();

  } catch (error) {
    showToast("Delete failed: " + error.message);
  }
}

/* PRODUCT VARIANT CRUD */

const variantForm = document.getElementById("variantForm");

variantForm.addEventListener("submit", async (event) => {
  event.preventDefault();

  const variantId = document.getElementById("variantId").value;
  const priceValue = document.getElementById("variantPrice").value;

  const payload = {
    productId: Number(document.getElementById("variantProductId").value),
    sku: document.getElementById("variantSku").value,
    color: document.getElementById("variantColor").value || null,
    size: document.getElementById("variantSize").value || null,
    price: priceValue ? Number(priceValue) : null,
    isActive: document.getElementById("variantIsActive").value === "true"
  };

  try {
    if (variantId) {
      await requestJson(`${apiBase()}/variants/${variantId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
      });

      showToast("Variant updated successfully");
    } else {
      await requestJson(`${apiBase()}/variants`, {
        method: "POST",
        body: JSON.stringify(payload)
      });

      showToast("Variant created successfully");
    }

    clearVariantForm();
    loadVariants();

  } catch (error) {
    showToast("Variant error: " + error.message);
  }
});

document.getElementById("clearVariantBtn").addEventListener("click", clearVariantForm);

function clearVariantForm() {
  variantForm.reset();
  document.getElementById("variantId").value = "";
  document.getElementById("variantIsActive").value = "true";
}

async function loadVariants() {
  try {
    const variants = await requestJson(`${apiBase()}/variants`);

    const tbody = document.getElementById("variantsTableBody");

    tbody.innerHTML = variants.map((variant) => {
      return `
        <tr>
          <td>${variant.variantId}</td>
          <td>${variant.productId}</td>
          <td>${safeValue(variant.sku)}</td>
          <td>${safeValue(variant.color)}</td>
          <td>${safeValue(variant.size)}</td>
          <td>${safeValue(variant.price)}</td>
          <td>${safeValue(variant.isActive)}</td>

          <td>
            <div class="actions">
              <button
                class="secondary-btn small-btn"
                onclick='editVariant(${JSON.stringify(variant)})'
              >
                Edit
              </button>

              <button
                class="danger-btn small-btn"
                onclick='deleteVariant(${variant.variantId})'
              >
                Delete
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join("");

  } catch (error) {
    showToast("Could not load variants");
  }
}

function editVariant(variant) {
  document.getElementById("variantId").value = variant.variantId;
  document.getElementById("variantProductId").value = variant.productId || "";
  document.getElementById("variantSku").value = variant.sku || "";
  document.getElementById("variantColor").value = variant.color || "";
  document.getElementById("variantSize").value = variant.size || "";
  document.getElementById("variantPrice").value = variant.price || "";
  document.getElementById("variantIsActive").value = String(variant.isActive ?? true);

  showToast("Variant loaded for editing");
}

async function deleteVariant(variantId) {
  const confirmDelete = confirm("Delete this variant?");

  if (!confirmDelete) {
    return;
  }

  try {
    await requestJson(`${apiBase()}/variants/${variantId}`, {
      method: "DELETE"
    });

    showToast("Variant deleted successfully");
    loadVariants();

  } catch (error) {
    showToast("Delete failed: " + error.message);
  }
}

/* LOAD DATA WHEN PAGE OPENS */

function loadAll() {
  loadCategories();
  loadProducts();
  loadVariants();
}

loadAll();