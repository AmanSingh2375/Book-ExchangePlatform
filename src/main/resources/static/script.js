
const API_URL = "/books";

const bookGrid = document.getElementById("book-grid");
const searchInput = document.getElementById("search-input");
const searchButton = document.getElementById("search-button");
const categoryFilter = document.getElementById("category-filter");
const bookCount = document.getElementById("book-count");
const emptyMessage = document.getElementById("empty-message");
const addBookForm = document.getElementById("add-book-form");
const formMessage = document.getElementById("form-message");

const coverColors = [
    "#345d49",
    "#bd754f",
    "#536b88",
    "#a78a52",
    "#755c83",
    "#477c78"
];

let allBooks = [];

function escapeHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, function (char) {
        return {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        }[char];
    });
}

async function loadBooks() {
    bookGrid.innerHTML = '<p class="loading-message">Loading books...</p>';
    emptyMessage.hidden = true;

    try {
        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Could not load books");
        }

        allBooks = await response.json();
        displayBooks();
    } catch (error) {
        bookGrid.innerHTML = "";
        bookCount.textContent = "Connection error";
        emptyMessage.hidden = false;
        emptyMessage.textContent =
            "Could not load books. Check that the Spring Boot server is running.";
    }
}

function displayBooks() {
    const query = searchInput.value.trim().toLowerCase();
    const category = categoryFilter.value;

    const filteredBooks = allBooks.filter(function (book) {
        const title = (book.title || "").toLowerCase();
        const author = (book.author || "").toLowerCase();
        const matchesSearch = title.includes(query) || author.includes(query);
        const matchesCategory =
            category === "all" || book.category === category;

        return matchesSearch && matchesCategory;
    });

    bookCount.textContent = `${filteredBooks.length} book${filteredBooks.length !== 1 ? "s" : ""} found`;

    if (filteredBooks.length === 0) {
        bookGrid.innerHTML = "";
        emptyMessage.hidden = false;
        return;
    }

    emptyMessage.hidden = true;

    bookGrid.innerHTML = filteredBooks.map(function (book, index) {
        const color = coverColors[index % coverColors.length];

        return `
            <article class="book-card">
                <div class="book-cover">
                    <div class="cover-design" style="background:${color}">
                        ${escapeHTML(book.title)}
                    </div>
                </div>
                <div class="book-details">
                    <span class="category-tag">
                        ${escapeHTML(book.category || "Other")}
                    </span>
                    <h3>${escapeHTML(book.title)}</h3>
                    <p class="book-author">
                        By ${escapeHTML(book.author || "Unknown author")}
                    </p>
                    <p class="book-owner">
                        📚 Shared by ${escapeHTML(book.owner || "Reader")}
                    </p>
                    
<button class="primary-button exchange-button"
    onclick="requestExchange(${book.id})">
    Request Exchange ↗
</button>
                </div>
            </article>
        `;
    }).join("");
}

searchInput.addEventListener("input", displayBooks);
searchButton.addEventListener("click", displayBooks);
categoryFilter.addEventListener("change", displayBooks);

addBookForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const book = {
        id: Number(document.getElementById("book-id").value),
        title: document.getElementById("book-title").value.trim(),
        author: document.getElementById("book-author").value.trim(),
        category: document.getElementById("book-category").value,
        owner: document.getElementById("book-owner").value.trim()
    };

    if (!book.title || !book.author || !book.category || !book.owner) {
        formMessage.textContent = "Please fill in all fields.";
        return;
    }

    if (!Number.isSafeInteger(book.id) || book.id < 1) {
        formMessage.textContent = "Please enter a valid positive Book ID.";
        return;
    }

    formMessage.textContent = "Adding book...";

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(book)
        });

        if (!response.ok) {
            throw new Error("Could not add book");
        }

        formMessage.textContent = "Book added successfully!";

        addBookForm.reset();
        await loadBooks();

        document.getElementById("books").scrollIntoView({
            behavior: "smooth"
        });
    } catch (error) {
        formMessage.textContent =
            "Could not add book. Check the server and ensure the Book ID is unique.";
    }
});

loadBooks();


async function requestExchange(bookId) {
    const book = allBooks.find(function (item) {
        return item.id === bookId;
    });

    if (!book) {
        alert("Book not found. Please refresh the page.");
        return;
    }

    const requesterName = prompt("Enter your name to request this book:");

    if (!requesterName || !requesterName.trim()) {
        return;
    }

    if (requesterName.trim().toLowerCase() ===
        (book.owner || "").trim().toLowerCase()) {
        alert("You cannot request your own book!");
        return;
    }

    try {
        const response = await fetch("/requests", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                bookId: book.id,
                requesterName: requesterName.trim(),
                ownerName: book.owner
            })
        });

        
