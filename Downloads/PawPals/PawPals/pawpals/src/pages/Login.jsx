import { useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

export default function Login() {
  const { login } = useAuth();
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post("/auth/login", form);
      const token = res.data.access_token;
      const payload = JSON.parse(atob(token.split(".")[1]));
      const role = payload.role;

      login(token, role);
    } catch (err) {
      setError("⚠️ Username atau password salah");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-purple-100 px-4">
      <div className="card w-full max-w-md bg-white shadow-xl border border-base-300">
        <div className="card-body">
          <h2 className="text-2xl font-bold mb-4 text-center">Masuk ke Akun Anda</h2>

          {error && (
            <div className="alert alert-error p-2 text-sm rounded mb-2">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <input
              type="text"
              name="username"
              placeholder="Username"
              className="input input-bordered w-full"
              onChange={handleChange}
              required
            />
            <input
              type="password"
              name="password"
              placeholder="Password"
              className="input input-bordered w-full"
              onChange={handleChange}
              required
            />
            <button type="submit" className="btn btn-primary w-full">
              Login
            </button>
          </form>

          <p className="text-sm mt-4 text-center">
            Belum punya akun?{" "}
            <Link to="/register" className="text-blue-500 font-medium hover:underline">
              Daftar sekarang
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
