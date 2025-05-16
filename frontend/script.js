
let array = [];
let sortingSteps = [];
let currentStep = 0;
let isPaused = false;
let isReset = false;
let stepMode = false;
let speed = 50;
let comparisons = 0;
let swaps = 0;

let arrayContainer, algorithmSelect, speedSlider, comparisonsEl, swapsEl;
let manualInput, fileInput, stepToggle, nextStepBtn, algoInfoBox, progressBar, themeToggle;

let clickSound, swapSound;

window.onload = () => {
  arrayContainer = document.getElementById('arrayContainer');
  algorithmSelect = document.getElementById('algorithmSelect');
  speedSlider = document.getElementById('speedSlider');
  comparisonsEl = document.getElementById('comparisons');
  swapsEl = document.getElementById('swaps');
  manualInput = document.getElementById('manualInput');
  fileInput = document.getElementById('fileInput');
  stepToggle = document.getElementById('stepToggle');
  nextStepBtn = document.getElementById('nextStepBtn');
  algoInfoBox = document.getElementById('algoInfoBox');
  progressBar = document.getElementById('progressBar');
  themeToggle = document.getElementById('themeToggle');

  try {
    clickSound = new Audio("sounds/click.mp3");
    swapSound = new Audio("sounds/swap.mp3");
  } catch {
    clickSound = { play: () => {} };
    swapSound = { play: () => {} };
  }

  speedSlider.addEventListener("input", () => {
    speed = 100 - parseInt(speedSlider.value) + 10;
  });
  
  fileInput.addEventListener("change", handleFileUpload);
  nextStepBtn.addEventListener("click", nextStep);
  algorithmSelect.addEventListener("change", () => displayAlgorithmInfo(algorithmSelect.value));
  themeToggle.addEventListener("click", toggleTheme);

  const savedUser = localStorage.getItem('loggedInUser');
  if (savedUser) renderLoggedInUI(savedUser);

  generateArray();

  window.startSorting = startSorting;
  window.resetSorting = resetSorting;
  window.pauseSorting = pauseSorting;
  window.resumeSorting = resumeSorting;
  window.stopSorting = stopSorting;
  window.applyManualInput = applyManualInput;
  window.handleFileUpload = handleFileUpload;
  window.nextStep = nextStep;
  window.showAuth = showAuth;
  window.toggleAuthMode = toggleAuthMode;
  window.closeAuth = closeAuth;
  window.userLogin = userLogin;
  window.userSignup = userSignup;
  window.logoutUser = logoutUser;
  window.viewHistory = viewHistory;
};

function renderLoggedInUI(username) {
  const navRight = document.querySelector('.nav-right');
  navRight.innerHTML = `
    <span class="nav-welcome">Welcome, ${username} 👋</span>
    <button id="themeToggle">🌙</button>
    <a href="#about" class="nav-link">About Us</a>
    <button class="history-btn" onclick="viewHistory()">📜 History</button>
    <button class="logout-btn" onclick="logoutUser()">Logout</button>
  `;
  themeToggle = document.getElementById('themeToggle');
  themeToggle.addEventListener("click", toggleTheme);
}

function viewHistory() {
  const box = document.getElementById("historyBox");
  box.style.display = box.style.display === "none" ? "block" : "none";
  box.scrollIntoView({ behavior: "smooth" });

  const username = localStorage.getItem("loggedInUser");
  if (!username) {
    document.getElementById("historyContent").innerText = "Please login to view history.";
    return;
  }

  fetch("http://localhost:8081/getHistory?username=" + encodeURIComponent(username))
    .then(res => res.json())
    .then(data => {
      const container = document.getElementById("historyContent");
      if (!data || data.length === 0) {
        container.innerHTML = "<p>No history found.</p>";
        return;
      }
      container.innerHTML = data.map(entry => `
        <div class="history-item">
          <strong>${entry.algorithm}</strong> → [${entry.input_array}]<br/>
          <small>${entry.timestamp}</small>
        </div>
      `).join("<hr>");
    })
    .catch(() => {
      document.getElementById("historyContent").innerText = "❌ Failed to load history.";
    });
}

function generateArray(size = 30) {
  array = Array.from({ length: size }, () => Math.floor(Math.random() * 300) + 50);
  renderArray();
  comparisons = 0;
  swaps = 0;
  updateCounters();
  progressBar.style.width = "0%";
}