if (!response.ok) {
    if (response.status === 401) {
        throw new Error("Please login first, then try requesting the book.");
    }
    const errorText = await response.text();
    throw new Error(errorText || "Request failed");
}

        alert("Exchange request sent successfully! Status: Pending");
    } catch (error) {
        console.error(error);
        alert(error.message || "Could not send request.");
    }
}




async function loadRequests() {
    const requestList = document.getElementById("request-list");
    requestList.innerHTML = '<p class="loading-message">Loading requests...</p>';

    try {
        const response = await fetch("/requests");

        if (!response.ok) {
            throw new Error("Could not load requests");
        }

        const requests = await response.json();

        if (requests.length === 0) {
            requestList.innerHTML =
                '<p class="empty-message">No exchange requests yet.</p>';
            return;
        }

        requestList.innerHTML = `
            <div class="request-grid">
                ${requests.map(function (request) {
                    const status = ["Pending", "Accepted", "Rejected"]
                        .includes(request.status) ? request.status : "Pending";

                    const isOwner =
                        String(currentUserName || "").trim().toLowerCase() ===
                        String(request.ownerName || "").trim().toLowerCase();

                    console.log({
                        loggedInUser: currentUserName,
                        bookOwner: request.ownerName,
                        isOwner: isOwner
                    });

                    return `
                        <article class="request-card">
                            <span class="status-tag status-${status.toLowerCase()}">
                                ${escapeHTML(status)}
                            </span>
                            <h3>${escapeHTML(request.requesterName)}</h3>
                            <p>Requested book ID: ${escapeHTML(request.bookId)}</p>
                            <p>Book owner: ${escapeHTML(request.ownerName)}</p>
                            <p>Request ID: ${escapeHTML(request.id)}</p>

                            ${status === "Pending" &&
  String(currentUserName || "").trim().toLowerCase() ===
  String(request.ownerName || "").trim().toLowerCase() ? `
                                <div class="request-actions">
                                    <button class="primary-button"
                                        onclick="updateRequestStatus(${Number(request.id)}, 'Accepted')">
                                        Accept
                                    </button>
                                    <button class="reject-button"
                                        onclick="updateRequestStatus(${Number(request.id)}, 'Rejected')">
                                        Reject
                                    </button>
                                </div>
                            ` : ""}
                        </article>
                    `;
                }).join("")}
            </div>
        `;
    } catch (error) {
        console.error(error);
        requestList.innerHTML =
            '<p class="empty-message">Could not load requests. Please refresh.</p>';
    }
}

async function updateRequestStatus(id, status) {
    try {
        const response = await fetch(
            `/requests/${id}/status?status=${status}`,
            { method: "PATCH" }
        );

        if (!response.ok) {
            throw new Error("Could not update request");
        }

        alert(`Request ${status.toLowerCase()} successfully!`);
        await loadRequests();
    } catch (error) {
        console.error(error);
        alert("Could not update status. Please try again.");
    }
}

async function loadRequests() {
    const requestList = document.getElementById("request-list");
    requestList.innerHTML = '<p class="loading-message">Loading requests...</p>';

    try {
        const response = await fetch("/requests");

        if (!response.ok) {
            throw new Error("Could not load requests");
        }

        const requests = await response.json();

        if (requests.length === 0) {
            requestList.innerHTML =
                '<p class="empty-message">No exchange requests yet.</p>';
            return;
        }

        requestList.innerHTML = `
            <div class="request-grid">
                ${requests.map(function (request) {
                    const status = ["Pending", "Accepted", "Rejected"]
                        .includes(request.status) ? request.status : "Pending";

                    return `
                        <article class="request-card">
                            <span class="status-tag status-${status.toLowerCase()}">
                                ${escapeHTML(status)}
                            </span>
                            <h3>${escapeHTML(request.requesterName)}</h3>
                            <p>Requested book ID: ${escapeHTML(request.bookId)}</p>
                            <p>Book owner: ${escapeHTML(request.ownerName)}</p>
                            <p>Request ID: ${escapeHTML(request.id)}</p>

                            ${status === "Pending" ? `
                                <div class="request-actions">
                                    <button class="primary-button"
                                        onclick="updateRequestStatus(${Number(request.id)}, 'Accepted')">
                                        Accept
                                    </button>
                                    <button class="reject-button"
                                        onclick="updateRequestStatus(${Number(request.id)}, 'Rejected')">
                                        Reject
                                    </button>
                                </div>
                            ` : ""}
                        </article>
                    `;
                }).join("")}
            </div>
        `;
    } catch (error) {
        console.error(error);
        requestList.innerHTML =
            '<p class="empty-message">Could not load requests. Please refresh.</p>';
    }
}

