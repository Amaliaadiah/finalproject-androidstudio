import { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [redirectRole, setRedirectRole] = useState(null); // Tambahan
  const navigate = useNavigate();

  // Cek localStorage saat pertama kali load
  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (token && role) {
      setUser({ token, role });
    }
  }, []);

  // Redirect setelah login berdasarkan role
  useEffect(() => {
    if (redirectRole) {
      if (redirectRole === "admin") {
        navigate("/admin");
      } else {
        navigate("/dashboard");
      }
      setRedirectRole(null); // Reset setelah redirect
    }
  }, [redirectRole, navigate]);

  const login = (token, role) => {
    localStorage.setItem("token", token);
    localStorage.setItem("role", role);
    setUser({ token, role });
    setRedirectRole(role); // Set role untuk redirect nanti
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