function renderArray(highlight = [], done = []) {
  arrayContainer.innerHTML = "";
  array.forEach((value, index) => {
    const bar = document.createElement("div");
    bar.className = "bar";
    bar.style.height = `${value}px`;
    bar.style.backgroundColor = done.includes(index)
      ? "green"
      : highlight.includes(index)
      ? "red"
      : "var(--accent)";
    arrayContainer.appendChild(bar);
  });
}

function updateCounters() {
  comparisonsEl.innerText = comparisons;
  swapsEl.innerText = swaps;
}

function startSorting() {
  if (!algorithmSelect.value) return alert("Please select a sorting algorithm.");
  isPaused = false;
  isReset = false;
  stepMode = stepToggle.checked;

  fetch("http://localhost:8081/sort", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ array, algorithm: algorithmSelect.value })
  })
    .then(res => res.json())
    .then(data => {
      sortingSteps = data.steps;
      currentStep = 0;

      fetch("http://localhost:8081/saveHistory", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: localStorage.getItem("loggedInUser") || "Guest",
          algorithm: algorithmSelect.value,
          input_array: array
        })
      });

      runSortingSteps();
    })
    .catch(() => alert("❌ Backend error. Is port 8081 running?"));
}

function runSortingSteps() {
  if (currentStep >= sortingSteps.length || isPaused || isReset) {
    if (!isReset && currentStep >= sortingSteps.length) {
      renderArray([], array.map((_, i) => i));
    }
    return;
  }

  const prevArray = [...array];
  array = sortingSteps[currentStep++];

  const swapped = array
    .map((val, i) => (val !== prevArray[i] ? i : -1))
    .filter(i => i >= 0);

  clickSound.play();
  if (swapped.length) {
    swapSound.play();
    swaps++;
  }

  comparisons++; // ✅ always increment comparisons

  renderArray(swapped);
  updateCounters();
  progressBar.style.width = `${(currentStep / sortingSteps.length) * 100}%`;

  if (!stepMode) {
    setTimeout(runSortingSteps, speed);
  }
}


function pauseSorting() { isPaused = true; }
function resumeSorting() { if (isPaused) { isPaused = false; runSortingSteps(); } }
function stopSorting() { isReset = true; sortingSteps = []; progressBar.style.width = "0%"; alert("Sorting stopped."); }
function resetSorting() { generateArray(); }

function applyManualInput() {
  try {
    const input = JSON.parse(manualInput.value);
    if (!Array.isArray(input)) throw new Error();
    array = input;
    renderArray();
  } catch {
    alert("Invalid array. Use format: [10, 20, 30]");
  }
}

function handleFileUpload(event) {
  const file = event.target.files[0]; // ✅ Get the selected file
  if (!file) {
    alert("❌ No file selected.");
    return;
  }

  const reader = new FileReader();
  reader.onload = e => {
    try {
      const uploaded = JSON.parse(e.target.result);
      if (!Array.isArray(uploaded)) throw new Error("Not an array.");
      array = uploaded;
      renderArray(); // ✅ Update bars
    } catch {
      alert("❌ Invalid file content. Must be a plain array like [10, 20, 30]");
    }
  };

  reader.readAsText(file); // ✅ Make sure file is passed here
}


function nextStep() {
  if (currentStep < sortingSteps.length) {
    const prevArray = [...array];
    array = sortingSteps[currentStep++];
    const swapped = array.map((val, i) => (val !== prevArray[i] ? i : -1)).filter(i => i >= 0);
    renderArray(swapped);
    progressBar.style.width = `${(currentStep / sortingSteps.length) * 100}%`;
    updateCounters();
  } else {
    renderArray([], array.map((_, i) => i));
  }
}

