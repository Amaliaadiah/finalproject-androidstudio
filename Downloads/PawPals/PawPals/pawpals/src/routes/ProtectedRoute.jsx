import { Navigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

export default function ProtectedRoute({ children, allowedRole }) {
  const { user } = useAuth()

  if (allowedRole && user.role !== allowedRole) {
    return <Navigate to="/" replace />
  }

  return children
}
