# sorting-algorithms-visualizer
Sorting Algorithms Visualizer is a feature-rich educational web application that demonstrates how different sorting algorithms work through animation. It supports custom input, login/signup, history tracking, step-by-step execution, and responsive UI, making it ideal for both learning and presentation purposes.

This project is a web-based application that helps users understand and compare different sorting algorithms through interactive visualization. The goal is to make learning algorithms more engaging and practical by letting users see the internal process of sorting step by step.

---

## 1. Features

1. Supports 8 sorting algorithms: Bubble, Insertion, Selection, Merge, Quick, Heap, Shell, and Radix Sort
2. Step-by-step sorting animation for better understanding
3. User login and signup functionality
4. Sorting history is saved and can be viewed after login
5. Dark and light theme toggle
6. Manual array input and JSON file upload support
7. Counters for total comparisons and swaps during sorting
8. Sorting control options like start, pause, resume, stop, and reset
9. Adjustable speed slider to control animation pace
10. Fully responsive user interface across devices

---

## 2. Technologies Used

- Frontend: HTML, CSS, JavaScript
- Backend: Java (HttpServer)
- Database: MySQL (via JDBC)
- Deployment:
  - Frontend on Render:  [Visit Live App](https://sorting-algorithms-visualizer-2380.onrender.com) 
  - Backend not deployed yet

---

## 3. Project Flow

1. The user selects a sorting algorithm from the dropdown.
2. The user can either enter an array manually or upload a JSON file.
3. When "Start" is clicked, a request is sent to the backend with the input array and selected algorithm.
4. The backend returns all sorting steps.
5. These steps are animated on the frontend.
6. If the user is logged in, sorting details (algorithm, input, timestamp) are stored in the database.
7. The user can later view their personal sorting history.

---

## 4. JSON Input Format

You can upload a file named `input.json` with the following content as an example:

```json
[25, 10, 45, 5, 30]


                                          

