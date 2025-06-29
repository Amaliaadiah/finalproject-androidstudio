import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { pathname } = location;

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const isActive = (path) => pathname === path;

  return (
    <div className="navbar bg-base-100 shadow-md px-4 md:px-8">
      {/* Logo / Brand */}
      <div className="flex-1">
        <Link
          to="/"
          className="text-2xl font-extrabold text-primary tracking-wide"
        >
          PawPals <span className="text-lg">🐾</span>
        </Link>
      </div>

      {/* Navigation Buttons */}
      <div className="flex-none space-x-2">
        {user ? (
          <>
            {/* Admin */}
            {user.role === "admin" ? (
              <Link
                to="/admin"
                className={`btn btn-sm normal-case ${
                  isActive("/admin")
                    ? "btn-primary text-white"
                    : "btn-ghost"
                }`}
              >
                Kelola Adopsi
              </Link>
            ) : (
              <>
                {/* User biasa */}
                <Link
                  to="/"
                  className={`btn btn-sm normal-case ${
                    isActive("/") ? "btn-primary text-white" : "btn-ghost"
                  }`}
                >
                  Dashboard
                </Link>
                <Link
                  to="/dashboard"
                  className={`btn btn-sm normal-case ${
                    isActive("/dashboard")
                      ? "btn-primary text-white"
                      : "btn-ghost"
                  }`}
                >
                  Daftar Hewan
                </Link>
                <Link
                  to="/myadoptions"
                  className={`btn btn-sm normal-case ${
                    isActive("/myadoptions")
                      ? "btn-primary text-white"
                      : "btn-ghost"
                  }`}
                >
                  Status Adopsi
                </Link>
              </>
            )}

            {/* Logout untuk semua peran */}
            <button
              onClick={handleLogout}
              className="btn btn-outline btn-sm normal-case hover:btn-error"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <Link
              to="/"
              className={`btn btn-sm normal-case ${
                isActive("/") ? "btn-primary text-white" : "btn-ghost"
              }`}
            >
              Beranda
            </Link>
            <Link
              to="/login"
              className={`btn btn-sm normal-case ${
                isActive("/login")
                  ? "btn-primary text-white"
                  : "btn-outline hover:btn-primary"
              }`}
            >
              Login
            </Link>
            <Link
              to="/register"
              className={`btn btn-sm normal-case ${
                isActive("/register")
                  ? "btn-primary text-white"
                  : "btn-primary btn-outline"
              }`}
            >
              Gabung
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
