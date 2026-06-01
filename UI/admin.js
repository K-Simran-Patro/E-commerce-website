/* this gets the api url from the input box in the sidebar, it is used because when js sends data to backend it needs to know where the backend is running */
function apiUrl() {
  return document.getElementById("apiUrl").value;
}

/* it shows pop up messages on the page like category created successfully*/
function showMessage(text) {
  document.getElementById("message").innerText = text;
}

/* it is used to open or close sidebar menus like to see the sub categories*/
function toggleMenu(id) {
  document.getElementById(id).classList.toggle("hidden");
}

/* it is used to show one page at a time */
function showPage(id) {
  let pages = document.querySelectorAll(".page");

  pages.forEach(function(page) {
    page.classList.add("hidden");
  });

  document.getElementById(id).classList.remove("hidden");
}

/* it is used to send data from admin page to backend, the fetch function is a built in function in js which is used to call api, method is used to tell the backend what function to perform, header tells the backend that the request is in json format, stringify is used to convert the text inton json format as api send it in text format */
async function sendData(url, method, data) {
  let response = await fetch(url, {
    method: method,
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(data)
  });

  

  if (response.ok) {
    showMessage("Action completed successfully");
  } else {
    showMessage("Something went wrong");
  }
}

/* response ok means it checks whether the backend request succeeded or failed and deleteData is used to request to the backend to remove a record from database */
async function deleteData(url) {
  let response = await fetch(url, {
    method: "DELETE"
  });

  if (response.ok) {
    showMessage("Deleted successfully");
  } else {
    showMessage("Delete failed");
  }
}

/* CATEGORY */

/* it runs when create category form is submitted */
document.getElementById("createCategoryForm").addEventListener("submit", function(e) {
  e.preventDefault(); /* stops the browser from refreshing the page */

  let parentId = document.getElementById("createCategoryParentId").value; /* get the value entered in the parent id section */

  /* creates data object that will be sent to backend */
  let data = {
    parentId: parentId ? Number(parentId) : null,
    name: document.getElementById("createCategoryName").value,
    slug: document.getElementById("createCategorySlug").value
  };

  sendData(apiUrl() + "/categories", "POST", data);
  this.reset(); /* reset function is used to clear the form after submit */
});

/* it is used to update an existing category and it needs the id */
document.getElementById("updateCategoryForm").addEventListener("submit", function(e) {
  e.preventDefault(); 

  let id = document.getElementById("updateCategoryId").value;
  let parentId = document.getElementById("updateCategoryParentId").value; 

  let data = {
    parentId: parentId ? Number(parentId) : null,
    name: document.getElementById("updateCategoryName").value,
    slug: document.getElementById("updateCategorySlug").value
  };

  sendData(apiUrl() + "/categories/" + id, "PUT", data);
  this.reset();
});

/* it is used to delete a category */
document.getElementById("deleteCategoryForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let id = document.getElementById("deleteCategoryId").value;

  if (confirm("Delete this category?")) {
    deleteData(apiUrl() + "/categories/" + id);
    this.reset();
  }
});

/* PRODUCT */
/* it runs when create product form is submitted */
document.getElementById("createProductForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let brandId = document.getElementById("createProductBrandId").value;

  let data = {
    categoryId: Number(document.getElementById("createProductCategoryId").value), /*number function converts input text into an actual number because backend and database use integer id.*/
    brandId: brandId ? Number(brandId) : null,
    name: document.getElementById("createProductName").value,
    description: document.getElementById("createProductDescription").value,
    mainImageKey: document.getElementById("createProductImage").value,
    status: document.getElementById("createProductStatus").value
  };

  sendData(apiUrl() + "/products", "POST", data);
  this.reset();
});

/* it is used to update an existing product and it needs the id */
document.getElementById("updateProductForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let id = document.getElementById("updateProductId").value;
  let brandId = document.getElementById("updateProductBrandId").value;

  let data = {
    categoryId: Number(document.getElementById("updateProductCategoryId").value),
    brandId: brandId ? Number(brandId) : null,
    name: document.getElementById("updateProductName").value,
    description: document.getElementById("updateProductDescription").value,
    mainImageKey: document.getElementById("updateProductImage").value,
    status: document.getElementById("updateProductStatus").value
  };

  sendData(apiUrl() + "/products/" + id, "PUT", data);
  this.reset();
});

/* it is used to delete a product */
document.getElementById("deleteProductForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let id = document.getElementById("deleteProductId").value;

  if (confirm("Delete this product?")) {
    deleteData(apiUrl() + "/products/" + id);
    this.reset();
  }
});

/* PRODUCT VARIANT */
/* it runs when create productvariant form is submitted */
document.getElementById("createVariantForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let price = document.getElementById("createVariantPrice").value;

  let data = {
    productId: Number(document.getElementById("createVariantProductId").value),
    sku: document.getElementById("createVariantSku").value,
    color: document.getElementById("createVariantColor").value,
    size: document.getElementById("createVariantSize").value,
    price: price ? Number(price) : null,
    isActive: document.getElementById("createVariantActive").value === "true"
  };

  sendData(apiUrl() + "/variants", "POST", data);
  this.reset();
});

/* it is used to update an existing productvariant and it needs the id */
document.getElementById("updateVariantForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let id = document.getElementById("updateVariantId").value;
  let price = document.getElementById("updateVariantPrice").value;

  let data = {
    productId: Number(document.getElementById("updateVariantProductId").value),
    sku: document.getElementById("updateVariantSku").value,
    color: document.getElementById("updateVariantColor").value,
    size: document.getElementById("updateVariantSize").value,
    price: price ? Number(price) : null,
    isActive: document.getElementById("updateVariantActive").value === "true"
  };

  sendData(apiUrl() + "/variants/" + id, "PUT", data);
  this.reset();
});

/* it is used to delete a productvariant */
document.getElementById("deleteVariantForm").addEventListener("submit", function(e) {
  e.preventDefault();

  let id = document.getElementById("deleteVariantId").value;

  if (confirm("Delete this variant?")) {
    deleteData(apiUrl() + "/variants/" + id);
    this.reset();
  }
});