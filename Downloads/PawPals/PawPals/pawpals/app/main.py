from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from app.database import Base, engine
from app.routes import auth, pet, adoption

Base.metadata.create_all(bind=engine)

app = FastAPI()

# ✅ Tambahkan middleware CORS dengan benar
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],  # atau "*" pas dev
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ✅ Mount static files di luar add_middleware
app.mount("/uploads", StaticFiles(directory="uploads"), name="uploads")

# ✅ Include router
app.include_router(auth.router, prefix="/auth", tags=["Auth"])
app.include_router(pet.router, prefix="/pets", tags=["Pet"])
app.include_router(adoption.router, prefix="/adoptions", tags=["Adoption"])

