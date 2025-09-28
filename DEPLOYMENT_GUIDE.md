# 🚀 CSI Backend & Frontend Deployment Guide

## 📋 **Prerequisites**
- ✅ Supabase database configured
- ✅ Backend running locally
- ✅ Frontend running locally

## 🎯 **Deployment Options**

### **Option 1: Render (Recommended - Free)**
- **Backend**: Spring Boot on Render
- **Frontend**: Static site on Render
- **Database**: Supabase (already configured)

### **Option 2: Railway**
- **Backend**: Spring Boot on Railway
- **Frontend**: Static site on Railway

### **Option 3: Vercel + Railway**
- **Backend**: Railway
- **Frontend**: Vercel

---

## 🚀 **Deploy to Render (Step-by-Step)**

### **Step 1: Deploy Backend**

1. **Go to [render.com](https://render.com)**
2. **Sign up/Login**
3. **Click "New +" → "Web Service"**
4. **Connect your GitHub repository**
5. **Configure Backend:**
   - **Name**: `csi-backend`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`
   - **Port**: `8080`

6. **Set Environment Variables:**
   ```
   DATABASE_HOST=aws-1-ap-southeast-1.pooler.supabase.com
   DATABASE_PORT=5432
   DATABASE_NAME=postgres
   DATABASE_USER=postgres.qzmnsrankbpqnuaxoyhp
   DATABASE_PASSWORD=your-actual-database-password
   EMAIL_USERNAME=contactus.csirscoe@gmail.com
   EMAIL_PASSWORD=your-actual-email-password
   PORT=8080
   ```

7. **Click "Create Web Service"**
8. **Wait for deployment** (5-10 minutes)
9. **Copy the backend URL** (e.g., `https://csi-backend.onrender.com`)

### **Step 2: Deploy Frontend**

1. **In Render dashboard, click "New +" → "Static Site"**
2. **Connect your GitHub repository**
3. **Configure Frontend:**
   - **Name**: `csi-frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`

4. **Set Environment Variables:**
   ```
   VITE_API_URL=https://your-backend-url.onrender.com
   ```

5. **Click "Create Static Site"**
6. **Wait for deployment** (3-5 minutes)
7. **Copy the frontend URL** (e.g., `https://csi-frontend.onrender.com`)

---

## 🚀 **Deploy to Railway (Alternative)**

### **Backend Deployment**

1. **Go to [railway.app](https://railway.app)**
2. **Sign up/Login**
3. **Click "New Project"**
4. **Select "Deploy from GitHub repo"**
5. **Choose your backend repository**
6. **Railway will auto-detect Java and deploy**
7. **Set environment variables in Railway dashboard**
8. **Get the backend URL**

### **Frontend Deployment**

1. **Create new Railway project**
2. **Connect frontend repository**
3. **Set build command**: `npm install && npm run build`
4. **Set start command**: `npx serve dist`
5. **Set environment variables**
6. **Deploy**

---

## 🔧 **Environment Variables Summary**

### **Backend Environment Variables:**
```bash
DATABASE_HOST=aws-1-ap-southeast-1.pooler.supabase.com
DATABASE_PORT=5432
DATABASE_NAME=postgres
DATABASE_USER=postgres.qzmnsrankbpqnuaxoyhp
DATABASE_PASSWORD=your-actual-database-password
EMAIL_USERNAME=contactus.csirscoe@gmail.com
EMAIL_PASSWORD=your-actual-email-password
PORT=8080
```

### **Frontend Environment Variables:**
```bash
VITE_API_URL=https://your-backend-url.onrender.com
```

---

## ✅ **Post-Deployment Checklist**

- [ ] Backend deployed and accessible
- [ ] Frontend deployed and accessible
- [ ] Database connection working
- [ ] API endpoints responding
- [ ] Frontend can communicate with backend
- [ ] File uploads working
- [ ] Authentication working

---

## 🆘 **Troubleshooting**

### **Common Issues:**

1. **Backend not starting:**
   - Check environment variables
   - Check build logs
   - Verify database connection

2. **Frontend not loading:**
   - Check VITE_API_URL
   - Verify build process
   - Check console for errors

3. **Database connection issues:**
   - Verify Supabase credentials
   - Check network connectivity
   - Verify SSL settings

### **Support:**
- Check deployment logs
- Verify environment variables
- Test API endpoints manually

---

## 🎉 **Success!**

Once deployed, you'll have:
- ✅ **Backend API**: `https://your-backend.onrender.com`
- ✅ **Frontend Website**: `https://your-frontend.onrender.com`
- ✅ **Database**: Supabase PostgreSQL
- ✅ **Full Stack Application**: Ready for production!

**Your CSI website will be live and accessible to users worldwide!** 🌍