function displayAlgorithmInfo(name) {
  const info = {
      "Bubble Sort": {
        time: "Best: O(n), Avg/Worst: O(n²)", space: "O(1)",
        desc: "Bubble Sort repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. This process is repeated until the list is sorted. Though simple, it is inefficient for large datasets."
      },
      "Insertion Sort": {
        time: "Best: O(n), Avg/Worst: O(n²)", space: "O(1)",
        desc: "Insertion Sort builds the sorted list one element at a time by inserting each new item into its correct position. It works efficiently for small datasets or nearly sorted arrays."
      },
      "Selection Sort": {
        time: "Best/Worst/Avg: O(n²)", space: "O(1)",
        desc: "Selection Sort repeatedly selects the smallest (or largest) element from the unsorted portion and places it in the correct position. It performs well on small lists but is generally inefficient."
      },
      "Merge Sort": {
        time: "Best/Worst/Avg: O(n log n)", space: "O(n)",
        desc: "Merge Sort uses a divide-and-conquer strategy to split the list into halves, sort them recursively, and then merge the sorted halves. It's efficient, stable, and works well on large datasets."
      },
      "Quick Sort": {
        time: "Best/Avg: O(n log n), Worst: O(n²)", space: "O(log n)",
        desc: "Quick Sort picks a pivot element and partitions the array around it such that smaller elements go left and larger go right. It is very fast on average but can degrade to O(n²) with poor pivots."
      },
      "Heap Sort": {
        time: "Best/Worst/Avg: O(n log n)", space: "O(1)",
        desc: "Heap Sort builds a binary heap and repeatedly removes the largest element to build the sorted array. It offers good performance and does not require additional memory like Merge Sort."
      },
      "Shell Sort": {
        time: "Best: O(n log n), Worst: O(n²)", space: "O(1)",
        desc: "Shell Sort is an optimization over Insertion Sort. It sorts elements at a specific gap apart and gradually reduces the gap, leading to better performance on larger arrays."
      },
      "Radix Sort": {
        time: "O(nk), where k = max digit length", space: "O(n + k)",
        desc: "Radix Sort sorts numbers digit by digit starting from the least significant to the most significant digit using a stable sort like Counting Sort. It is efficient for integers and fixed-length data."
      }
    };
    

  const selected = info[name];
  algoInfoBox.innerHTML = selected
    ? `<h3>${name}</h3><p><strong>Time Complexity:</strong> ${selected.time}</p><p><strong>Space Complexity:</strong> ${selected.space}</p><p>${selected.desc}</p>`
    : "";
}

function toggleTheme() {
  const isLight = document.body.classList.toggle("light");
  themeToggle.innerText = isLight ? "🌙" : "☀️";
}

let isSignup = false;
function showAuth() {
  document.getElementById('authPopup').classList.remove('hidden');
  updateAuthForm();
}
function closeAuth() {
  document.getElementById('authPopup').classList.add('hidden');
  document.getElementById('authForm').reset();
  document.getElementById('authMessage').innerText = '';
}
function toggleAuthMode() {
  isSignup = !isSignup;
  updateAuthForm();
}
function updateAuthForm() {
  document.getElementById('authTitle').innerText = isSignup ? "User Signup" : "User Login";
  document.getElementById('authSubmitBtn').innerText = isSignup ? "Sign Up" : "Login";
  document.querySelector('.auth-actions button[type="button"]').innerText = isSignup ? "Switch to Login" : "Switch to Signup";
  document.getElementById('authEmail').classList.toggle('hidden', !isSignup);
  document.getElementById('authConfirmPassword').classList.toggle('hidden', !isSignup);
}
function userLogin() {
  const username = document.getElementById('authUsername').value.trim();
  const password = document.getElementById('authPassword').value.trim();

  fetch("http://localhost:8081/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        localStorage.setItem('loggedInUser', username);
        renderLoggedInUI(username);
        closeAuth();
      } else {
        document.getElementById('authMessage').innerText = "Invalid credentials ❌";
      }
    });
}
function userSignup() {
  const username = document.getElementById('authUsername').value.trim();
  const email = document.getElementById('authEmail').value.trim();
  const password = document.getElementById('authPassword').value.trim();
  const confirm = document.getElementById('authConfirmPassword').value.trim();

  if (password !== confirm) {
    document.getElementById('authMessage').innerText = "❌ Passwords do not match";
    return;
  }

  fetch("http://localhost:8081/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password, email })
  })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        localStorage.setItem('loggedInUser', username);
        renderLoggedInUI(username);
        closeAuth();
      } else {
        document.getElementById('authMessage').innerText = "❌ Username already taken";
      }
    });
}
function logoutUser() {
  localStorage.removeItem('loggedInUser');
  const navRight = document.querySelector(".nav-right");
  navRight.innerHTML = `
    <button id="themeToggle">🌙</button>
    <a href="#about" class="nav-link">About Us</a>
    <button class="auth-nav-btn" onclick="showAuth()">Login / Signup</button>
  `;
  themeToggle = document.getElementById('themeToggle');
  themeToggle.addEventListener("click", toggleTheme);
}



