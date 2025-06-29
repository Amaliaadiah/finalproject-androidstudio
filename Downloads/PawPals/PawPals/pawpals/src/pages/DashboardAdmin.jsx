import { useEffect, useState } from "react";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/navbar"; // ✅ Import Navbar

export default function DashboardAdmin() {
  const { user } = useAuth();
  const [pets, setPets] = useState([]);
  const [adoptions, setAdoptions] = useState([]);
  const [form, setForm] = useState({
    name: "",
    type: "",
    age: "",
    description: "",
  });
  const [imageFile, setImageFile] = useState(null);
  const [editingPet, setEditingPet] = useState(null);

  const headers = { Authorization: `Bearer ${user?.token}` };

  useEffect(() => {
    if (user?.token) getAllData();
  }, []);

  const getAllData = async () => {
    try {
      const [resPets, resAdoptions] = await Promise.all([
        api.get("/pets", { headers }),
        api.get("/adoptions", { headers }),
      ]);
      setPets(resPets.data);
      setAdoptions(resAdoptions.data);
    } catch (err) {
      console.error("Gagal ambil data:", err);
    }
  };

  const handleDelete = async (id) => {
    await api.delete(`/pets/${id}`, { headers });
    getAllData();
  };

  const handleEdit = (pet) => {
    setEditingPet(pet);
    setForm({
      name: pet.name,
      type: pet.type,
      age: pet.age,
      description: pet.description,
    });
    setImageFile(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    Object.entries(form).forEach(([key, value]) => formData.append(key, value));
    if (imageFile) formData.append("image", imageFile);

    try {
      if (editingPet) {
        await api.put(`/pets/${editingPet.id}/`, formData, {
          headers: { ...headers, "Content-Type": "multipart/form-data" },
        });
      } else {
        await api.post("/pets/", formData, {
          headers: { ...headers, "Content-Type": "multipart/form-data" },
        });
      }

      setForm({ name: "", type: "", age: "", description: "" });
      setImageFile(null);
      setEditingPet(null);
      getAllData();
    } catch (err) {
      console.error("Submit gagal:", err);
    }
  };

  const updateAdoption = async (id, status) => {
    await api.put(`/adoptions/${id}`, { status }, { headers });
    getAllData();
  };

  const handleDeleteAdoption = async (id) => {
    await api.delete(`/adoptions/${id}`, { headers });
    getAllData();
  };


  if (!user?.token) {
    return (
      <div className="min-h-screen flex items-center justify-center text-center text-red-600 font-semibold">
        Mohon login ulang... (token kosong)
      </div>
    );
  }

  return (
    <>
    <Navbar/>
    <div className="p-6 bg-base-200 min-h-screen space-y-12">
      {/* FORM */}
      <section className="bg-base-100 p-6 rounded-xl shadow-md">
        <h2 className="text-2xl font-bold mb-4">
          {editingPet ? "Edit Hewan" : "Tambah Hewan Baru"}
        </h2>
        <form
          onSubmit={handleSubmit}
          className="grid grid-cols-1 md:grid-cols-2 gap-4"
        >
          <input
            className="input input-bordered"
            placeholder="Nama"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
          <input
            className="input input-bordered"
            placeholder="Jenis (kucing/anjing)"
            value={form.type}
            onChange={(e) => setForm({ ...form, type: e.target.value })}
            required
          />
          <input
            type="number"
            className="input input-bordered"
            placeholder="Umur"
            value={form.age}
            onChange={(e) => setForm({ ...form, age: e.target.value })}
            required
          />
          <input
            className="input input-bordered"
            placeholder="Deskripsi"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            required
          />
          <input
            type="file"
            accept="image/*"
            className="file-input file-input-bordered col-span-1 md:col-span-2"
            onChange={(e) => setImageFile(e.target.files[0])}
          />
          <button
            type="submit"
            className="btn btn-primary col-span-1 md:col-span-2"
          >
            {editingPet ? "Update" : "Tambah"}
          </button>
        </form>
      </section>

      {/* DAFTAR HEWAN */}
      <section>
        <h2 className="text-xl font-semibold mb-4">Daftar Hewan</h2>
        <div className="overflow-x-auto">
          <table className="table table-zebra w-full">
            <thead>
              <tr>
                <th>Foto</th>
                <th>Nama</th>
                <th>Jenis</th>
                <th>Umur</th>
                <th>Aksi</th>
              </tr>
            </thead>
            <tbody>
              {pets.map((pet) => (
                <tr key={pet.id}>
                  <td>
                    {pet.image_url ? (
                      <img
                        src={pet.image_url}
                        alt={pet.name}
                        className="w-16 h-16 object-cover rounded"
                      />
                    ) : (
                      "No Image"
                    )}
                  </td>
                  <td>{pet.name}</td>
                  <td>{pet.type}</td>
                  <td>{pet.age}</td>
                  <td className="flex flex-wrap gap-2">
                    <button
                      className="btn btn-sm btn-info"
                      onClick={() => handleEdit(pet)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-error"
                      onClick={() => handleDelete(pet.id)}
                    >
                      Hapus
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      {/* PENGAJUAN ADOPSI */}
      <section>
        <h2 className="text-xl font-semibold mb-4">Pengajuan Adopsi</h2>
        <div className="overflow-x-auto">
          <table className="table table-zebra w-full">
            <thead>
              <tr>
                <th>Hewan</th>
                <th>Pengguna</th>
                <th>Status</th>
                <th>Aksi</th>
              </tr>
            </thead>
            <tbody>
              {adoptions.map((a) => (
                <tr key={a.id}>
                  <td>{a.pet?.name || "-"}</td>
                  <td>{a.user?.username || "-"}</td>
                  <td>
                    <td>
                      <span
                        className={`badge px-3 py-1 text-sm capitalize ${
                          a.status === "approved"
                            ? "bg-green-500 text-white"
                            : a.status === "rejected"
                            ? "bg-red-500 text-white"
                            : "bg-blue-500 text-white"
                        }`}
                      >
                        {a.status}
                      </span>
                    </td>

                  </td>
                  <td className="flex flex-wrap gap-2">
                    <button
                      className="btn btn-sm btn-success"
                      onClick={() => updateAdoption(a.id, "approved")}
                    >
                      Approve
                    </button>
                    <button
                      className="btn btn-sm btn-warning"
                      onClick={() => updateAdoption(a.id, "rejected")}
                    >
                      Reject
                    </button>
                    <button
                      className="btn btn-sm btn-error"
                      onClick={() => handleDeleteAdoption(a.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
    </>
  );
}