async function updateRequestStatus(id, status) {
    try {
        const response = await fetch(
            `/requests/${id}/status?status=${status}`,
            { method: "PATCH" }
        );

        if (!response.ok) {
            throw new Error("Could not update request");
        }

        alert(`Request ${status.toLowerCase()} successfully!`);
        await loadRequests();
    } catch (error) {
        console.error(error);
        alert("Could not update status. Please try again.");
    }
}

document.getElementById("refresh-requests")
    .addEventListener("click", loadRequests);

loadRequests();
let isRegisterMode = false;

const authForm = document.getElementById("auth-form");
const authTitle = document.getElementById("auth-title");
const authDescription = document.querySelector(".auth-description");
const nameField = document.getElementById("name-field");
const authName = document.getElementById("auth-name");
const authEmail = document.getElementById("auth-email");
const authPassword = document.getElementById("auth-password");
const authSubmit = document.getElementById("auth-submit");
const authToggle = document.getElementById("auth-toggle");
const authSwitchText = document.getElementById("auth-switch-text");
const authMessage = document.getElementById("auth-message");

authToggle.addEventListener("click", function () {
    isRegisterMode = !isRegisterMode;

    authTitle.textContent = isRegisterMode ? "Create Account" : "Student Login";
    authDescription.textContent = isRegisterMode
        ? "Join BookNest and start exchanging books."
        : "Sign in to connect with fellow readers.";
    nameField.hidden = !isRegisterMode;
    authName.required = isRegisterMode;
    authSubmit.textContent = isRegisterMode ? "Register" : "Login";
    authSwitchText.textContent = isRegisterMode
        ? "Already have an account?"
        : "New to BookNest?";
    authToggle.textContent = isRegisterMode ? "Login instead" : "Create an account";
    authMessage.textContent = "";
});

authForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    authSubmit.disabled = true;
    authMessage.textContent = "Please wait...";

    const payload = {
        email: authEmail.value.trim(),
        password: authPassword.value
    };

    if (isRegisterMode) {
        payload.name = authName.value.trim();
    }

    try {
        const endpoint = isRegisterMode
            ? "/auth/register"
            : "/auth/login";

        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || "Something went wrong.");
        }

        authMessage.textContent = result.message;

        if (isRegisterMode) {
            authForm.reset();
            authToggle.click();
        } else {
            authMessage.textContent = `Welcome, ${result.name}! Login successful.`;
            showLoggedInUser(result.name);
        }
    } catch (error) {
        authMessage.textContent = error.message || "Could not connect to server.";
    } finally {
        authSubmit.disabled = false;
    }
});
const loginNav = document.getElementById("login-nav");
const loggedInUser = document.getElementById("logged-in-user");
const logoutButton = document.getElementById("logout-button");


let currentUserName = "";

function showLoggedInUser(name) {
    currentUserName = name;
    loginNav.hidden = true;
    loggedInUser.hidden = false;
    logoutButton.hidden = false;
    loggedInUser.textContent = `Hi, ${name}`;
}

function showLoggedOutUser() {
    currentUserName = "";
    loginNav.hidden = false;
    loggedInUser.hidden = true;
    logoutButton.hidden = true;
    loggedInUser.textContent = "";
}

logoutButton.addEventListener("click", async function () {
    try {
        const response = await fetch("/auth/logout", {
            method: "POST",
            credentials: "same-origin"
        });

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        showLoggedOutUser();
        authMessage.textContent = "You have logged out successfully.";
        authForm.reset();
    } catch (error) {
        alert(error.message || "Could not log out.");

    }
});
async function restoreLoginSession() {
    try {
        const response = await fetch("/auth/me", {
            credentials: "same-origin"
        });

        if (!response.ok) {
            showLoggedOutUser();
            return;
        }

        const user = await response.json();
        showLoggedInUser(user.name);
    } catch (error) {
        console.error("Could not restore login session:", error);
        showLoggedOutUser();
    }
}

restoreLoginSession();