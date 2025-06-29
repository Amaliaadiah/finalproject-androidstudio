import { Routes, Route } from "react-router-dom"
import Home from "./pages/Home"
import Login from "./pages/Login"
import Register from "./pages/Register"
import { AuthProvider } from "./context/AuthContext"
import DashboardUser from "./pages/DashboardUser"
import DashboardAdmin from "./pages/DashboardAdmin" // ✅ Tambah ini
import ProtectedRoute from "./routes/ProtectedRoute"
import MyAdoptions from "./pages/MyAdoptions"

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/myadoptions" element={<MyAdoptions />} />

        {/* ✅ USER */}
        <Route path="/dashboard" element={
          <ProtectedRoute allowedRole="user">
            <DashboardUser />
          </ProtectedRoute>
        }/>

        {/* ✅ ADMIN */}
        <Route path="/admin" element={
          <ProtectedRoute allowedRole="admin">
            <DashboardAdmin />
          </ProtectedRoute>
        }/>
      </Routes>
    </AuthProvider>
  )
}

export default App
