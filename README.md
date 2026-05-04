# 🚀 Code Review AI Platform

An end-to-end full-stack application that analyzes GitHub repositories, detects code quality issues, and provides AI-powered suggestions for improvement.

---

## 🔍 Overview

This project allows users to submit a GitHub repository URL and receive automated code review insights.

The system performs:
- Static code analysis  
- Code smell detection  
- Security issue identification  
- AI-based suggestions for improvement  

It combines rule-based analyzers with AI to deliver actionable feedback.

---

## 🏗️ Project Structure

codereview/
 ├── codereview        # Backend (Spring Boot)
 └── codereview-ui     # Frontend (React + Vite)

---

## ⚙️ Tech Stack

### Backend
- Java  
- Spring Boot  
- Maven  

### Frontend
- React  
- Vite  
- JavaScript  

### Other
- GitHub repository scanning  
- AI integration (Gemini API)  

---

## 🚀 Features

- 🔗 Analyze any public GitHub repository  
- 🧠 AI-powered code improvement suggestions  
- 🔍 Detect:
  - Long methods  
  - Large files  
  - Deep nesting  
  - TODO comments  
  - Hardcoded secrets  
- ⚡ Fast and scalable processing  

---

## 🛠️ How to Run

### 1. Clone Repository
git clone https://github.com/Sijin-Kuriakose/codereview.git  
cd codereview  

---

### 2. Run Backend
cd codereview  
./mvnw spring-boot:run  

Backend runs at:  
http://localhost:8080  

---

### 3. Run Frontend
cd codereview-ui  
npm install  
npm run dev  

Frontend runs at:  
http://localhost:5173  

---

## 📡 API

### Analyze Repository
POST /analyze  

Request Body:
{
  "repoUrl": "https://github.com/user/repository"
}

---

## 🧠 How It Works

1. User submits a GitHub repository URL  
2. Backend clones and scans the repository  
3. Code is analyzed using rule-based checks  
4. AI generates suggestions  
5. Results are returned and displayed in UI  

---

## 📌 Future Improvements

- Support private repositories  
- Advanced AI-based analysis  
- Performance optimization for large repositories  
- User authentication and history  
- Exportable reports  

---

## 👨‍💻 Author

Sijin Kuriakose
