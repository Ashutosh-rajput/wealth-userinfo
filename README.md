<h1 style="display: flex; gap: 20px;">
  <img src="https://i.postimg.cc/Vk6NxD9p/Wealth-Arcbig.png" alt="WealthArc Logo" width="200" style="align-self: flex-start; margin-top: 35px;"/>
  <span style="align-self: center;">Your Intelligent Financial Command Center 💰📊</span>
</h1>

## 📖 Table of Contents

*   [ justifications for Technology Choices](#justifications-for-technology-choices)
*   [🌟 Introduction](#introduction)
*   [🚀 Key Features and Functionality](#key-features-and-functionality)
    *   [💸 Expense Tracking](#expense-tracking)
    *   [📈 Earning Tracking](#earning-tracking)
    *   [🎯 Budgeting](#budgeting)
    *   [🔁 Subscription Management](#subscription-management)
    *   [📄 AI-Powered DocAI Processor](#ai-powered-docai-processor)
    *   [🎨 User Interface (UI) and User Experience (UX)](#user-interface-ui-and-user-experience-ux)
*   [🛠️ Technology Stack](#technology-stack)
*   [🏗️ Technical Architecture](#technical-architecture)
    *   [Microservice Breakdown](#microservice-breakdown)
    *   [Supporting Infrastructure Components](#supporting-infrastructure-components)
*   [🔐 Security: JWT-Based Authentication & Authorization](#security-jwt-based-authentication--authorization)
    *   [Authentication Flow](#authentication-flow)
    *   [Authorization Flow](#authorization-flow)
    *   [Benefits of JWT](#benefits-of-jwt)
    *   [Security Considerations](#security-considerations)
*   [⚙️ Running Wealth Manager (Kubernetes Deployment)](#running-wealth-manager-kubernetes-deployment)
    *   [Overview](#overview)
    *   [Prerequisites](#prerequisites)
    *   [Assumed Directory Structure](#assumed-directory-structure)
    *   [Deployment Steps](#deployment-steps)
    *   [Verify Deployments](#verify-deployments)
    *   [Accessing the Application](#accessing-the-application)
    *   [Optional: CI/CD Pipeline with Jenkins](#optional-cicd-pipeline-with-jenkins)
    *   [Important Notes](#important-notes)
*   [💡 Future Scope and Roadmap](#future-scope-and-roadmap)


# WealthArc: Scalable Microservices-Based Personal Finance Platform

This report details the development of **WealthArc**, a modern personal finance application designed to help users efficiently manage their income, expenses, budgets, and subscriptions.

Built using a **scalable, resilient microservices architecture** deployed on **Google Cloud Platform (GCP)**, WealthArc focuses on delivering a secure, intuitive, and insightful financial management experience.

WealthArc places high priority on **data privacy**, **security**, and **user experience**, offering a complete view of one’s financial life while minimizing complexity and maximizing control.



---

## 🧠 Justifications for Technology Choices

| Technology | Reason |
|------------|--------|
| **Spring Boot (Java)** | Mature, robust, scalable for backend microservices with REST, DB, and security support |
| **ReactJS** | Component-based, fast UI for web frontend |
| **Apache Kafka** | Asynchronous messaging, event-driven, high-throughput |
| **Redis** | Fast in-memory caching, session/data optimization |
| **Google Sign-In** | Seamless and secure authentication via OAuth 2.0 with trusted identity provider |
| **Nimbus JOSE + JWT** | Secure, stateless token validation with embedded claims; eliminates extra DB calls per request |
| **MongoDB** | Schema-flexible, scalable NoSQL DB for user-centric data |
| **Docker** | Consistent containerization across environments |
| **Kubernetes (GKE)** | Orchestrated microservice management, high availability |
| **Jenkins** | CI/CD pipeline automation |
| **GCP (Google Cloud Platform)** | Scalable cloud infrastructure, managed Kubernetes, AI tools |

---


## 🌟 Introduction

Wealth Manager is a microservices-based application designed to provide users with an intelligent and comprehensive command center for their personal finances. Our core value propositions are:

*   **Simplified Expense and Earning Tracking:** Effortlessly log and categorize income and expenses, with options for manual entry and AI-powered automated data extraction via our DocAI Processor.
*   **Dynamic Budgeting:** Create flexible budgets aligned with financial goals and track progress in real-time.
*   **Insightful Subscription Management:** Centralize and analyze subscription spending, with AI-driven insights to identify potential savings.
*   **Secure & Scalable Platform:** Built with modern, resilient technologies ensuring data privacy and a robust user experience.

## 🚀 Key Features and Functionality

### 💸 Expense Tracking

*   **Manual Expense Entry:** Intuitive forms for quickly logging expenses with details like amount, date, merchant, category, and optional notes.
*   **Automated Expense Categorization:** AI-powered categorization engine that automatically assigns categories to transactions, learning user preferences over time.
*   **Transaction History and Filtering:** View detailed expense history, filter by date range, category, merchant, and tags for comprehensive analysis.
*   **Expense Grouping & Tagging:** Users can create custom categories and tags to further organize expenses for personalized reporting and analysis (e.g., "Project: Home Renovation," "Vacation - Europe").

### 📈 Earning Tracking

*   **Manual Earning Entry:** Similar to expense tracking, users can easily log income with source, amount, date, category, and notes.
*   **Income Source Categorization:** Categorize income sources (Salary, Freelance, Investments, etc.) for clear income stream analysis.
*   **Income History and Reporting:** Track income trends, visualize earning patterns, and generate reports on income sources over time.

### 🎯 Budgeting

*   **Budget Creation:** Set up flexible budgets for different categories (e.g., monthly food budget, yearly travel budget).
*   **Goal-Based Budgeting:** Align budgets with financial goals (e.g., "Save for a down payment," "Pay off debt").
*   **Progress Tracking:** Visually track budget progress in real-time, see remaining budget balances, and identify potential overspending areas.

### 🔁 Subscription Management

*   **Subscription List Tracking:** Centralized list of all active subscriptions (e.g., streaming services, SaaS tools, memberships).
*   **Subscription Spending Analysis:** Visualize total subscription spending, identify high-cost subscriptions, and analyze subscription trends.
*   **Unused Subscription Identification:** AI can identify potentially unused subscriptions based on spending patterns and usage data (if integrated with external services in the future).
*   **Cancellation Reminders and Nudges:** Optional reminders for subscription renewals and gentle nudges to consider canceling underutilized services.

### 📄 AI-Powered DocAI Processor
*   **Automated Data Entry:** Seamlessly processes uploaded receipts, invoices, and bank statements to automatically extract expense and income details, minimizing manual data entry.

### 🎨 User Interface (UI) and User Experience (UX)

*   **ReactJS Web Application:** Modern, responsive, and user-friendly web interface built with ReactJS for desktop and mobile browser access.
*   **Intuitive Navigation and Design:** Clean, well-organized interface for easy navigation and access to all features.
*   **Data Visualization:** Charts, graphs, and dashboards to present financial data in a visually engaging and easy-to-understand manner.

## 🛠️ Technology Stack

*   **Backend:** Spring Boot (Java)
*   **Frontend:** ReactJS (JavaScript)
*   **Mobile (Future):** Flutter (Dart)
*   **Messaging Queue:** Apache Kafka
*   **Caching:** Redis
*   **Database:** MongoDB
*   **Containerization:** Docker
*   **Orchestration:** Kubernetes (on GCP GKE)
*   **CI/CD:** Jenkins
*   **Cloud Platform:** Google Cloud Platform (GCP)
*   **AI/ML Models:** BERT, DeepSeek, SpaCy, Custom Models (Python-based)

## 🏗️ Technical Architecture

Wealth Manager is designed as a microservices-based application to ensure scalability, maintainability, and independent deployability of different functionalities. JWT (JSON Web Tokens) are used for secure authentication and authorization across the microservice ecosystem.

### Microservice Breakdown

1.  **Auth Service (Spring Boot)**
    *   Manages user authentication and authorization.
    *   Handles user registration, login, and password management.
    *   Generates and validates JWTs.
    *   Interacts with the User Database.
    *   **Tech Stack:** Spring Boot, Spring Security (for JWT), Spring Data JPA.
    *   **Repository:** [https://github.com/Ashutosh-rajput/wealth-userinfo](https://github.com/Ashutosh-rajput/wealth-userinfo)

2.  **Expense Service (Spring Boot)**
    *   Manages all operations related to expense tracking (CRUD).
    *   Handles expense categorization logic (potentially using AI Service or rule-based engine).
    *   Provides APIs for expense data, filtering, and reporting.
    *   Interacts with the Expense Database.
    *   **Tech Stack:** Spring Boot, Spring Data JPA, (potentially Spring Cloud Stream for Kafka).
    *   **Repository:** [https://github.com/Ashutosh-rajput/wealth-expense](https://github.com/Ashutosh-rajput/wealth-expense)

3.  **Nexus Service (Spring Boot)**
    *   Handles asynchronous operations like sending emails, notifications, and document processing coordination.
    *   **Repository:** [https://github.com/Ashutosh-rajput/Nexus](https://github.com/Ashutosh-rajput/Nexus)

4.  **Frontend Service (ReactJS)**
    *   Provides the user interface for the web application.
    *   Built with ReactJS for a dynamic and responsive experience.
    *   **Repository:** [https://github.com/Ashutosh-rajput/wealtharc-frontend](https://github.com/Ashutosh-rajput/wealtharc-frontend) (Note: Repository name still contains "wealtharc")

5.  **Databases (MongoDB)**
    *   **User Database:** Stores user credentials, profiles, and potentially user preferences.
    *   **Service-Specific Databases:** Stores persistent data for Expense, Earning, Budget, and Subscription services respectively.
    *   MongoDB is chosen for its scalability, flexibility, and performance, suitable for diverse data models across services.

### Supporting Infrastructure Components

*   **Redis Cache:** Used for caching frequently accessed data (e.g., user sessions, API responses, processed DocAI data) to improve performance and reduce database load.
*   **Apache Kafka (Message Queue):** Enables asynchronous communication and event-driven architecture between microservices.
*   **Jenkins:** Implements CI/CD pipelines for automated building, testing, and deployment of microservices.

## 🔐 Security: Google Sign-In & Nimbus JWT-Based Authentication

Wealth Manager implements secure, modern authentication using **Google Sign-In** and **Nimbus JOSE + JWT** for API authorization. This ensures high performance, reduced server load, and seamless user login experience.

### 🔑 Authentication Flow

1. **User Login via Google:**
   - ReactJS/Flutter frontend integrates Google Sign-In.
   - On login, the Google ID token is retrieved client-side.

2. **Token Exchange & JWT Generation:**
   - The frontend sends the Google ID token to the Auth Service.
   - Auth Service validates the Google token using Google's public keys.
   - On success, Auth Service generates a **custom JWT** using **Nimbus JOSE + JWT**.
   - The signed JWT is returned to the client and securely stored (e.g., HttpOnly cookie or secure storage).

3. **No Repeated DB Lookups:**
   - User data is embedded in the JWT as claims.
   - Thanks to Nimbus JWT’s full cryptographic verification, **no DB call is required** to validate the user on each request — unlike standard implementations where DB lookup may occur for session validation or user role resolution.

---

### 🔒 Authorization Flow

1. For protected API calls, the frontend includes:
   ```http
   Authorization: Bearer <JWT>
   ```
2. API Gateway (or backend services) verifies the JWT:
   - Validates signature
   - Checks expiration and issuer
   - Parses claims (e.g., userId, roles) directly from the token

3. On successful validation, request is processed with the associated user context — no DB access needed during this flow.

---

### ✅ Benefits of Using Nimbus JOSE + JWT

* **Zero DB Overhead:** All necessary user info is encoded securely in JWT claims — reducing latency and improving scalability.
* **Stateless & Scalable:** Ideal for microservice environments; no session state management required.
* **Strong Security:** Nimbus provides support for advanced JWT signing and encryption algorithms.
* **Interoperable with Google OAuth:** Supports ID token verification from Google seamlessly.
* **Standard Compliant:** Fully compliant with JOSE and JWT specifications.


> 🧠 *Using Nimbus JWT significantly enhances API efficiency by removing unnecessary DB calls — a crucial advantage in distributed microservice systems.*


## ⚙️ Running Wealth Manager (Kubernetes Deployment)

This guide provides instructions on how to deploy and run the Wealth Manager application using Kubernetes. You can find the project's Kubernetes files in this repository: [https://github.com/Ashutosh-rajput/Kubernetes-files](https://github.com/Ashutosh-rajput/Kubernetes-files)

### Overview

Wealth Manager is a microservices-based personal finance application. To run it, you will need to deploy several services into a Kubernetes cluster, along with its dependencies: Kafka and Redis. An optional CI/CD pipeline using Jenkins can also be set up.

The core application services are:
*   **`userinfo`**: Manages user accounts, authentication, and authorization.
*   **`expense`**: Handles all other backend APIs related to expenses, earnings, budgets, and subscriptions.
*   **`nexus`**: Handles asynchronous operations like sending emails and document processing.
*   **`frontend`**: The ReactJS user interface.
*   **`mongo`**: The MongoDB database instance.

### Prerequisites
*   A running Kubernetes cluster (e.g., Minikube, Kind, Docker Desktop K8s, GKE, EKS, AKS).
*   `kubectl` command-line tool configured to communicate with your cluster.
*   (Optional) Docker installed if you need to build images locally.
*   Access to the project repository containing the Kubernetes manifest files.
*   **Create Namespaces**: Ensure the following namespaces are created in your cluster:
    ```bash
    kubectl create namespace backend
    kubectl create namespace frontend
    kubectl create namespace database-namespace
    kubectl create namespace kafka-namespace
    ```
## 📁 Kubernetes Directory Structure

```
kubernetes-files/
├── userinfo-deployment.yaml
├── expense-deployment.yaml
├── frontend-deployment.yaml
├── mongo-statefulset.yaml  # Or mongo-deployment.yaml as used in commands
├── nexus-deployment.yaml
│
└── common/  # Or potentially separate directories for Kafka/Redis
    ├── kafka-deployment.yaml
    ├── zookeeper-deployment.yaml
    ├── redis-deployment.yaml
    └── jenkins-deployment.yaml  # Use for Docker; for GKE use GCP Jenkins YAMLs
```

---

## 🚀 Deployment Steps

### 1. Deploy External Dependencies (Kafka & Redis)

Deploy Kafka and Redis into your Kubernetes cluster. If manifests are in `common/`:

```bash
# Deploy Zookeeper and Kafka
kubectl apply -f common/zookeeper-deployment.yaml -n kafka-namespace
kubectl apply -f common/kafka-deployment.yaml -n kafka-namespace

# Deploy Redis
kubectl apply -f common/redis-deployment.yaml -n database-namespace  # Or a dedicated namespace
```

> 🔧 *Adjust paths and namespaces according to your file structure and naming preferences.*

---

### 2. Deploy Core Application Services

Apply the Kubernetes manifest files for each Wealth Manager service in the recommended order:

#### a. Deploy Database (MongoDB)
```bash
kubectl apply -f mongo-deployment.yaml -n database-namespace  
```

#### b. Deploy Backend Services
```bash
kubectl apply -f userinfo-deployment.yaml -n backend
kubectl apply -f expense-deployment.yaml -n backend
kubectl apply -f nexus-deployment.yaml -n backend
```

#### c. Deploy Frontend
```bash
kubectl apply -f frontend-deployment.yaml -n frontend
```

> ⚠️ *Ensure the frontend manifest is configured to connect to the API Gateway or backend services (`userinfo`, `expense`, `nexus`) using Kubernetes service DNS names.*

---

## ✅ Verify Deployments

Check status of all components:

```bash
kubectl get pods -A
kubectl get services -A
kubectl get deployments -A
kubectl get statefulsets -A  # If using StatefulSets for MongoDB
```

Check logs for runtime issues:

```bash
kubectl logs -f <pod-name> -n <namespace>
```

> 🟢 Ensure all pods are in `Running` state and desired replicas are available.

---

## 🌐 Accessing the Application

Once the frontend service is running and exposed:

```bash
kubectl get svc frontend-service -n frontend  # Replace with actual service name
```

- **If using LoadBalancer:** Look for `EXTERNAL-IP` and open in your browser.
- **If using NodePort:** Combine `minikube ip` or node IP with the exposed port.
- **If using Ingress:** Use the configured hostname.

---

## 🔄 Optional: CI/CD Pipeline with Jenkins

### For GCP Kubernetes (GKE):

```bash
cd gcp-jenkins/  # Path within the Kubernetes-files repo
# Follow GKE-specific Jenkins setup instructions
```

- Configure Jenkins with appropriate GCP credentials.
- Use a provided `Jenkinsfile` for automated build/deploy.

### For Docker-based Jenkins:

```bash
cd common/jenkins/
kubectl apply -f jenkins-deployment.yaml -n <your-ci-cd-namespace>
```

> ⚙️ *Ensure Jenkins has access to Docker registry, Kubernetes credentials, and your source code repo.*

---

## 📌 Important Notes

- **Configuration:** Use ConfigMaps and Secrets to supply MongoDB, Kafka, Redis connection details, API keys, and other config data.
- **Namespaces:** Use consistent namespaces across manifests and commands.
- **Private Registries:** Set up `imagePullSecrets` in YAML files for private Docker images.
- **Persistent Volumes:** Ensure MongoDB (`mongo-statefulset.yaml`) has properly configured `PVCs` and `PVs`.

---

## 🔭 Future Scope and Roadmap

Wealth Manager will continue to evolve. Planned features include:

- **Flutter Mobile App:** Native iOS/Android experience from a single codebase.
- **Advanced AI Features:**
  - Predictive budgeting and forecasting
  - Personalized investment recommendations
  - Sentiment-driven financial insights
- **Chatbot Integration:** Real-time financial assistance via conversational UI
- **Financial Advisor Portal:** Secure dashboard access for advisors
- **Institution Integration:** Bank/brokerage API integrations (e.g., Plaid)
- **Ethical Spending Analysis:** Environmental and social impact tracking
- **Gamification and Social Features:** Enhance user engagement
- **Enhanced DocAI Processor:** Better extraction from uploaded documents


## 📋 Deployment & Infrastructure Summary

- **Platform:** GCP (Google Cloud Platform)
- **Container Management:** Docker
- **Orchestration:** Kubernetes via GKE
- **CI/CD:** Jenkins
- **Observability:** GCP Cloud Monitoring & Logging
- **Networking:** GCP Load Balancer
- **Security:** GCP IAM & best practices
